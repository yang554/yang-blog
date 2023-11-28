<template>
    <div class="write-blog-view">
        <el-form ref="editForm" status-icon label-width="80px" label-position="left" :model="editForm" :rules="rules1">
            <el-form-item label="标题" prop="title">
                <el-input v-model="editForm.title"></el-input>
            </el-form-item>

            <el-form-item label="描述" prop="description">
                <el-input v-model="editForm.description"></el-input>
            </el-form-item>

            <el-form-item label="首页图片">
                <el-upload ref="upload" :on-success="handleFileSuccess" accept=".jpg,.png,.bmp,.jpeg,.gif"
                    action="/api/file/upload" list-type="picture-card" :auto-upload="true" :on-change="handleChange"
                    :limit='1' :on-remove="handleRemove">
                    <i class="el-icon-plus"></i>
                </el-upload>
                <el-dialog :visible.sync="dialogVisible">
                    <img width="100%" :src="dialogImageUrl" alt="">
                </el-dialog>
            </el-form-item>

            <el-form-item label="文章标签">
                <el-select :multiple-limit="3" v-model="editForm.blogTags" multiple placeholder="请选择Tag标签">
                    <el-option v-for="(item, index) in tagList" :key="index" :label="item.name" :value="item.id">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="分类专栏">
                <el-select v-model="editForm.typeid" placeholder="请选择文章分类Category">
                    <el-option v-for="(item, index) in typeList" :key="index" :label="item.name" :value="item.id">
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

            <el-form-item>
                <el-switch style="display: block" v-model="editForm.issue" active-color="#13ce66" inactive-color="#ff4949"
                    active-text="文章发布" inactive-text="暂存草稿">
                </el-switch>
            </el-form-item>

            <el-form-item label="内容" prop="content">
                <mavon-editor ref="md" placeholder="请输入文档内容..." :boxShadow="false" @imgAdd="handleMavonEditorImgAdd"
                    @imgDel="handleMavonEditorImgDel" style="z-index:1;border: 1px solid #d9d9d9;height:60vh"
                    v-model="editForm.content" />
            </el-form-item>

            <el-form-item style="margin: auto;">
                <el-button type="info" @click="reset">重置</el-button>
                <el-button type="primary" @click="submitBlogForm">提交</el-button>
            </el-form-item>
        </el-form>
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
                isoriginal: 1,		//原创
                ispublic: 1,			//公开
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
            },
            tagList: [],		//文章标签列表
            typeList: [],		//文章类型列表
        }
    },
    methods: {
        //初始化
        init() {
            /*初始化mavon-editor*/
            // this.$refs.md.value = "";
            /*获取所有tags*/
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
        handleFileSuccess(response, file, fileList) {
            this.$message.success("封面上传成功");
            if (response.status == 200) {
                this.editForm.cover = response.obj
            }
        },
        handleChange(file, fileList) {
            if (fileList.length > 0) {// 当上传图片成功时，即隐藏上传按钮
                console.log(file)
                // this.editForm.cover = window.webkitURL.createObjectURL(file.raw);
                // console.log(this.editForm.cover)
                // console.log(file.url)
                this.$refs.upload.$children[1].$el.style.display = "none" //= ('display', 'none');
            }

        },
        handleRemove(file, fileList) {
            this.fileList = []
            if (fileList.length == 0) {
                this.$refs.upload.$children[1].$el.style.display = "inline-block";// = ('display', 'inline-block');
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
                            this.$notify.success(res.data.msg);
                            this.$router.push('/home/allblog')
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

    },
    created() {
        this.init();
    }
}
</script>

<style scoped>
.el-upload-list el-upload-list--picture-card {
    display: "none";
}

.write-blog-view {
    width: 98%;
}

.block {
    height: 80px;
    width: 80px;
}
</style>
