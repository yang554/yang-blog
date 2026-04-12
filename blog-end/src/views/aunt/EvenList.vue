<template>
    <div class="user-list-view">
        <!--搜索-->
        <div class="search-container">
            <el-row :gutter="20">
                <el-col :span="3">
                    <div class="search-item">
                        <el-dropdown>
                            <span class="search-label">
                                事件类型：<i class="el-icon-arrow-down el-icon--right"></i>
                            </span>
                            <el-input disabled size="small" style="width: 80px;" placeholder="事件类型"
                                v-model="params.type"></el-input>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item @click.native="selectClickEvent('普通')">普通</el-dropdown-item>
                                <el-dropdown-item @click.native="selectClickEvent('生日')">生日</el-dropdown-item>
                                <el-dropdown-item @click.native="selectClickEvent('纪念日')">纪念日</el-dropdown-item>
                                <el-dropdown-item disabled>其他</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                    </div>
                </el-col>
                <el-col :span="3">
                    <div class="search-item">
                        <span class="search-label">事件标题：</span>
                        <el-input size="small" style="width: 200px;" placeholder="输入事件标题模糊搜索"
                            v-model="params.eTitle"></el-input>
                    </div>
                </el-col>
                <el-col :span="3">
                    <div class="search-item">
                        <span class="search-label">相关人：</span>
                        <el-input size="small" style="width: 200px;" placeholder="输入当事人" v-model="params.uName"></el-input>
                    </div>
                </el-col>
                <el-col :span="4">
                    <el-button size="small" type="success" @click="handleSearchEvent">
                        <i class="el-icon-search"></i>
                        搜索
                    </el-button>
                    <el-button size="small" @click="handleReset">
                        <i class="el-icon-refresh"></i>
                        重置
                    </el-button>
                </el-col>
            </el-row>
        </div>

        <div class="table-container">
            <el-table v-loading="pagination.loading" border :data="pagination.currentTableData"
                :max-height="this.screenHeight" :header-cell-style="{ 'text-align': 'center' }" style="width: 100%">
                <el-table-column label="编码" width="130px" align='center'>
                    <template slot-scope="scope">
                        {{ scope.row.id }}
                    </template>
                </el-table-column>
                <el-table-column label="事件标题">
                    <template slot-scope="scope">
                        {{ scope.row.title | tableTile }}
                    </template>
                </el-table-column>
                <el-table-column label="事件类型" width="100px" align='center'>
                    <template slot-scope="scope">
                        {{ scope.row.type }}
                    </template>
                </el-table-column>
                <el-table-column label="相关人员" width="100px" align='center'>
                    <template slot-scope="scope">
                        {{ scope.row.name }}
                    </template>
                </el-table-column>
                <el-table-column label="开始时间" width="180px" align='center'>
                    <template slot-scope="scope">
                        {{ scope.row.startDate }}
                    </template>
                </el-table-column>
                <el-table-column label="结束时间" width="180px" align='center'>
                    <template slot-scope="scope">
                        {{ scope.row.endDate }}
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" width="180px" align='center'>
                    <template slot-scope="scope">
                        {{ scope.row.createDate }}
                    </template>
                </el-table-column>
                <el-table-column label="创建人" width="100px" align='center'>
                    <template slot-scope="scope">
                        {{ scope.row.createName }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="210" align='center'>
                    <template slot-scope="scope">
                        <el-button size="mini" type="success" @click="handleEdit(scope.$index, scope.row)">查看\编辑
                        </el-button>
                        <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
            </el-pagination>
        </div>
        <!--dialog  -->
        <el-dialog title="事件明细" :visible.sync="dialogFormVisible">
            <el-form :model="scheduleForm" :rules="scheduleFormRules" ref="scheduleForm" label-width="100px"
                class="demo-ruleForm">
                <el-form-item label="事件标题" prop="scheduleTitle">
                    <el-input v-model="scheduleForm.scheduleTitle"></el-input>
                </el-form-item>
                <el-form-item label="事件地址" prop="scheduleAddress">
                    <el-input v-model="scheduleForm.scheduleAddress"></el-input>
                </el-form-item>
                <el-form-item label="开始时间" required>
                    <el-date-picker v-model="scheduleForm.startDate"
                        @change="handleDateChange(scheduleForm.startDate, 'bg')" format="yyyy-MM-dd HH:mm:ss"
                        type="datetime" placeholder="选择开始时间">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="结束时间" required>
                    <el-date-picker v-model="scheduleForm.endDate" @change="handleDateChange(scheduleForm.endDate, 'ed')"
                        type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="选择结束时间">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="事件内容">
                    <el-input type="textarea" v-model="scheduleForm.content"></el-input>
                </el-form-item>
                <el-form-item label="是否短信通知">
                    <el-switch v-model="scheduleForm.isNotice"></el-switch>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input type="textarea" v-model="scheduleForm.note"></el-input>
                </el-form-item>
                <el-form-item label="创建时间：">
                    {{ this.scheduleForm.createDate }}
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleEditAunt">保 存</el-button>
            </div>
        </el-dialog>
    </div>
</template>
  
<script>
import { _getUserAll, _delUserById, _getEventAll, _updEvent, _delEventById } from "@/api/api.js";
import { parseTime } from '../../utils/formatDate';
export default {
    name: "UserListView",
    data() {
        return {
            dialogFormVisible: false,
            screenHeight: 950,
            baseUrl: "",
            inputKeyWord: "",
            inputNewTypeName: "",
            postNewTypeLoading: false,
            tableData: [],
            pagination: {
                listData: null,  //总data
                currentPage: 1, //当前页
                pageSizes: [5, 10, 15, 20],
                pageSize: 20,    //每页显示条数
                totalPage: 1,		//总页数
                currentTableData: [],		//当前页data
                total: 0,  //总数量
                loading: true, 		//table加载样式是否显示
            },
            params: {
                type: '', //事件类型
                eTitle: '',  //标题
                uName: '',   //当事人
                createName: ''   //创建人
            },
            currentUser: {
                id: "",
                name: ""
            },		//当前编辑的user
            scheduleForm: {
                id: "",  //时间编码
                type: '',   //事件类型
                scheduleTitle: '',  //事件标题
                scheduleAddress: '',    //事件地址
                startDate: '',  //事件开始时间
                endDate: '',    //事件结束时间
                content: '',    //事件内容
                isNotice: false,    //是否通知
                note: '',   //事件备注   
                name: '',    //当事人名称
                createNameID: '',  //创建人ID
                createDate: parseTime(new Date().getTime())
            },
            scheduleFormRules: {
                scheduleTitle: [
                    { required: true, message: '请输入事件名称', trigger: 'blur' },
                    { min: 1, max: 15, message: '长度在 3 到 5 个字符', trigger: 'blur' }
                ],
                scheduleAddress: [
                    { required: true, message: '请输入事件发生地', trigger: 'change' }
                ]
            }
        }
    },
    filters: {
        tableTile(value) {
            if (!value) return '';
            if (value.length > 20) {
                return value.slice(0, 20) + '...'
            }
            return value
        },
        dateFormat(val) {
            if (!val) return ''
            else return parseTime(val)
        }
    },
    beforeDestroy() {
        window.removeEventListener("resize", this.updateScreenHeight); // 组件销毁前移除事件监听
    },
    mounted() {
        this.updateScreenHeight(); // 组件挂载时获取屏幕高度
        window.addEventListener("resize", this.updateScreenHeight); // 监听窗口大小变化
    },
    methods: {
        updateScreenHeight() {
            this.screenHeight = window.innerHeight - 300; // 更新屏幕高度
        },
        //初始化
        init() {
            if (this.$store.state.login_user.roles[0].name === 'root') {
                this.params.createName = '';
            } else {
                this.params.createName = this.$store.state.login_user.username;
            }

            this.baseUrl = `/event/getEventAll?type=${this.params.type}&eTitle=${this.params.eTitle}&uName=${this.params.uName}&createName=${this.params.createName}`

            _getEventAll(this.baseUrl).then(res => {
                if (res.status === 200) {
                    if (res.data.data != '') {
                        this.pagination.listData = res.data.data;
                        this.pagination.total = res.data.data.length;

                        this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                        this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                        this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                        this.pagination.loading = false;
                    } else {

                    }
                }
            })
        },
        //分页
        getCurrentPageData(listData, currentPage, pageSize) {
            let begin = (currentPage - 1) * pageSize;
            let end = currentPage * pageSize;
            this.pagination.currentTableData = listData.slice(
                begin,
                end
            );
        },
        //表格操作-删除
        handleDelete(index, row) {
            this.$confirm('此操作将永久删除该标签, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //网络请求
                _delEventById(row.id).then(res => {
                    if (res.data.status === 200) {
                        this.init();
                        this.$message.success('删除成功!');
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },

        //表格操作-编辑
        handleEdit(index, row) {
            this.scheduleForm.id = row.id
            this.scheduleForm.type = row.type
            this.scheduleForm.scheduleTitle = row.title
            this.scheduleForm.scheduleAddress = row.address
            this.scheduleForm.startDate = row.startDate
            this.scheduleForm.endDate = row.endDate
            this.scheduleForm.content = row.content

            if (row.isNotice == "1") {
                this.scheduleForm.isNotice = true
            } else {
                this.scheduleForm.isNotice = false
            }
            this.scheduleForm.note = row.note
            this.scheduleForm.name = row.name
            this.scheduleForm.createNameID = row.createNameID
            this.scheduleForm.createDate = parseTime(row.createDate)

            this.dialogFormVisible = true
        },
        // 保存
        handleEditAunt() {
            if (this.scheduleForm.isNotice == true) {
                this.scheduleForm.isNotice = "1"
            } else {
                this.scheduleForm.isNotice = "0"
            }
            this.scheduleForm.startDate = parseTime(this.scheduleForm.startDate)
            this.scheduleForm.endDate = parseTime(this.scheduleForm.endDate)

            _updEvent(this.scheduleForm).then(res => {
                if (res.data.status === 200) {
                    this.$message.success("编辑成功!");
                    this.init();
                    this.dialogFormVisible = false
                }

            })
            // this.$store.state.date = new Date().getTime()
        },
        /*搜索*/
        handleSearchEvent() {

            if (this.params.type != '') {
                this.baseUrl += "&type="
                this.baseUrl += this.params.type
            }
            if (this.params.eTitle != '') {
                this.baseUrl += "&eTitle="
                this.baseUrl += this.params.eTitle
            }
            if (this.params.uName != '') {
                this.baseUrl += "&uName="
                this.baseUrl += this.params.uName
            }
            if (this.params.createName != '') {
                this.baseUrl += "&createName="
                this.baseUrl += this.params.createName
            }
            _getEventAll(this.baseUrl).then(res => {
                if (res.data.status === 200) {
                    this.baseUrl = '/event/getEventAll?1=1'
                    this.pagination.listData = res.data.data;
                    this.pagination.total = res.data.data.length;

                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                    this.pagination.loading = false;
                } else {
                    this.baseUrl = '/event/getEventAll?1=1'
                    this.$message.error(res.data.msg);
                }
            })
        },
        handleReset() {
            this.params = {
                type: '', //事件类型
                eTitle: '',  //标题
                uName: '',   //当事人
                createName: ''   //创建人
            }
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
        //下拉点击事件
        selectClickEvent(val) {
            this.params.type = val
        },
        handleDateChange(val, type) {
            if (type === 'bg') this.scheduleForm.startDate = parseTime(val)
            else this.scheduleForm.endDate = parseTime(val)
        }
    },
    created() {
        this.init();
    }
}
</script>
  
<style  lang="less" scoped>
@import '@/assets/table-container.css';
</style>
  