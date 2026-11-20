<template>
    <div class="write-blog-view">
        <div class="header">
            <h1><i class="el-icon-edit"></i> 撰写新文章</h1>
            <p class="subtitle">分享你的知识与见解</p>
        </div>

        <el-card class="form-container">
            <el-form ref="editForm" status-icon label-width="100px" label-position="top" :model="editForm" :rules="rules1"
                @submit.prevent>
                <div class="form-grid">
                    <!-- 左侧表单 -->
                    <div class="form-left">
                        <el-form-item label="文章标题" prop="title">
                            <el-input v-model="editForm.title" placeholder="输入引人入胜的标题..." size="large" class="title-input">
                                <template #prefix>
                                    <i class="el-icon-document"></i>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item label="文章摘要" prop="description">
                            <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="简要描述文章内容..."
                                show-word-limit maxlength="200" resize="none">
                            </el-input>
                        </el-form-item>

                        <el-form-item label="封面图片" class="cover-upload">
                            <div class="upload-tip">
                                <i class="el-icon-picture"></i> 建议尺寸：1200×630像素
                            </div>
                            <el-upload ref="upload" :on-success="handleFileSuccess" accept=".jpg,.png,.bmp,.jpeg,.gif"
                                action="/api/file/upload" list-type="picture-card" :auto-upload="true"
                                :on-change="handleChange" :limit='1' :on-remove="handleRemove">
                                <i class="el-icon-plus avatar-uploader-icon"></i>
                                <template #file="{ file }">
                                    <div class="cover-preview">
                                        <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                                        <span class="el-upload-list__item-actions">
                                            <span class="el-upload-list__item-preview"
                                                @click="handlePictureCardPreview(file)">
                                                <i class="el-icon-zoom-in"></i>
                                            </span>
                                            <span class="el-upload-list__item-delete" @click="handleRemove(file)">
                                                <i class="el-icon-delete"></i>
                                            </span>
                                        </span>
                                    </div>
                                </template>
                            </el-upload>
                            <el-dialog :visible.sync="dialogVisible">
                                <img width="100%" :src="dialogImageUrl" alt="">
                            </el-dialog>
                        </el-form-item>
                    </div>

                    <!-- 右侧表单 -->
                    <div class="form-right">
                        <el-form-item label="文章标签" prop="blogTags">
                            <el-select :multiple-limit="3" v-model="editForm.blogTags" multiple placeholder="选择标签（最多3个）"
                                class="tag-selector" size="medium">
                                <el-option v-for="(item, index) in tagList" :key="index" :label="item.name"
                                    :value="item.id">
                                    <el-tag :type="tagColors[index % tagColors.length]">{{ item.name }}</el-tag>
                                </el-option>
                            </el-select>
                        </el-form-item>

                        <el-form-item label="分类专栏" prop="typeid">
                            <el-select v-model="editForm.typeid" placeholder="选择文章分类" size="medium"
                                class="category-selector">
                                <el-option v-for="(item, index) in typeList" :key="index" :label="item.name"
                                    :value="item.id">
                                    <div class="category-option">
                                        <i :class="categoryIcons[index % categoryIcons.length]"></i>
                                        <span>{{ item.name }}</span>
                                    </div>
                                </el-option>
                            </el-select>
                        </el-form-item>

                        <div class="publish-switch">
                            <el-form-item label="文章类型">
                                <el-switch v-model="editForm.isoriginal" active-color="#409eff" inactive-color="#909399"
                                    active-text="原创" inactive-text="转载" size="large">
                                </el-switch>
                            </el-form-item>
                        </div>


                        <div class="publish-switch">
                            <el-form-item label="发布形式">
                                <el-switch v-model="editForm.ispublic" active-color="#409eff" inactive-color="#909399"
                                    active-text="公开" inactive-text="私密" size="large">
                                </el-switch>
                            </el-form-item>
                        </div>

                        <div class="publish-switch">
                            <el-form-item label="发布状态">
                                <el-switch v-model="editForm.issue" active-color="#409eff" inactive-color="#909399"
                                    active-text="发布文章" inactive-text="保存草稿" size="large">
                                </el-switch>
                            </el-form-item>
                        </div>
                    </div>
                </div>

                <el-form-item label="文章内容" prop="content">
                    <mavon-editor ref="md" :autofocus="false" placeholder="开始撰写你的内容..." :boxShadow="false" @imgAdd="handleMavonEditorImgAdd"
                        @imgDel="handleMavonEditorImgDel" class="markdown-editor" v-model="editForm.content" />
                </el-form-item>

                <el-form-item class="action-buttons">
                    <el-button type="info" @click="reset" size="large" class="btn-reset">
                        <i class="el-icon-delete"></i> 重置内容
                    </el-button>
                    <el-button type="primary" @click="submitBlogForm" size="large" class="btn-submit">
                        <i class="el-icon-s-promotion"></i> {{ editForm.issue ? '立即发布' : '保存草稿' }}
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <div class="footer-tips">
            <p><i class="el-icon-warning"></i> 提示：内容提交后无法撤销，请仔细检查后再发布</p>
        </div>
    </div>
