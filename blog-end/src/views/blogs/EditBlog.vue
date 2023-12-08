<template>
    <div class="edit-blog-view">
        <el-form ref="editForm" status-icon label-width="100px" label-position="left" :model="editForm" :rules="rules1"
            @submit.native.prevent>
            <el-form-item label="标题" prop="title">
                <el-input v-model="editForm.title"></el-input>
            </el-form-item>

            <el-form-item label="描述" prop="description">
                <el-input v-model="editForm.description"></el-input>
            </el-form-item>

            <el-form-item label="首页图片">
                <div class="demo-image__preview">
                    <el-image style="width: 150px; height: 100px" :src="editForm.cover"
                        :preview-src-list="[editForm.cover]">
                    </el-image>
                </div>
                <el-upload ref="cover" class="upload-demo" :show-file-list="false" action="/api/file/upload" :auto-upload="true"
                    auto:true :on-change="handleChange" :on-success="handleFileSuccess" accept=".jpg,.png,.bmp,.jpeg,.gif">
                    <el-button size="small" type="primary">点击上传</el-button>
                </el-upload>
            </el-form-item>

            <el-form-item label="文章标签">
                <el-tag style="margin-left: 5px;" v-for="tag in editForm.blogTags" :type="tag.color">{{
                    tag.name }}</el-tag>
                <el-select :multiple-limit="3" v-model="editForm.tags" multiple placeholder="请选择Tag标签">
                    <el-option v-for="(item, index) in tagList" :key="index" :label="item.name" :value="item.id">
                    </el-option>
                </el-select>

            </el-form-item>

            <el-form-item label="分类专栏">
                <el-tag style="margin-left: 5px;">{{ editForm.tName }}</el-tag>
                <el-select v-model="editForm.typeid" placeholder="请选择文章分类">
                    <el-option v-for="(item, index) in typeList" :key="index" :label="item.name" :value="item.id"
                        :selected="true">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="文章类型">
                <el-radio-group v-model="editForm.isoriginal">
                    <el-radio :label="0">转载</el-radio>
                    <el-radio :label="1">原创</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="发布形式">
                <el-radio-group v-model="editForm.ispublic">
                    <el-radio :label="0">私密</el-radio>
                    <el-radio :label="1">公开</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="完成/草稿">
                <el-switch style="display: block" v-model="editForm.issue" active-color="#13ce66" inactive-color="#ff4949"
                    active-text="发布" inactive-text="草稿">
                </el-switch>
            </el-form-item>

            <el-form-item label="内容" prop="content">
                <mavon-editor ref="md" placeholder="请输入文档内容..." :boxShadow="false" @imgAdd="handleMavonEditorImgAdd"
                    @imgDel="handleMavonEditorImgDel" style="z-index:1;border: 1px solid #d9d9d9;height:70vh"
                    v-model="editForm.content" />
            </el-form-item>

            <el-form-item style="margin: auto;">
                <el-button type="warning" @click="reset">重置</el-button>
                <el-button type="primary" @click="submitBlogForm">更新博客</el-button>
                <el-button type="info" @click="$router.push('/home/allblog')">返回</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import { _getBlogById, _typeAll, _getTagAll, _saveBlog, _uploadImgs } from "@/api/api.js";

export default {
    name: "EditBlog",
    components: {},
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
        }
    },
    methods: {
        //初始化
        initBlog() {
            /*通过blog_id获取博客内容*/
            _getBlogById(this.bid).then(res => {
                if (res.data.status === 200) {
                    this.editForm = res.data.obj;
                    this.editForm.isoriginal = res.data.obj.isoriginal === "转载" ? 0 : 1;
                    this.editForm.ispublic = res.data.obj.ispublic ? 1 : 0;
                }
            })

            _getTagAll().then(res => {
                if (res.data.status === 200) {
                    //每次请求时清空tableData数据
                    this.tagList.splice(0);
                    this.tagList.push(...res.data.obj)
                }
            })

            /*获取所有type*/
            _typeAll().then(res => {
                if (res.data.status === 200) {
                    //每次请求时清空tableData数据
                    this.typeList.splice(0);
                    this.typeList.push(...res.data.obj)
                }
            })
        },
        //提交更新
        submitBlogForm() {
            this.editForm.id = this.bid;
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    _saveBlog(this.editForm).then(res => {
                        if (res.data.status === 200) {
                            this.$notify.success(res.data.msg);
                            this.$router.push("/home/allblog");
                        }
                    })
                } else {
                    this.$notify.error('error submit!!');
                    return false;
                }
            });
        },
        //重置（清空输入）
        reset() {
            this.$refs.editForm.resetFields();
        },

        /*mavon-editor图片上传*/
        handleMavonEditorImgAdd(pos, $file) {
            var formdata = new FormData();
            formdata.append('file', $file);
            console.log(formdata);

            _uploadImgs(formdata).then(res => {
                //将返回的url替换到文本原位置![...](0) -> ![...](url)
                console.log(res);
                if (res.data.status === 200) {
                    this.$message.success("图片上传成功");
                    this.$refs.md.$img2Url(pos, res.data.obj);
                }
            })
        },
        /*mavon-editor图片删除*/
        handleMavonEditorImgDel(pos, $file) {
            delete this.img_file[pos];
        },
        handleFileSuccess(response, file, fileList) {
            if (response.status == 200) {
                this.editForm.cover = response.obj
            }
        },
        handleChange(file, fileList) {
            // this.multipartFile = new FormData();
            // this.multipartFile.append("file", file.raw);
            // this.editForm.cover = window.webkitURL.createObjectURL(file.raw);
        }

    },
    created() {
        this.bid = this.$route.params.id
        this.initBlog();
    }
}
</script>

<style scoped>
.edit-blog-view {
    width: 98%;
}
</style>
