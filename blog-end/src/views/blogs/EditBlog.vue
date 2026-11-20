<template>
    <div class="edit-container">
        <div class="header">
            <h1><i class="el-icon-edit-outline"></i> 编辑博客文章</h1>
            <p class="subtitle">更新您的内容，分享最新见解</p>
        </div>

        <el-card class="form-card" shadow="hover">
            <el-form ref="editForm" status-icon label-width="120px" label-position="top" :model="editForm" :rules="rules1"
                @submit.native.prevent>
                <div class="form-grid">
                    <!-- 左侧表单 -->
                    <div class="form-column">
                        <el-form-item label="文章标题" prop="title">
                            <el-input v-model="editForm.title" placeholder="请输入文章标题..." size="large" class="title-input">
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
                            <div class="current-cover">
                                <el-image
                                    style="width: 180px; height: 120px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);"
                                    :src="editForm.cover" :preview-src-list="[editForm.cover]">
                                    <div slot="error" class="image-error">
                                        <i class="el-icon-picture-outline"></i>
                                        <p>封面未设置</p>
                                    </div>
                                </el-image>
                                <div class="cover-tip">当前封面</div>
                            </div>

                            <div class="upload-control">
                                <div class="upload-tip">
                                    <i class="el-icon-info"></i> 支持 JPG/PNG 格式，建议尺寸 1200×630
                                </div>
                                <el-upload ref="cover" class="upload-demo" :show-file-list="false" :action="uploadAvatarUrl"
                                    :data="uploadAvatarParams" :auto-upload="true" :on-change="handleChange"
                                    :on-success="handleFileSuccess" :before-upload="beforeAvatarUpload"
                                    accept=".jpg,.png,.bmp,.jpeg,.gif">
                                    <el-button size="medium" type="primary" icon="el-icon-upload">
                                        更新封面
                                    </el-button>
                                </el-upload>
                            </div>
                        </el-form-item>
                    </div>

                    <!-- 右侧表单 -->
                    <div class="form-column">
                        <el-form-item label="文章标签">
                            <div class="current-tags">
                                <el-tag v-for="(tag, index) in editForm.blogTags" :key="index"
                                :type="tag.color" class="tag-item">
                                    <i class="el-icon-collection-tag"></i> {{ tag.name }}
                                </el-tag>
                            </div>

                            <el-select :multiple-limit="3" v-model="editForm.tags" multiple placeholder="选择新标签（最多3个）"
                                class="tag-selector" size="medium">
                                <el-option v-for="(item, index) in tagList" :key="index" :label="item.name"
                                    :value="item.id">
                                    <el-tag :type="item.color">{{ item.name }}</el-tag>
                                </el-option>
                            </el-select>
                        </el-form-item>

                        <el-form-item label="分类专栏">
                            <div class="current-category">
                                <el-tag type="info" class="category-tag">
                                    <i class="el-icon-notebook-2"></i> {{ editForm.tName }}
                                </el-tag>
                            </div>

                            <el-select v-model="editForm.typeid" placeholder="选择新分类" size="medium"
                                class="category-selector">
                                <el-option v-for="(item, index) in typeList" :key="index" :label="item.name"
                                    :value="item.id" :selected="true">
                                    <div class="category-option">
                                        <i :class="categoryIcons[index % categoryIcons.length]"></i>
                                        <span>{{ item.name }}</span>
                                    </div>
                                </el-option>
                            </el-select>
                        </el-form-item>

                        <div class="publish-switch">
                            <el-form-item label="文章类型">
                                <el-switch v-model="editForm.isoriginal" active-color="#13ce66" inactive-color="#ff4949"
                                    active-text="原创" inactive-text="转载" size="large">
                                </el-switch>
                            </el-form-item>
                        </div>

                        <div class="publish-switch">
                            <el-form-item label="发布形式">
                                <el-switch v-model="editForm.ispublic" active-color="#13ce66" inactive-color="#ff4949"
                                    active-text="公开" inactive-text="私密" size="large">
                                </el-switch>
                            </el-form-item>
                        </div>

                        <div class="publish-switch">
                            <el-form-item label="发布状态">
                                <el-switch v-model="editForm.issue" active-color="#13ce66" inactive-color="#ff4949"
                                    active-text="发布" inactive-text="草稿" size="large">
                                </el-switch>
                            </el-form-item>
                        </div>
                    </div>
                </div>

                <el-form-item label="文章内容" prop="content">
                    <mavon-editor ref="md" placeholder="编辑文章内容..." :boxShadow="false" @imgAdd="handleMavonEditorImgAdd"
                        @imgDel="handleMavonEditorImgDel" class="markdown-editor" v-model="editForm.content" />
                </el-form-item>

                <el-form-item class="action-buttons">
                    <el-button type="info" @click="$router.push('/home/allblog')" size="large" class="btn-back">
                        <i class="el-icon-back"></i> 返回列表
                    </el-button>
                    <el-button type="warning" @click="reset" size="large" class="btn-reset">
                        <i class="el-icon-refresh"></i> 重置
                    </el-button>
                    <el-button type="primary" @click="submitBlogForm" size="large" class="btn-submit">
                        <i class="el-icon-s-promotion"></i> 更新博客
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <div class="footer-tips">
            <p><i class="el-icon-warning"></i> 提示：更新后将覆盖原内容，请仔细检查后再提交</p>
        </div>
    </div>