</template>

<script>
import { _getTagAll, _typeAll, _addBlog, _uploadImgs } from "@/api/api.js"

export default {
    name: "WriteView",
    components: {},
    data() {
        return {
            dialogImageUrl: '',
            dialogVisible: false,
            disabled: false,
            editForm: {
                title: "",
                description: "",
                cover: "",
                content: "",
                typeid: "",			//类型
                isoriginal: true,		//原创
                ispublic: true,			//公开
                issue: true,		//文章是否完成
                blogTags: [],				//选中的tag列表
                userid: this.$store.state.login_user.id
            },
            rules1: {
                title: [
                    { required: true, message: '请输入标题', trigger: 'blur' },
                    { min: 3, max: 100, message: '长度在 3 到 100 个字符', trigger: 'blur' }
                ],
                description: [
                    { required: true, message: '请输入摘要', trigger: 'blur' }
                ],
                content: [
                    { required: true, message: '请输入文章内容', trigger: 'blur' }
                ],
                blogTags: [
                    { required: true, message: '请选择文章标签', trigger: 'blur' }
                ],
                typeid: [
                    { required: true, message: '请选择文章分类类型', trigger: 'blur' }
                ],
            },
            tagList: [],		//文章标签列表
            typeList: [],		//文章类型列表
            tagColors: ['', 'success', 'warning', 'danger', 'info'],
            categoryIcons: [
                'el-icon-notebook-2',
                'el-icon-cpu',
                'el-icon-data-analysis',
                'el-icon-collection',
                'el-icon-reading'
            ]
        }
    },
    methods: {
        // 初始化
        init() {
            /*获取所有tags*/
            _getTagAll().then(res => {
                if (res.data.status === 200) {
                    this.tagList = res.data.data;
                }
            })
            /*获取所有type*/
            _typeAll().then(res => {
                if (res.data.status === 200) {
                    this.typeList = res.data.data;
                }
            })
        },
        handleFileSuccess(response, file, fileList) {
            this.$message.success("封面上传成功");
            if (response.status == 200) {
                this.editForm.cover = response.data
            }
        },
        handleChange(file, fileList) {
            if (fileList.length > 0) {
                this.$refs.upload.$children[1].$el.style.display = "none"
            }
        },
        handleRemove(file, fileList) {
            this.fileList = []
            if (fileList.length == 0) {
                this.$refs.upload.$children[1].$el.style.display = "inline-block";
            }
        },
        handlePictureCardPreview(file) {
            this.dialogImageUrl = file.url;
            this.dialogVisible = true;
        },
        //提交博客
        submitBlogForm() {
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    _addBlog(this.editForm).then(res => {
                        if (res.data.status === 200) {
                            this.$notify.success({
                                title: '发布成功',
                                message: res.data.msg,
                                position: 'bottom-right'
                            });
                            this.$router.push('/home/allblog')
                        }
                    })
                } else {
                    this.$notify.error({
                        title: '表单验证失败',
                        message: '请检查必填字段',
                        position: 'bottom-right'
                    });
                    return false;
                }
            });
        },
        //重置（清空输入）
        reset() {
            this.$confirm('确定要重置所有内容吗？', '重置确认', {
                confirmButtonText: '确定重置',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$refs.editForm.resetFields();
                this.$refs.md.value = "";
                this.$refs.upload.clearFiles();
                this.$message.success('内容已重置');
            });
        },

        /*mavon-editor图片上传*/
        handleMavonEditorImgAdd(pos, $file) {
            var formdata = new FormData();
            formdata.append('file', $file);

            _uploadImgs(formdata).then(res => {
                if (res.data.status === 200) {
                    this.$message.success("图片上传成功");
                    this.$refs.md.$img2Url(pos, res.data.data);
                }
            })
        },
        /*mavon-editor图片删除*/
        handleMavonEditorImgDel(pos, $file) {
            delete this.img_file[pos];
        },
    },
    created() {
        this.init();
    }
}
</script>

