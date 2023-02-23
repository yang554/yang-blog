<template>
    <div class="all-blog-view">
        <!--搜索-->
        <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="12">
                <el-input placeholder="按照博客标题搜索" v-model="inputSearchKeyWord">
                    <el-button slot="append" type="success" @click="searchByTitle" icon="el-icon-search"></el-button>
                </el-input>
            </el-col>
        </el-row>

        <el-menu :default-active="activeTab" class="el-menu-demo" mode="horizontal" @select="handleNavTagSelect">
            <el-menu-item index="0">全部</el-menu-item>
            <el-menu-item index="1">原创</el-menu-item>
            <el-menu-item index="2">转载</el-menu-item>
            <el-menu-item index="3">草稿</el-menu-item>
            <el-menu-item index="4">公开</el-menu-item>
            <el-menu-item index="5">私密</el-menu-item>
            <el-menu-item index="6">回收站</el-menu-item>
        </el-menu>

        <el-table v-loading="pagination.loading" :data="pagination.currentTableData" style="width: 100%">
            <el-table-column label="文章列表" width="1000">
                <template slot-scope="scope">
                    <el-card class="box-card">
                        <el-row type="flex" :gutter="10">
                            <el-col :span="6">
                                <el-image style="width: 100%;height: 80px;border: 1px solid #eee;" :src="scope.row.cover"
                                    fit="cover"></el-image>
                            </el-col>

                            <el-col :span="18">
                                <el-row style="font-size: 18px;">
                                    <!-- 根据issue属性判断文章是否为草稿 -->
                                    <el-button size="mini" v-if="!scope.row.issue" type="warning" icon="el-icon-edit"
                                        circle></el-button>
                                    {{ scope.row.title }}
                                </el-row>

                                <div style="margin-top: 15px;" class="my-flex">
                                    <div style="margin-right: 10px;" v-if="scope.row.isDelete">
                                        <el-tag size="mini" type="danger">已删除</el-tag>
                                    </div>
                                    <div style="margin-right: 10px;" v-if="!scope.row.issue">
                                        <el-tag size="mini" type="warning">草稿
                                        </el-tag>
                                    </div>
                                    <div style="margin-right: 10px;" v-if="scope.row.issue">
                                        <el-tag size="mini">{{ scope.row.isoriginal }}
                                        </el-tag>
                                    </div>
                                    <div style="margin-right: 20px;" v-if="scope.row.issue">
                                        <el-tag size="mini" type="info">
                                            {{ scope.row.ispublic == '0' ? '私密' : '公开' }}
                                        </el-tag>
                                    </div>
                                    <div style="margin-right: 20px;"><i class="el-icon-view"> {{ scope.row.views }} </i>
                                    </div>
                                    <div style="margin-right: 20px;"><i class="el-icon-chat-square">
                                            {{ scope.row.commentCount }} </i>
                                    </div>
                                    <div class="my-flex-span1" style="margin-right: 20px;"><i class="el-icon-date">
                                            {{ scope.row.createtime | fortTime(scope.row.createtime) }}</i>
                                    </div>

                                    <div style="margin-right: 20px;">
                                        <el-button v-if="!scope.row.isDelete" type="primary" size="mini"
                                            @click="$router.push('/admin/editblog/' + scope.row.id)">编辑</el-button>
                                    </div>
                                    <div style="margin-right: 20px;" v-if="!scope.row.isDelete">
                                        <el-popconfirm @confirm="handleDelete(scope.$index, scope.row.id)"
                                            confirm-button-text='好的' cancel-button-text='不用了' icon="el-icon-info"
                                            icon-color="red" title="确定删除博客吗？">
                                            <el-button slot="reference" type="danger" size="mini">删除</el-button>
                                        </el-popconfirm>
                                    </div>
                                    <div style="margin-right: 20px;">
                                        <el-button type="warning" size="mini"
                                            @click="handlePreviewContent(scope.$index, scope.row)">预览</el-button>
                                    </div>
                                    <div style="margin-right: 20px;" v-if="scope.row.isDelete">
                                        <el-button @click="recoveryBlog(scope.row.id)" type="primary"
                                            size="mini">还原</el-button>
                                    </div>
                                </div>
                            </el-col>
                        </el-row>
                    </el-card>
                </template>
            </el-table-column>
        </el-table>

        <!--dialog预览博客内容-->
        <el-dialog title="预览博客内容" :visible.sync="previewBlogDialogVisible">
            <mavon-editor :value="currentBlog.content" defaultOpen="preview" :boxShadow="false"
                style="z-index:1;height:70vh" :editable="false" :subfield="false" :toolbarsFlag="false">
            </mavon-editor>
        </el-dialog>


        <!-- 分页 -->
        <div style="margin-top: 20px;" class="my-border-padding">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="pagination.totalBlogs">
            </el-pagination>
        </div>
