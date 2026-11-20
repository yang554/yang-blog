<template>
	<view class="example">
		<!-- 表单区域 -->
		<view class="form-container">
			<!-- 标题输入框 -->
			<view class="form-item">
				<view class="form-label-wrapper">
					<text class="form-label">标题</text>
					<text class="word-count">{{ formData.title.length }}/50</text>
				</view>
				<input class="form-input" v-model="formData.title" placeholder="请输入标题" maxlength="50"
					placeholder-style="color: #aaa;" />
			</view>

			<!-- 描述文本域 -->
			<view class="form-item">
				<view class="form-label-wrapper">
					<text class="form-label">描述</text>
					<text class="word-count">{{ formData.description.length }}/200</text>
				</view>
				<textarea class="form-textarea" v-model="formData.description" placeholder="请输入描述或简介" maxlength="200"
					placeholder-style="color: #aaa;" />
			</view>

			<!-- 标签和类型行 -->
			<view class="form-row">
				<view class="form-col">
					<text class="form-label">标签</text>
					<view class="tag-selector" @click="showTagPicker = true">
						<text v-if="formData.tags.length === 0" class="placeholder">选择标签</text>
						<view v-else class="selected-tags">
							<view v-for="(tag, index) in formData.tags" :key="tag.id" class="tag-item"
								@click.stop="removeTag(index)">
								<text>{{ tag.name }}</text>
								<text class="tag-remove">×</text>
							</view>
						</view>
						<uni-icons type="arrowdown" size="16" color="#999"></uni-icons>
					</view>
				</view>

				<view class="form-col">
					<text class="form-label">类型</text>
					<picker class="type-picker" mode="selector" :range="typeOptions" :range-key="'name'"
						@change="onTypeChange">
						<view class="picker-content">
							<text v-if="formData.type" class="selected-type">{{ formData.type.name }}</text>
							<text v-else class="placeholder">选择类型</text>
							<uni-icons type="arrowdown" size="16" color="#999"></uni-icons>
						</view>
					</picker>
				</view>
			</view>

			<!-- 封面上传 -->
			<view class="form-item">
				<text class="form-label">封面</text>
				<view class="cover-uploader">
					<view v-if="formData.cover" class="cover-preview">
						<image :src="getFullImageUrl(formData.cover)" class="cover-image" mode="aspectFill" />
						<view class="cover-actions">
							<view class="cover-action-btn" @click="rechooseCover">
								<text class="iconfont icon-fuwei2"></text>
							</view>
							<view class="cover-action-btn delete" @click="removeCover">
								<text class="iconfont icon-shanchu"></text>
							</view>
						</view>
					</view>
					<view v-else class="cover-placeholder" @click="chooseCover">
						<text class="iconfont icon-xinxifabu"></text>
						<text class="placeholder-text">上传封面</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 内容编辑区域 -->
		<view class="editor-wrapper">
			<view class="editor-header">
				<text class="editor-title">内容编辑</text>
				<view class="editor-format-buttons">
					<button class="format-btn" @click="insertCodeBlock">代码块</button>
					<button class="format-btn" @click="insertLink">链接</button>
				</view>
			</view>

			<!-- 替换为 textarea -->
			<view class="editor-container">
				<textarea class="content-textarea" v-model="formData.content" placeholder="请输入内容..." maxlength="10000"
					auto-height @input="onContentInput" placeholder-style="color: #999;"></textarea>
				<view class="word-count-indicator">
					<text class="count-text">{{ formData.content.length }}/10000</text>
				</view>
			</view>

			<!-- 代码块预览（如果有代码块） -->
			<view v-if="hasCodeBlocks" class="code-preview-section">
				<text class="preview-title">代码预览</text>
				<view class="code-preview-container">
					<scroll-view scroll-y class="code-preview-scroll">
						<view v-for="(codeBlock, index) in codeBlocks" :key="index" class="code-preview-item">
							<view class="code-header">
								<text class="code-lang">代码块 {{ index + 1 }}</text>
							</view>
							<view class="code-content">
								<text class="code-text">{{ codeBlock }}</text>
							</view>
						</view>
					</scroll-view>
				</view>
			</view>
		</view>

		<!-- 标签选择弹窗 -->
		<uni-popup ref="tagPopup" type="bottom" @maskClick="showTagPicker = false">
			<view class="tag-popup">
				<view class="popup-header">
					<text class="popup-title">选择标签</text>
					<text class="popup-subtitle">最多选择3个</text>
					<view class="popup-actions">
						<text class="popup-action" @click="clearTags">清空</text>
						<text class="popup-action confirm" @click="showTagPicker = false">完成</text>
					</view>
				</view>

				<view class="tag-list">
					<view v-for="tag in allTags" :key="tag.id" class="tag-option"
						:class="{ selected: isTagSelected(tag), disabled: formData.tags.length >= 3 && !isTagSelected(tag) }"
						@click="toggleTag(tag)">
						<text>{{ tag.name }}</text>
						<view v-if="isTagSelected(tag)" class="check-icon">
							<uni-icons type="checkmarkempty" size="14" color="#fff"></uni-icons>
						</view>
					</view>
				</view>
			</view>
		</uni-popup>

		<!-- 底部操作按钮 -->
		<view class="action-bar">
			<view class="action-buttons">
				<button class="action-btn cancel" @click="resetForm">重置</button>
				<button class="action-btn publish" @click="onSubmit">{{ btnTitle }}</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		blogApi,
		typeApi,
		tagApi
	} from "@/api/index.js";
	import {
		IMG_URL,
		BASE_URL
	} from '@/api/config.js';

	export default {
		data() {
			return {
				isEdit: false,
				editId: null,
				btnTitle: '添加',
				// 表单数据
				formData: {
					title: '',
					description: '',
					content: '',
					tags: [],
					type: null,
					cover: ''
				},
				allTags: [],
				typeOptions: [],
				showTagPicker: false,
				userInfo: uni.getStorageSync('userInfo')
			}
		},
		computed: {
			// 检查内容中是否有代码块
			hasCodeBlocks() {
				const codeBlockRegex = /```[\s\S]*?```/g;
				return codeBlockRegex.test(this.formData.content);
			},
			// 提取所有代码块
			codeBlocks() {
				const codeBlockRegex = /```(?:\w+)?\n?([\s\S]*?)\n?```/g;
				const matches = [];
				let match;
				while ((match = codeBlockRegex.exec(this.formData.content)) !== null) {
					matches.push(match[1].trim());
				}
				return matches;
			}
		},
		watch: {
			showTagPicker(val) {
				if (val) {
					this.$refs.tagPopup.open()
				} else {
					this.$refs.tagPopup.close()
				}
			}
		},
		onLoad(options) {
			if (options.bid) {
				this.btnTitle = '保存';
				this.isEdit = true;
				this.editId = options.bid;
				this.loadBlogData(options.bid);
			}
			this.loadType();
			this.loadTags();
		},
		methods: {
			// 加载博客数据（编辑时使用）
			async loadBlogData(id) {
				try {
					uni.showLoading({
						title: '加载中...'
					});
					// 调用API获取博客详情
					const res = await blogApi.getBlogById(id);

					if (res.status === 200 && res.data) {
						const blogData = res.data;

						// 填充表单数据
						this.formData = {
							title: blogData.title || '',
							description: blogData.description || '',
							content: blogData.content || '', // 直接使用markdown内容
							tags: blogData.blogTags || [],
							type: {
								id: blogData.typeid,
								name: blogData.tName
							} || null,
							cover: blogData.cover || ''
						};
					} else {
						uni.showToast({
							title: '加载数据失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error("加载博客数据失败:", error);
					uni.showToast({
						title: '加载数据失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},
			async loadType() {
				try {
					const res = await typeApi._typeAll();
					if (res.status === 200) {
						this.typeOptions = res.data.map(type => ({
							id: type.id,
							name: type.name || '未命名分类',
						}));
					}
				} catch (error) {
					console.error("加载分类失败:", error);
					uni.showToast({
						title: '加载分类失败',
						icon: 'none'
					});
				}
			},
			async loadTags() {
				try {
					const res = await tagApi._getTagAll();
					if (res.status === 200) {
						this.allTags = res.data.map(tag => ({
							id: tag.id,
							name: tag.name || '未命名标签'
						}));
					}
				} catch (error) {
					console.error("加载标签失败:", error);
					uni.showToast({
						title: '加载标签失败',
						icon: 'none'
					});
				}
			},
			onTypeChange(e) {
				const index = e.detail.value
				this.formData.type = this.typeOptions[index]
			},
			toggleTag(tag) {
				const index = this.formData.tags.findIndex(t => t.id === tag.id)
				if (index > -1) {
					this.formData.tags.splice(index, 1)
				} else {
					if (this.formData.tags.length >= 3) {
						uni.showToast({
							title: '最多只能选择3个标签',
							icon: 'none'
						})
						return
					}
					this.formData.tags.push(tag)
				}
			},
			clearTags() {
				this.formData.tags = [];
			},
			removeTag(index) {
				this.formData.tags.splice(index, 1)
			},
			isTagSelected(tag) {
				return this.formData.tags.some(t => t.id === tag.id)
			},
			chooseCover() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFilePath = res.tempFilePaths[0]
						uni.showLoading({
							title: '上传中...'
						})
						const uploadUrl = BASE_URL + '/file/upload';
						// 上传图片
						uni.uploadFile({
							url: uploadUrl,
							filePath: tempFilePath,
							name: 'file',
							header: {
								'Authorization': 'Bearer ' + uni.getStorageSync('token')
							},
							success: (uploadRes) => {
								const data = JSON.parse(uploadRes.data)
								if (data.status === 200) {
									uni.showToast({
										title: '上传成功',
										icon: 'success'
									})
									this.formData.cover = data.data;
								} else {
									uni.showToast({
										title: data.message || '上传失败',
										icon: 'none'
									})
								}
							},
							fail: (err) => {
								console.log('上传失败', err)
								uni.showToast({
									title: '上传失败',
									icon: 'none'
								})
							}
						})
					}
				})
			},
			rechooseCover() {
				this.chooseCover();
			},
			removeCover() {
				uni.showModal({
					title: '提示',
					content: '确定要移除封面吗？',
					success: ({
						confirm
					}) => {
						if (confirm) {
							this.formData.cover = ''
						}
					}
				})
			},

			// 插入代码块
			insertCodeBlock() {
				const codeBlock = "```\n// 在这里输入你的代码\n```";
				this.insertTextAtCursor('\n' + codeBlock + '\n');
			},

			// 插入链接
			insertLink() {
				uni.showModal({
					title: '插入链接',
					editable: true,
					content: '请输入链接地址',
					placeholderText: 'https://example.com',
					success: (res) => {
						if (res.confirm && res.content) {
							uni.showModal({
								title: '链接文本',
								editable: true,
								content: '请输入链接文本',
								placeholderText: '点击这里',
								success: (res2) => {
									if (res2.confirm) {
										const link = `[${res2.content || '链接'}](${res.content})`;
										this.insertTextAtCursor(link);
									}
								}
							});
						}
					}
				});
			},

			// 在光标位置插入文本
			insertTextAtCursor(startText, endText = '') {
				const currentContent = this.formData.content;
				// 这里需要一个实际的DOM操作，但由于uniapp的限制，我们直接插入到文本中
				// 实际应用中可能需要使用更复杂的光标处理
				this.formData.content = currentContent + startText + endText;
			},

			// 内容输入处理
			onContentInput(e) {
				// 可以在这里添加实时预览或其他处理
			},

			validateForm() {
				if (!this.formData.title.trim()) {
					uni.showToast({
						title: '请输入标题',
						icon: 'none'
					})
					return false
				}

				if (!this.formData.content.trim()) {
					uni.showToast({
						title: '请输入内容',
						icon: 'none'
					})
					return false
				}

				if (this.formData.tags.length === 0) {
					uni.showToast({
						title: '请至少选择一个标签',
						icon: 'none'
					})
					return false
				}

				if (!this.formData.type) {
					uni.showToast({
						title: '请选择类型',
						icon: 'none'
					})
					return false
				}
				return true
			},

			async onSubmit() {
				if (!this.validateForm()) return

				uni.showLoading({
					title: '发布中...'
				})

				try {
					const submitData = {
						...this.formData,
						content: this.formData.content, // 直接使用markdown内容
						blogTags: this.formData.tags.map(tag => tag.id),
						typeid: this.formData.type.id.toString(),
						userid: this.userInfo.id,
						id: this.editId,
						issue: true,
						ispublic: true,
						isoriginal: true
					}

					if (this.isEdit) {
						submitData.tags = submitData.blogTags
						const res = await blogApi.saveBlog(submitData)
						if (res.status === 200) {
							setTimeout(() => {
								uni.hideLoading()
								uni.showToast({
									title: '保存成功',
									icon: 'success'
								})
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							}, 1500)
						}
					} else {
						const res = await blogApi.addBlog(submitData)
						if (res.status === 200) {
							setTimeout(() => {
								uni.hideLoading()
								uni.showToast({
									title: '添加成功',
									icon: 'success'
								})
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							}, 1500)
						}
					}

				} catch (error) {
					uni.hideLoading()
					uni.showToast({
						title: '发布失败',
						icon: 'error'
					})
					console.error('发布失败:', error)
				}
			},

			resetForm() {
				uni.showModal({
					title: '确认重置',
					content: '确定要清空所有内容吗？',
					success: ({
						confirm
					}) => {
						if (confirm) {
							this.formData = {
								title: '',
								description: '',
								content: '',
								tags: [],
								type: null,
								cover: ''
							}
							uni.showToast({
								title: '已重置',
								icon: 'success'
							})
						}
					}
				})
			},

			// 图片URL处理
			getFullImageUrl(path) {
				if (!path) return "/static/default-knowledge.png";
				if (path.startsWith("http")) return path;
				return IMG_URL + (path.startsWith("/") ? path : "/" + path);
			}
		}
	}
</script>

<style lang="scss">
	.example {
		box-sizing: border-box;
		min-height: 100vh;
		background: #f5f7fa;
		display: flex;
		flex-direction: column;

		/* 表单容器 */
		.form-container {
			background: #fff;
			padding: 30rpx 32rpx;
			margin: 20rpx;
			border-radius: 16rpx;
			box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
		}

		.form-item {
			margin-bottom: 40rpx;
		}

		.form-row {
			display: flex;
			gap: 30rpx;
			margin-bottom: 40rpx;
		}

		.form-col {
			flex: 1;
		}

		.form-label-wrapper {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 16rpx;
		}

		.form-label {
			font-size: 30rpx;
			font-weight: 500;
			color: #333;
			line-height: 1;

			&::after {
				content: '*';
				color: #ff4444;
				margin-left: 6rpx;
			}
		}

		.form-input {
			border: 2rpx solid #e8e8e8;
			border-radius: 12rpx;
			padding: 24rpx;
			font-size: 28rpx;
			background: #fff;
			transition: border-color 0.3s;

			&:focus {
				border-color: #1890ff;
			}
		}

		.form-textarea {
			border: 2rpx solid #e8e8e8;
			border-radius: 12rpx;
			padding: 24rpx;
			font-size: 28rpx;
			height: 180rpx;
			background: #fff;
			transition: border-color 0.3s;

			&:focus {
				border-color: #1890ff;
			}
		}

		.word-count {
			font-size: 26rpx;
			color: #999;
		}

		/* 标签选择器 */
		.tag-selector,
		.type-picker .picker-content {
			border: 2rpx solid #e8e8e8;
			border-radius: 12rpx;
			padding: 24rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;
			background: #fff;
			min-height: 88rpx;
		}

		.selected-type {
			font-size: 28rpx;
			color: #333;
		}

		.placeholder {
			color: #aaa;
			font-size: 28rpx;
		}

		.selected-tags {
			display: flex;
			flex-wrap: wrap;
			gap: 16rpx;
			flex: 1;
		}

		.tag-item {
			background: linear-gradient(135deg, #1890ff, #52c41a);
			border-radius: 40rpx;
			padding: 12rpx 24rpx;
			display: flex;
			align-items: center;
			gap: 8rpx;
			font-size: 26rpx;
			color: #fff;
			box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.2);
		}

		.tag-remove {
			color: #fff;
			font-size: 32rpx;
			line-height: 1;
			margin-left: 4rpx;
			opacity: 0.8;
		}

		/* 封面上传 */
		.cover-uploader {
			.cover-preview {
				position: relative;
				width: 300rpx;
				height: 200rpx;
				border-radius: 12rpx;
				overflow: hidden;
				box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);

				.cover-image {
					width: 100%;
					height: 100%;
				}

				.cover-actions {
					position: absolute;
					right: 12rpx;
					top: 12rpx;
					display: flex;
					gap: 12rpx;
				}

				.cover-action-btn {
					width: 56rpx;
					height: 56rpx;
					border-radius: 50%;
					background: rgba(0, 0, 0, 0.5);
					display: flex;
					align-items: center;
					justify-content: center;
					backdrop-filter: blur(10rpx);

					&.delete {
						background: rgba(255, 68, 68, 0.7);
					}
				}
			}

			.cover-placeholder {
				width: 300rpx;
				height: 200rpx;
				border: 2rpx dashed #1890ff;
				border-radius: 12rpx;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				color: #1890ff;
				font-size: 28rpx;
				background: rgba(24, 144, 255, 0.05);
				transition: all 0.3s;

				&:active {
					background: rgba(24, 144, 255, 0.1);
				}
			}
		}

		/* 编辑器区域 */
		.editor-wrapper {
			flex: 1;
			background: #fff;
			margin: 20rpx;
			border-radius: 16rpx;
			overflow: hidden;
			box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
			display: flex;
			flex-direction: column;
		}

		.editor-header {
			padding: 24rpx 32rpx;
			border-bottom: 2rpx solid #f0f0f0;
			display: flex;
			justify-content: space-between;
			align-items: center;
			flex-wrap: wrap;
			gap: 20rpx;
		}

		.editor-title {
			font-size: 32rpx;
			font-weight: 600;
			color: #333;
		}

		.editor-format-buttons {
			display: flex;
			gap: 16rpx;
			flex-wrap: wrap;
		}

		.format-btn {
			padding: 8rpx 24rpx;
			font-size: 26rpx;
			background: #f0f2f5;
			color: #666;
			border-radius: 20rpx;
			border: none;

			&:active {
				background: #e8e8e8;
			}
		}

		/* 文本区域样式 */
		.editor-container {
			flex: 1;
			position: relative;
			padding: 32rpx;
		}

		.content-textarea {
			width: 100%;
			min-height: 400rpx;
			font-size: 28rpx;
			line-height: 1.6;
			color: #333;
			border: none;
			background: transparent;
			resize: none;

			&:focus {
				outline: none;
			}
		}

		.word-count-indicator {
			position: absolute;
			bottom: 10rpx;
			right: 32rpx;
		}

		.count-text {
			font-size: 24rpx;
			color: #999;
		}

		/* 代码预览区域 */
		.code-preview-section {
			margin: 0 32rpx 32rpx;
			border-top: 2rpx solid #f0f0f0;
			padding-top: 32rpx;
		}

		.preview-title {
			font-size: 30rpx;
			font-weight: 600;
			color: #333;
			margin-bottom: 20rpx;
			display: block;
		}

		.code-preview-container {
			background: #f8f9fa;
			border-radius: 12rpx;
			overflow: hidden;
			max-height: 400rpx;
		}

		.code-preview-scroll {
			max-height: 400rpx;
		}

		.code-preview-item {
			background: #fff;
			border-radius: 8rpx;
			margin-bottom: 16rpx;
			overflow: hidden;
			border: 1rpx solid #e8e8e8;
		}

		.code-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 16rpx 24rpx;
			background: #f5f7fa;
			border-bottom: 1rpx solid #e8e8e8;
		}

		.code-lang {
			font-size: 26rpx;
			color: #666;
			font-weight: 500;
		}

		.copy-btn {
			padding: 6rpx 20rpx;
			font-size: 24rpx;
			background: #1890ff;
			color: #fff;
			border-radius: 16rpx;
			border: none;

			&:active {
				background: #096dd9;
			}
		}

		.code-content {
			padding: 20rpx 24rpx;
			font-family: 'Monaco', 'Consolas', monospace;
			font-size: 26rpx;
			line-height: 1.5;
			color: #333;
			background: #f8f9fa;
			overflow-x: auto;
		}

		.code-text {
			white-space: pre-wrap;
			word-break: break-all;
		}

		/* 底部操作栏 */
		.action-bar {
			background: #fff;
			padding: 24rpx 32rpx;
			border-top: 2rpx solid #f0f0f0;
			position: sticky;
			bottom: 0;
			z-index: 50;
		}

		.action-buttons {
			display: flex;
			gap: 24rpx;
		}

		.action-btn {
			flex: 1;
			height: 80rpx;
			line-height: 80rpx;
			font-size: 30rpx;
			font-weight: 500;
			border-radius: 40rpx;
			border: none;

			&.cancel {
				background: #f5f7fa;
				color: #666;
			}

			&.save {
				background: #1890ff;
				color: #fff;
			}

			&.publish {
				background: linear-gradient(135deg, #52c41a, #1890ff);
				color: #fff;
				box-shadow: 0 4rpx 20rpx rgba(82, 196, 26, 0.3);
			}
		}

		/* 标签选择弹窗 */
		.tag-popup {
			background: #fff;
			border-radius: 32rpx 32rpx 0 0;
			padding: 32rpx;
			max-height: 70vh;
			display: flex;
			flex-direction: column;
			position: relative;
			z-index: 100;

			.popup-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 40rpx;
				padding-bottom: 24rpx;
				border-bottom: 2rpx solid #f0f0f0;
				flex-shrink: 0;
				/* 防止头部被压缩 */
			}

			.popup-title {
				font-size: 36rpx;
				font-weight: 600;
				color: #333;
			}

			.popup-subtitle {
				font-size: 28rpx;
				color: #999;
				margin-left: 20rpx;
			}

			.popup-actions {
				display: flex;
				gap: 40rpx;
			}

			.popup-action {
				font-size: 30rpx;
				color: #666;

				&.confirm {
					color: #1890ff;
					font-weight: 500;
				}
			}

			/* 标签列表容器 - 添加滚动 */
			.tag-list {
				display: grid;
				grid-template-columns: repeat(3, 1fr);
				gap: 24rpx;
				padding: 0 8rpx;
				overflow-y: auto;
				max-height: 500rpx;
				flex: 1;

				&::-webkit-scrollbar {
					width: 8rpx;
				}

				&::-webkit-scrollbar-track {
					background: #f5f5f5;
					border-radius: 4rpx;
				}

				&::-webkit-scrollbar-thumb {
					background: #c1c1c1;
					border-radius: 4rpx;
				}

				&::-webkit-scrollbar-thumb:hover {
					background: #a8a8a8;
				}
			}

			.tag-option {
				border: 2rpx solid #e8e8e8;
				border-radius: 12rpx;
				padding: 28rpx 20rpx;
				text-align: center;
				font-size: 28rpx;
				color: #666;
				display: flex;
				align-items: center;
				justify-content: center;
				position: relative;
				transition: all 0.3s;
				min-height: 30rpx;
				/* 设置最小高度 */

				&.selected {
					border-color: #1890ff;
					background: linear-gradient(135deg, #1890ff, #52c41a);
					color: #fff;
					box-shadow: 0 4rpx 16rpx rgba(24, 144, 255, 0.2);
				}

				&.disabled {
					opacity: 0.5;
					cursor: not-allowed;
				}

				.check-icon {
					position: absolute;
					top: 12rpx;
					right: 12rpx;
					width: 32rpx;
					height: 32rpx;
					border-radius: 50%;
					background: #fff;
					display: flex;
					align-items: center;
					justify-content: center;
				}
			}
		}
		
	}
</style>