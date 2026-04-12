<template>
  <text
    :eid="eid"
    :change:eid="quillEditor.watchEID"
    :sid="sid"
    :change:sid="quillEditor.watchStartID"
    :video="videoUrl"
    :change:pastemode="quillEditor.watchPasteMode"
    :pastemode="pastemode"
    :change:video="quillEditor.watchVideoUrl"
    :cover="coverUrl"
    :change:cover="quillEditor.watchCoverUrl"
    :coverios="coverUrlIOS"
    :change:coverios="quillEditor.watchCoverUrlIOS"
  ></text>
</template>

<script>
/**
 * 富文本plugin特殊扩展
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-14
 */

export default {
  props: {
    sid: {
      type: String,
      default: ''
    },
    eid: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      videoUrl: '', // 视频地址
      coverUrl: '', // 封面地址
      coverUrlIOS: '', // ios端封面地址
      pastemode: 'text' // 粘贴模式 text | origin
    }
  },
  mounted() {},
  methods: {
    changePasteMode(e) {
      this.pastemode = e
    },
    editorPaste(e) {
      this.$emit('epaste', e)
    },
    createVideoThumbnail(url) {
      this.videoUrl = url
    },
    getVideoThumbnail(e) {
      // e: { video, cover }
      uni.$emit(`E_EDITOR_GET_VIDEO_THUMBNAIL_${e.video}`, e)
    },
    createCoverThumbnail(url) {
      // #ifdef H5
      this.coverUrl = url
      // #endif
      // #ifdef APP
      const isIOS = uni.getSystemInfoSync().platform == 'ios'
      if (isIOS) {
        this.coverUrlIOS = url // iOS用不了OffscreenCanvas
      } else {
        this.coverUrl = url
      }
      // #endif
    },
    getCoverThumbnail(e) {
      // e: { image, cover }
      uni.$emit(`E_EDITOR_GET_COVER_THUMBNAIL_${e.image}`, e)
    }
  }
}
</script>

<script module="quillEditor" lang="renderjs">
import config from '../common/config.js'

