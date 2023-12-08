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
            <el-table-column label="文章列表" style="width: 100%">
                <template slot-scope="scope">
                    <el-card class="box-card">
                        <el-row type="flex" :gutter="10">
                            <el-col :span="6">
                                <el-image style="width: 100%;height: 180px;border: 1px solid #eee;" :src="scope.row.cover"
                                    fit="cover"></el-image>
                            </el-col>

                            <el-col :span="18">
                                <el-row style="font-size: 22px;margin: 10px">
                                    <span style="font-weight: bold;">{{ scope.row.title | ellipsis }}</span>
                                </el-row>
                                <el-row style="font-size: 16px;margin: 10px">
                                    {{ scope.row.description | ellipsisDis }}
                                </el-row>

                                <div style="margin-top: 15px;margin: 10px;font-size: 10px;">
                                    <div style="margin-right: 20px;">
                                        <i style="margin: 5px;" class="iconfont icon-zuozhe">{{ scope.row.uName }} </i>
                                        <i style="margin: 5px;" class="iconfont icon-shuben1">{{ scope.row.tName }} </i>
                                        <i style="margin: 5px;" class="iconfont icon-31shijian">{{ scope.row.createtime |
                                            fortTime(scope.row.createtime) }}</i>
                                    </div>
                                </div>

                                <div style="margin-top: 15px;margin: 10px">
                                    <div style="margin-right: 10px;">
                                        <i style="margin: 5px;" v-if="scope.row.ispublic == '0'"
                                            class="iconfont icon-suoding"></i>
                                        <i style="margin: 5px;" v-if="scope.row.ispublic == '1'"
                                            class="iconfont icon-jiesuo"></i>
                                        <el-tag style="margin: 5px;" v-if="scope.row.isdel" size="mini"
                                            type="danger">已删除</el-tag>
                                        <el-tag style="margin: 5px;" v-if="!scope.row.issue" size="mini"
                                            type="warning">草稿</el-tag>
                                        <el-tag style="margin: 5px;" v-if="scope.row.issue" size="mini">{{
                                            scope.row.isoriginal }}</el-tag>

                                        <i class="iconfont icon-biaoqian" style="margin: 5px;">
                                            <el-tag style="margin-left: 5px;" v-for="tag in scope.row.blogTags.t"
                                                size="mini" :type="tag.color">{{ tag.name }}</el-tag>
                                        </i>

                                    </div>
                                </div>
                                <div style="margin-top: 15px;margin: 10px" class="my-flex">
                                    <div style="margin-right: 20px;"><i style="margin: 5px;"
                                            class="iconfont icon-yanjing_xianshi"> {{
                                                scope.row.browsecount }} </i>
                                    </div>
                                    <div style="margin-right: 20px;"><i class="iconfont icon-dianzan_huaban">
                                            {{ scope.row.likes }} </i>
                                    </div>
                                    <div style="margin-right: 20px;"><i class="iconfont icon-31shoucang">
                                            {{ scope.row.collection }} </i>
                                    </div>

                                    <div class="my-flex-span1" style="margin-right: 20px;">
                                    </div>

                                    <div style="margin-right: 20px;">
                                        <el-button v-if="!scope.row.isDelete" type="primary" size="mini"
                                            @click="$router.push('/home/editblog/' + scope.row.bid)">编辑</el-button>
                                    </div>
                                    <div style="margin-right: 20px;" v-if="!scope.row.isDelete">
                                        <el-popconfirm @confirm="handleDelete(scope.$index, scope.row.bid)"
                                            confirm-button-text='确定' cancel-button-text='取消' icon="el-icon-info"
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
import { _getBlog, _delBlog, _delBlogH ,_getBlogByTile} from "@/api/api.js";
import { formatDate } from '@/utils/formatDate'
export default {
    name: "BlogView",
    data() {
        return {
            baseUrl:"",
            baseLikeUrl:"",
            inputSearchKeyWord: "",
            activeName: "first",   //当前选项卡的label
            tabindex: "0",   //选项卡index
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
        },
        ellipsis(value) {
            if (!value) return '';
            if (value.length > 30) {
                return value.slice(0, 30) + '...'
            }
            return value
        },
        ellipsisDis(value) {
            if (!value) return '';
            if (value.length > 100) {
                return value.slice(0, 100) + '...'
            }
            return value
        }

    },
    methods: {
        //分页
        getCurrentPageData(listData, currentPage, pageSize) {
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
            this.baseUrl = `/blog/getBlogByPage?1=1`;
            this.baseLikeUrl = `/blog/getBlogByPageTitle?1=1`;
            if (this.activeTab === "0") {		//全部
                this.baseUrl += "&isdel=0 ";
                this.baseLikeUrl += "&isdel=0 ";
                this.pagination.currentPage = 1;
                this.pagination.pageSize = 5;
            } else if (this.activeTab === "1") {	//原创
                this.baseUrl += "&isoriginal=原创&issue=1&isdel=0 ";
                this.baseLikeUrl += "&isoriginal=原创&issue=1&isdel=0 ";
                this.pagination.currentPage = 1;
                this.pagination.pageSize = 5;
            } else if (this.activeTab === "2") {	//转载
                this.baseUrl += "&isoriginal=转载&issue=1&isdel=0 ";
                this.baseLikeUrl += "&isoriginal=转载&issue=1&isdel=0 ";
                this.pagination.currentPage = 1;
                this.pagination.pageSize = 5;
            } else if (this.activeTab === "3") {	//草稿
                this.baseUrl += "&issue=0&isdel=0 ";
                this.baseLikeUrl += "&issue=0&isdel=0 ";
                this.pagination.currentPage = 1;
                this.pagination.pageSize = 5;
            } else if (this.activeTab === "4") {	//公开
                this.baseUrl += "&ispublic=1&issue=1&isdel=0 ";
                this.baseLikeUrl += "&ispublic=1&issue=1&isdel=0 ";
                this.pagination.currentPage = 1;
                this.pagination.pageSize = 5;
            } else if (this.activeTab === "5") {	//私密
                this.baseUrl += "&ispublic=0&issue=1&isdel=0 "
                this.baseLikeUrl += "&ispublic=0&issue=1&isdel=0 "
            } else if (this.activeTab === "6") {	//回收站
                this.baseUrl += "&isdel=1 ";
                this.baseLikeUrl += "&isdel=1 ";
                this.pagination.currentPage = 1;
                this.pagination.pageSize = 5;
            }
            //获取所有博客内容
            _getBlog(this.baseUrl).then(res => {
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
        },
        //根据标题栏搜索
        searchByTitle() {
            //根据博客标题关键字搜索
            _getBlogByTile(this.baseLikeUrl+"&title="+this.inputSearchKeyWord).then(res => {
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
        },

        //表格操作-删除
        handleDelete(index, bid) {
            if (this.activeTab === '6') {
                _delBlog(bid).then(res => {
                    this.$message.success("删除成功!");
                    this.initBlogs();

                })
            } else {
                _delBlogH(bid).then(res => {
                    this.$message.success("删除成功!");
                    this.initBlogs();

                })
            }
        },
        //预览博客内容
        handlePreviewContent(index, row) {
            this.currentBlog = row;
            this.previewBlogDialogVisible = true;
        },

        //nav改变
        handleNavTagSelect(key, keypath) {
            this.activeTab = key;
            this.initBlogs();
        },
        /*分页事件*/
        handleSizeChange(val) {
            this.pagination.pageSize = val;
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