</div>
</template>

<script>
import { _getBlog } from "@/api/api.js";
import { formatTime, formatDate } from '@/utils/formatDate'
export default {
    name: "BlogView",
    data() {
        return {
            inputSearchKeyWord: "",
            activeName: "first",   //当前选项卡的label
            tabindex: "0",   //选项卡index
            hidden_search_comp: false,		//false隐藏搜索子组件,true显示
            pagination: {
                listData: null,  //微博总data
                currentPage: 1, //当前页
                pageSizes: [5, 10, 15, 20],
                pageSize: 5,    //每页显示条数
                totalPage: 1,		//总页数
                currentTableData: [],		//当前页data
                totalBlogs: 0,  //总数量
                loading: false, 		//table加载样式是否显示
            },
            activeTab: "0",		//当前选择tab
            currentBlog: {},		//当前选中的博客
            previewBlogDialogVisible: false,
        }
    },
    created() {
        this.initBlogs();
    },
    filters: {
        fortTime(val) {
            return formatDate(val);
        }
    },
    methods: {
        //分页
        getCurrentPageData(listData, currentPage, pageSize) {
            console.log("实际分页数" + pageSize);
            let begin = (currentPage - 1) * pageSize;
            let end = currentPage * pageSize;
            this.pagination.currentTableData = listData.slice(
                begin,
                end
            );
        },
        //初始化【全部】博客的数据
        initBlogs() {
            //通用路由
            let baseUrl = `/blog/getBlogByPage?1=1`;
            if (this.activeTab === "0") {		//全部
                baseUrl += "&isdel=0 "
            } else if (this.activeTab === "1") {	//原创
                baseUrl += "&isoriginal=原创&issue=1&isdel=0 "
            } else if (this.activeTab === "2") {	//转载
                baseUrl += "&isoriginal=转载&issue=1&isdel=0 "
            } else if (this.activeTab === "3") {	//草稿
                baseUrl += "&issue=0&isdel=0 "
            } else if (this.activeTab === "4") {	//公开
                baseUrl += "&ispublic=1&issue=1&isdel=0 "
            } else if (this.activeTab === "5") {	//私密
                baseUrl += "&ispublic=0&issue=1&isdel=0 "
            } else if (this.activeTab === "6") {	//回收站
                baseUrl += "&isdel=1 "
            }
            console.log("表头" + this.activeTab);
            console.log("每页条数----：" + this.pagination.pageSize);
            //获取所有博客内容
            _getBlog(baseUrl).then(res => {
                if (res.data.status === 200) {
                    this.pagination.totalBlogs = Number(res.data.obj.length);
                    this.pagination.listData = res.data.obj;
                    this.pagination.loading = true;
                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;


                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);

                    this.pagination.loading = false;
                } else {
                    this.pagination.loading = false;
                    this.$message.warning(res.data.msg)
                }
            })
            console.log("每页条数：" + this.pagination.pageSize);
        },
        //根据标题栏搜索
        searchByTitle() {
            if (this.inputSearchKeyWord === "") {
                this.$message.error("请输入搜索关键字")
                this.hidden_search_comp = false;
            } else {
                this.hidden_search_comp = true;
            }
            //根据博客标题关键字搜索
            this.$request("/blog/findByLikeTitle?title=" + this.inputSearchKeyWord).then(res => {
                console.log(res);
                if (res.data.status === 200) {
                    this.tableData.splice(0);
                    this.tableData.push(...res.data.obj);
                    this.totalBlogs = res.data.obj.length;
                }
            })
        },

        //表格操作-删除
        handleDelete(index, bid) {
            console.log(index, bid);
            //网络请求
            this.$deleteRequest("/blog/delete/?bid=" + bid).then(res => {
                console.log(res);
                this.$message.success("删除成功!");

                this.tableData.splice(index, 1);
            })
        },
        //预览博客内容
        handlePreviewContent(index, row) {
            // console.log(index);
            this.currentBlog = row;
            this.previewBlogDialogVisible = true;
        },

        //nav改变
        handleNavTagSelect(key, keypath) {
            this.activeTab = key;
            console.log(this.pagination.pageSize + "nav=====");
            this.initBlogs();
        },
        /*分页事件*/
        handleSizeChange(val) {
            console.log("val========="+val);
            this.pagination.pageSize = val;
            console.log("改变每页数：" + this.pagination.pageSize);
            this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize)
        },
        handleCurrentChange(val) {
            this.pagination.currentPage = val
            this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize)
        },
    }
}
</script>

<style></style>