<style scoped>
.write-blog-view {
    /* max-width: 1200px; */
    margin: 0 auto;
    padding: 5px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background-color: #f8fafc;
    min-height: 100vh;
}

.header {
    text-align: center;
    margin-bottom: 10px;
    padding: 5px 0;
    color: #2c3e50;
    animation: fadeInDown 0.6s;
}

.header h1 {
    font-size: 2.2rem;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.header .subtitle {
    font-size: 1rem;
    color: #7f8c8d;
    font-weight: 400;
}

.form-container {
    border-radius: 12px;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    border: none;
    overflow: hidden;
    transition: all 0.3s ease;
    background: white;
}

.form-container:hover {
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}

.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 30px;
    margin-bottom: 20px;
}

@media (max-width: 992px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}

.form-left,
.form-right {
    display: flex;
    flex-direction: column;
}

.title-input {
    font-size: 1.1rem;
}

.cover-upload {
    margin-top: 15px;
}

.upload-tip {
    font-size: 0.85rem;
    color: #7f8c8d;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 5px;
}

.cover-preview {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: hidden;
    border-radius: 6px;
}

.cover-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.el-upload-list__item-actions {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
}

.cover-preview:hover .el-upload-list__item-actions {
    opacity: 1;
}

.el-upload-list__item-preview,
.el-upload-list__item-delete {
    color: white;
    font-size: 1.2rem;
    margin: 0 10px;
    cursor: pointer;
    transition: transform 0.2s;
}

.el-upload-list__item-preview:hover,
.el-upload-list__item-delete:hover {
    transform: scale(1.2);
}

.tag-selector,
.category-selector {
    width: 100%;
}

.category-option {
    display: flex;
    align-items: center;
    gap: 10px;
}

.el-radio {
    margin-right: 15px;
}

.publish-switch {
    background: #e8f4ff;
    border-radius: 8px;
    padding: 15px;
    text-align: center;
}

.markdown-editor {
    border-radius: 8px;
    overflow: hidden;
    border: 1px solid #dcdfe6;
    transition: border-color 0.3s;
}

.markdown-editor:hover {
    border-color: #409eff;
}

.action-buttons {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    gap: 20px;
}

.btn-reset,
.btn-submit {
    padding: 12px 30px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s;
}

.btn-reset {
    background: #f0f2f5;
    border-color: #dcdfe6;
    color: #606266;
}

.btn-reset:hover {
    background: #e6e9ed;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.btn-submit {
    background: linear-gradient(135deg, #409eff, #337ecc);
    border: none;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.btn-submit:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.footer-tips {
    margin-top: 20px;
    text-align: center;
    font-size: 0.9rem;
    color: #95a5a6;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}

@keyframes fadeInDown {
    from {
        opacity: 0;
        transform: translateY(-20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>