export default {
  data() {
		return {
			editorID: '',
			idStack: [], // sid栈
      matcherMode: '' // 粘贴模式 text | origin
		}
  },
  methods: {
    /**
     * 注意：watch频繁触发时需要异步修改，否则可能会导致监听不到
     */
		watchPasteMode(newValue, oldValue, ownerInstance, instance) {
			if (newValue) {
				this.matcherMode = newValue
			}
		},
		watchStartID(newValue, oldValue, ownerInstance, instance) {
			if (newValue) {
				this.addMatcher(newValue)
			}
		},
		watchEID(newValue, oldValue, ownerInstance, instance) {
			if (newValue) {
				this.editorID = newValue
			}
		},
		watchVideoUrl(newValue, oldValue, ownerInstance, instance) {
		  if (newValue) {
		    this.generateVideoThumbnail(newValue).then((res) => {
					this.$ownerInstance.callMethod('getVideoThumbnail', {
            video: newValue,
            cover: res
          })
				})
		  }
		},
		watchCoverUrl(newValue, oldValue, ownerInstance, instance) {
		  if (newValue) {
		    this.generateCoverThumbnail(newValue).then((res) => {
					this.$ownerInstance.callMethod('getCoverThumbnail', {
            image: newValue,
            cover: res
          })
				})
		  }
		},
    /**
     * Only Apple Can Do !!!
     */
		watchCoverUrlIOS(newValue, oldValue, ownerInstance, instance) {
		  if (newValue) {
		    this.generateCoverThumbnailIOS(newValue).then((res) => {
					this.$ownerInstance.callMethod('getCoverThumbnail', {
            image: newValue,
            cover: res
          })
				})
		  }
		},
		/**
		 * 保留格式粘贴内容
		 * @description 此方式尽可能保留原格式，易于再编辑，但是部分格式会丢失
		 * @param {String} sid 当前编辑器id
		 */
		addMatcher(sid) {
			if(this.idStack.includes(sid)) return // 禁止重复添加Matcher
			this.idStack.push(sid)

			const el = document.querySelector(`#${sid}`);
			const quill = Quill.find(el);

			const getStyleAttributes = (node, style) => {
				let attributes = {}

				// node属性
				const width = node.getAttribute('width');
				const height = node.getAttribute('height');
				if (width) attributes.width = width
				if (height) attributes.height = height
				const dataCustom = node.getAttribute('data-custom');
				if (dataCustom) attributes['data-custom'] = dataCustom;

				// style样式
				if (style.textAlign) attributes.align = style.textAlign;
				if (style.fontWeight === 'bold' || style.fontWeight === '700') attributes.bold = true;
				if (style.fontStyle === 'italic') attributes.italic = true;
				if (style.textDecoration.includes('underline')) attributes.underline = true;
				if (style.textDecoration.includes('line-through')) attributes.strike = true;
				if (style.verticalAlign === 'super') attributes.script = 'super'
				if (style.verticalAlign === 'sub') attributes.script = 'sub'
				if (style.fontFamily) attributes.font = style.fontFamily;
				if (style.fontSize) attributes.size = parseFloat(style.fontSize);
				if (style.color) attributes.color = style.color;
				if (style.backgroundColor) attributes.background = style.backgroundColor;

				return attributes
			}

			const processNode = (node) => {
				let ops = [];

				if (node.nodeType === Node.ELEMENT_NODE) {
					const computedStyle = document.defaultView.getComputedStyle(node);

					// 处理 <img> 标签
					if (node.tagName === 'IMG') {
						const imgSrc = node.getAttribute('src');
						if (imgSrc) {
							ops.push({ insert: '\n' }); // 插入换行符，确保图片前有一个空行
							ops.push({
									insert: { image: imgSrc },
									attributes: getStyleAttributes(node, computedStyle)
							});
							ops.push({ insert: '\n' }); // 插入换行符，确保图片后有一个空行

							return ops; // 不参与递归
						}
					}

					// 处理 <p> 和 <div> 标签
					else if (node.tagName === 'P' || node.tagName === 'DIV') {
					  ops.push({ insert: '\n' }); // 插入换行符
					}

					// 处理 <ol> 标签 有序
					else if (node.tagName === 'OL') {
					  // ops.push({ insert: '\n', attributes: { list: 'ordered' } });
					}

					// 处理 <ul> 标签 无序
					else if (node.tagName === 'UL') {
					  // ops.push({ insert: '\n', attributes: { list: 'bullet' } });
					}

					// 处理 <li> 标签
					else if (node.tagName === 'LI') {
						ops.push({ insert: '\n' });
					}


					// 处理 <hr> 标签
					else if (node.tagName === 'HR') {
						ops.push({ insert: '\n' }); // 插入换行符
						ops.push({ insert: { divider: true } });

						return ops; // 不参与递归
          }

					// 处理 <a> 标签
					else if (node.tagName === 'A') {
						const href = node.getAttribute('href');
						const textContent = node.textContent.trim();

						if (href && textContent) {
							ops.push({
								insert: ' ' + textContent + ' ',
								attributes: {
									link: href,
									textDecoration: computedStyle.textDecoration,
										...getStyleAttributes(node, computedStyle)
									}
								});

							return ops; // 不参与递归
						}
					}

					// 处理 <h1> 到 <h6> 标题
					else if (['H1', 'H2', 'H3', 'H4', 'H5', 'H6'].includes(node.tagName)) {
						// 处理 <h1> 到 <h6> 标题，生成 header 操作
						const headerLevel = parseInt(node.tagName.charAt(1), 10);
						const textContent = node.textContent.trim();

						if (textContent) {
							ops.push({
								insert: textContent + '\n', // 必须要加上换行
								attributes: {
									header: headerLevel,
									...getStyleAttributes(node, computedStyle)
								}
							});

							return ops; // 不参与递归
						}
					}

					// 递归处理子节点
					for (let child of node.childNodes) {
							ops = ops.concat(processNode(child));
					}

				} else if (node.nodeType === Node.TEXT_NODE) {
					const textContent = node.nodeValue.trim();
					if (textContent) {
						// 从父元素获取样式
						const parentNode = node.parentNode;
						if (parentNode) {
							const computedStyle = document.defaultView.getComputedStyle(parentNode);
							ops.push({
								insert: textContent,
								attributes: getStyleAttributes(parentNode, computedStyle)
							});
						} else {
							// 如果没有父元素，直接插入文本
							ops.push({ insert: textContent });
						}
					}
				}

				return ops;
			}

			quill.clipboard.addMatcher(Node.ELEMENT_NODE, (node, delta) => {
                if (this.matcherMode == 'origin') {
                    let newOps = processNode(node);
                    if (newOps.length > 0) {
                        return { ops: newOps };
                    }
                }
				return delta;
			})

			const cleanClipboardHTML = (html, text) => {
				if(!html) return text
				// 使用正则表达式匹配 <!--StartFragment--> 和 <!--EndFragment--> 之间的内容
				const fragmentRegex = /<!--StartFragment-->([\s\S]*?)<!--EndFragment-->/;
				const match = html.match(fragmentRegex);
				if (match && match[1]) {
						// 返回匹配到的内容
						return match[1].trim();
				}
				// 如果没有匹配到片段内容，返回原始 HTML
				return html;
			}

			el.addEventListener('paste', (e) => {
				let clipboardText = e.clipboardData.getData('text/plain'); // 获取剪切板中的纯文本内容
				let clipboardHtml = e.clipboardData.getData('text/html'); // 获取剪切板中的 HTML 内容（如果存在）
				clipboardHtml = cleanClipboardHTML(clipboardHtml, clipboardText)

				setTimeout(() => {
					this.$ownerInstance.callMethod('editorPaste', {
						id: sid,
						text: clipboardText,
						html: clipboardHtml,
						range: quill.getSelection() // 获取当前光标位置
					})
				}, 100);
			});
		},
		/**
		 * 保留格式粘贴内容
		 * @description 此方式虽然可以极大程度保留格式，但是会导致粘贴下来的内容为一整个块，且不易再编辑
		 * @param {String} sid 当前编辑器id
		 */
		/*
		addMatcher(sid) {
			if(this.idStack.includes(sid)) return // 禁止重复添加Matcher
			this.idStack.push(sid)

			// 引入源码中的BlockEmbed
			const BlockEmbed = Quill.import('blots/block/embed');
			// 定义新的blot类型
			class AppPanelEmbed extends BlockEmbed {
				static create(value) {
					const node = super.create(value);
					node.setAttribute('width', '100%');
					//   设置自定义html
					node.innerHTML = this.transformValue(value)
					return node;
				}
				static transformValue(value) {
					let handleArr = value.split('\n')
					handleArr = handleArr.map(e => e.replace(/^[\s]+/, '').replace(/[\s]+$/, ''))
					return handleArr.join('')
				}
				// 返回节点自身的value值 用于撤销操作
				static value(node) {
					return node.innerHTML
				}
			}
			// blotName
			AppPanelEmbed.blotName = 'AppPanelEmbed';
			// 标签类型自定义
			AppPanelEmbed.tagName = 'p';
			Quill.register(AppPanelEmbed, true);

			const el = document.querySelector(`#${sid}`);
			const quill = Quill.find(el);

			const cleanClipboardHTML = (html, text) => {
				if(!html) return text
				// 使用正则表达式匹配 <!--StartFragment--> 和 <!--EndFragment--> 之间的内容
				const fragmentRegex = /<!--StartFragment-->([\s\S]*?)<!--EndFragment-->/;
				const match = html.match(fragmentRegex);

				if (match && match[1]) {
						// 返回匹配到的内容
						return match[1].trim();
				}

				// 如果没有匹配到片段内容，返回原始 HTML
				return html;
			}

			el.addEventListener('paste', (e) => {
				e.preventDefault();

				let clipboardText = e.clipboardData.getData('text/plain'); // 获取剪切板中的纯文本内容
				let clipboardHtml = e.clipboardData.getData('text/html'); // 获取剪切板中的 HTML 内容（如果存在）
				clipboardHtml = cleanClipboardHTML(clipboardHtml, clipboardText)

				this.$ownerInstance.callMethod('editorPaste', {
					id: sid,
					text: clipboardText,
					html: clipboardHtml
				})

				setTimeout(() => {
					const range = quill.getSelection(); // 获取当前光标位置
					quill.insertEmbed(range.index, 'AppPanelEmbed', clipboardHtml);
				}, 100);
			});
		},
		*/
		/**
		 * 生成视频封面图片（不兼容iOS）
		 * @property {String} videoUrl 视频地址
		 * @returns {Promise} 视频封面图片 注意异步处理
		 */
		async generateVideoThumbnail(videoUrl) {
			// 绘制播放按钮图标
			// @param {CanvasContext} context canvas上下文
			// @param {Canvas} canvas
			const drawPlayButton = (context, canvas) => {
				// 创建一个 <img> 元素来加载播放图标
				const img = new Image();
				img.src = config.video_playicon;

				// 等待图像加载完成
				return new Promise((resolve, reject) => {
					img.onload = () => {
						// 计算播放按钮的位置和大小
						// const playButtonSize = Math.min(canvas.width, canvas.height) * 0.15;
						const playButtonSize = canvas.width * 0.15;
						const playButtonX = (canvas.width - playButtonSize) / 2;
						const playButtonY = (canvas.height - playButtonSize) / 2;

						// 绘制播放按钮到 canvas
						context.drawImage(img, playButtonX, playButtonY, playButtonSize, playButtonSize);

						resolve();
					};

					img.onerror = (error) => {
						reject(new Error('Failed to load SVG image.'));
					};
				});
			}

			return new Promise(async (resolve, reject) => {
				try {
					// 创建一个新的 video 元素，并设置 crossOrigin 属性
					const video = document.createElement('video');
					video.crossOrigin = 'anonymous'; // 添加 crossOrigin 属性
					video.preload = 'metadata';
					video.src = videoUrl;

					// 创建一个新的 canvas 元素
					const canvas = document.createElement('canvas');
					const context = canvas.getContext('2d');

					// 监听视频加载元数据完成
					video.onloadedmetadata = async () => {
						// 设置 canvas 尺寸与视频相同
						canvas.width = video.videoWidth;
						canvas.height = video.videoHeight;

						// 尝试绘制视频的第一帧到 canvas
						video.currentTime = 0; // 确保我们从视频的第一帧开始
						video.onseeked = async () => {
							try {
								context.drawImage(video, 0, 0, canvas.width, canvas.height);

								// 绘制播放按钮
								await drawPlayButton(context, canvas);

								// 将 canvas 内容转换为 Data URL
								// resolve(canvas.toDataURL('image/png')); // base64太长了，不建议使用

								// 将 canvas 内容转换为 Blob 对象
								canvas.toBlob((blob) => {
									resolve(URL.createObjectURL(blob));
								}, 'image/png');

							} catch (error) {
								reject(new Error('Failed to draw image to canvas.'));
							}
						};

						// 如果 seek 操作没有成功，尝试直接绘制当前帧
						setTimeout(async () => {
							if (!video.seeking) {
								try {
									context.drawImage(video, 0, 0, canvas.width, canvas.height);
									// 绘制播放按钮
									await drawPlayButton(context, canvas);

									// 将 canvas 内容转换为 Data URL
									// resolve(canvas.toDataURL('image/png')); // base64太长了，不建议使用

									// 将 canvas 内容转换为 Blob 对象
									canvas.toBlob((blob) => {
										resolve(URL.createObjectURL(blob));
									}, 'image/png');

								} catch (error) {
									reject(new Error('Failed to draw image to canvas.'));
								}
							}
						}, 1000); // 等待1秒后尝试绘制，防止 seek 操作未完成
					};

					// 监听视频加载错误
					video.onerror = (error) => {
						// reject(new Error('Failed to load video or get metadata. PS: Maybe the browser cannot play videos.'));

						// 不直接抛出错误，而是抛出一个默认的封面图片，但是需要加以警告提示
						console.warn('Failed to load video or get metadata. PS: Maybe the browser cannot play videos.');
						resolve(config.video_thumbnail);
					};
				} catch (error) {
					// reject(error);
          // 不直接抛出错误，而是抛出一个默认的封面图片，但是需要加以警告提示
          console.warn(error)
          resolve(config.video_thumbnail);
				}
			});
		},
    /**
     * 生成封面图片，OffscreenCanvas方式（不兼容iOS）
     * @param {Object} coverUrl 封面图片地址
     * @returns {Promise}
     */
		async generateCoverThumbnail(coverUrl) {
      return new Promise((resolve, reject) => {

        // 内联 Worker 代码
        const workerCode = `
          self.onmessage = async function(e) {
            const { imageUrl, iconBase64 } = e.data;

            try {
              // 加载图片并创建 ImageBitmap
              let imgResponse = await fetch(imageUrl);
              if (!imgResponse.ok) {
                throw new Error(\`Failed to load image from \${imageUrl}: \${imgResponse.statusText}\`);
              }
              let imgBlob = await imgResponse.blob();
              let imgBitmap = await createImageBitmap(imgBlob);

              // 创建 OffscreenCanvas 并绘制图片
              const offscreen = new OffscreenCanvas(imgBitmap.width, imgBitmap.height);
              const ctx = offscreen.getContext('2d');
              ctx.drawImage(imgBitmap, 0, 0);

              // 加载图标并创建 ImageBitmap
              let iconResponse = await fetch(iconBase64);
              if (!iconResponse.ok) {
                throw new Error(\`Failed to load icon from \${iconBase64}: \${iconResponse.statusText}\`);
              }
              let iconBlob = await iconResponse.blob();
              let iconBitmap = await createImageBitmap(iconBlob);

              // 计算图标的中心位置并绘制
              const x = (imgBitmap.width - iconBitmap.width) / 2;
              const y = (imgBitmap.height - iconBitmap.height) / 2;
              ctx.drawImage(iconBitmap, x, y);

              // 获取处理后的图像数据
              const result = await offscreen.convertToBlob();

              // 发送结果回主线程
              self.postMessage(result);
            } catch (error) {
              console.error('Error processing image:', error.message);
              self.postMessage({ error: error.message });
            }
          };
        `

        // 创建 Blob
        const blob = new Blob([workerCode], { type: 'application/javascript' });

        // 创建一个指向 Blob 的 URL
        const workerUrl = URL.createObjectURL(blob);

        // 实例化 Worker
        const worker = new Worker(workerUrl);

        // 监听来自 Worker 的消息
        worker.onmessage = (e) => {
          if (e.data instanceof Blob) {
            resolve(URL.createObjectURL(e.data));
          } else {
            console.warn(e.data.error);
            // 不直接抛出错误，而是抛出一个默认的封面图片，但是需要加以警告提示
            resolve(config.video_thumbnail);
          }
          worker.terminate(); // 处理完成后终止 worker
        };

        // 向 Worker 发送消息
        worker.postMessage({ imageUrl: coverUrl, iconBase64: config.video_playicon });
      });
		},
    /**
     * 生成封面图片，普通方式，可能影响性能（兼容iOS）
     * @param {Object} coverUrl 封面图片地址
     * @returns {Promise}
     */
    async generateCoverThumbnailIOS(coverUrl){
      return new Promise(async (resolve, reject) => {
          try {
            // 创建 Image 对象并加载封面图片
            const img = new Image();
            img.src = coverUrl;
            await new Promise(resolve => img.onload = resolve);

            // 创建 Canvas 并绘制封面图片
            const canvas = document.createElement('canvas');
            canvas.width = img.width;
            canvas.height = img.height;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0);

            // 加载播放按钮图标并绘制
            const icon = new Image();
            icon.src = config.video_playicon; // 假设这是播放按钮图标的 URL
            await new Promise(resolve => icon.onload = resolve);

            // 计算播放按钮的位置和大小
            // const playButtonSize = Math.min(canvas.width, canvas.height) * 0.15;
            const playButtonSize = canvas.width * 0.15;
            const playButtonX = (canvas.width - playButtonSize) / 2;
            const playButtonY = (canvas.height - playButtonSize) / 2;

            // 确保播放按钮图标按比例缩放
            const iconAspectRatio = icon.width / icon.height;
            const iconWidth = playButtonSize;
            const iconHeight = iconWidth / iconAspectRatio;

            // 绘制播放按钮图标到 Canvas
            ctx.drawImage(icon, playButtonX, playButtonY, iconWidth, iconHeight);

            // 将 canvas 内容转换为 Blob 对象
            canvas.toBlob((blob) => {
            	resolve(URL.createObjectURL(blob));
            }, 'image/png');

          } catch (error) {
            // iOS Safari 的安全策略通常比其他浏览器更严格，本地file://协议也会导致跨域
            console.warn('iOS createCoverThumbnail error :', error);
            // reject(error);
            // 不直接抛出错误，而是抛出一个默认的封面图片，但是需要加以警告提示
            resolve(config.video_thumbnail);
          }
      })
    },
	}
}
</script>