</template>
  
<script>
import { _getBlogById, _typeAll, _getTagAll, _saveBlog, _uploadImgs } from "@/api/api.js";

export default {
    name: "EditBlog",
    components: {},
    computed: {
        uploadAvatarUrl() {
            return '/api/file/upload/avatar';
        },
    },
    props: {},
    data() {
        return {
            bid: "",   //博客id
            multipartFile: [],
            editForm: {
                bid: "",
                title: "",
                description: "",
                cover: "",
                content: "",
                typeid: "",			//类型
                isoriginal: 1,		//原创
                ispublic: 1,			//公开
                issue: 1,		//文章是否完成
                tags: [],				//选中的tag列表
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
            },
            tagList: [],		//标签列表
            typeList: [],		//类型列表
            uploadAvatarParams: {
                "id": "",
                "type": 'blogAvatar',
            },
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
        //初始化
        initBlog() {
            /*通过blog_id获取博客内容*/
            _getBlogById(this.bid).then(res => {
                if (res.data.status === 200) {
                    this.editForm = res.data.data;
                    this.editForm.isoriginal = res.data.data.isoriginal === "原创" ? true : false;
                }
            })

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
        //提交更新
        submitBlogForm() {
            this.editForm.id = this.bid;
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    this.$confirm('确定要更新这篇文章吗？', '更新确认', {
                        confirmButtonText: '确定更新',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        _saveBlog(this.editForm).then(res => {
                            if (res.data.status === 200) {
                                this.$notify.success({
                                    title: '更新成功',
                                    message: res.data.msg,
                                    position: 'bottom-right'
                                });
                                this.$router.push("/home/allblog");
                            }
                        })
                    });
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
            this.$confirm('确定要重置所有修改吗？', '重置确认', {
                confirmButtonText: '确定重置',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$refs.editForm.resetFields();
                this.$message.success('修改已重置');
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
        handleFileSuccess(response, file, fileList) {
            if (response.status == 200) {
                this.$message.success("封面更新成功");
                this.editForm.cover = response.data
            }
        },
        handleChange(file, fileList) {
            // this.multipartFile = new FormData();
            // this.multipartFile.append("file", file.raw);
            // this.editForm.cover = window.webkitURL.createObjectURL(file.raw);
        },
        /*上传头像*/
        beforeAvatarUpload(file) {
            const isJPG = file.type.startsWith("image");
            const isLt10M = file.size / 1024 / 1024 < 10;
            if (!isJPG) {
                this.$message.error('上传图片只能是 JPG/PNG 格式!');
            }
            if (!isLt10M) {
                this.$message.error('上传图片大小不能超过 10MB!');
            }
            return isJPG && isLt10M;
        },

    },
    created() {
        this.bid = this.$route.params.id
        this.uploadAvatarParams.id = this.$route.params.id
        this.initBlog();
    }
}
</script>
  
<style scoped>
.edit-container {
    /* max-width: 1200px; */
    margin: 0 auto;
    padding: 5px;
    font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
    background-color: #f8fafc;
    min-height: 100vh;
}

.header {
    text-align: center;
    margin-bottom: 5px;
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
    font-weight: 600;
    color: #1a365d;
}

.header .subtitle {
    font-size: 1rem;
    color: #718096;
    font-weight: 400;
}

.form-card {
    border-radius: 12px;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    border: none;
    overflow: hidden;
    transition: all 0.3s ease;
    background: white;
    padding: 25px;
}

.form-card:hover {
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

.form-column {
    display: flex;
    flex-direction: column;
}

.title-input {
    font-size: 1.1rem;
}

.title-input>>>.el-input__inner {
    border-radius: 8px;
    padding-left: 40px;
    height: 48px;
}

.cover-upload {
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-top: 15px;
}

.current-cover {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
}

.cover-tip {
    font-size: 0.85rem;
    color: #718096;
    font-style: italic;
}

.image-error {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #909399;
}

.upload-control {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.upload-tip {
    font-size: 0.85rem;
    color: #718096;
    display: flex;
    align-items: center;
    gap: 5px;
}

.current-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 15px;
}

.tag-item {
    display: flex;
    align-items: center;
    gap: 5px;
    font-weight: 500;
    border-radius: 20px;
    padding: 0 12px;
}

.current-category {
    margin-bottom: 15px;
}

.category-tag {
    font-size: 1rem;
    padding: 8px 15px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.tag-selector,
.category-selector {
    width: 100%;
}

.category-option {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 0;
}

.publish-switch {
    background: linear-gradient(135deg, #f0fff4, #f0f9eb);
    border-radius: 10px;
    padding: 18px;
    border: 1px solid #e1f3d8;
}

.markdown-editor {
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid #e2e8f0;
    transition: border-color 0.3s;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.markdown-editor:hover {
    border-color: #4299e1;
    box-shadow: 0 4px 12px rgba(66, 153, 225, 0.15);
}

.action-buttons {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    gap: 15px;
    flex-wrap: wrap;
}

.btn-back,
.btn-reset,
.btn-submit {
    padding: 12px 30px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s;
}

.btn-back {
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    color: #4a5568;
}

.btn-back:hover {
    background: #edf2f7;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
    color: #2d3748;
}

.btn-reset {
    background: #fff8e6;
    border: 1px solid #ffecd3;
    color: #e6a23c;
}

.btn-reset:hover {
    background: #ffecd3;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(230, 162, 60, 0.15);
}

.btn-submit {
    background: linear-gradient(135deg, #4299e1, #3182ce);
    border: none;
    box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
    color: white;
}

.btn-submit:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(66, 153, 225, 0.4);
    background: linear-gradient(135deg, #3182ce, #2b6cb0);
}

.footer-tips {
    margin-top: 25px;
    text-align: center;
    font-size: 0.9rem;
    color: #718096;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background: #f7fafc;
    padding: 12px;
    border-radius: 8px;
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

/* 表单标签样式 */
.el-form-item>>>.el-form-item__label {
    font-weight: 600;
    color: #2d3748;
    padding-bottom: 8px;
    font-size: 1rem;
}

/* 输入框样式 */
.el-input>>>.el-input__inner,
.el-textarea>>>.el-textarea__inner {
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    transition: all 0.3s;
}

.el-input>>>.el-input__inner:focus,
.el-textarea>>>.el-textarea__inner:focus {
    border-color: #4299e1;
    box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.2);
}
</style>