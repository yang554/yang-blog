<template>
    <div class="user-list-view">
        <!--搜索-->
        <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="5">
                <span class="span-txt">&emsp;姓名：</span>
                <el-input size="small" style="width: 200px;" placeholder="按姓名搜索" v-model="searchName"></el-input>
            </el-col>
            <el-col :span="1">
                <el-button size="small" type="success" @click="handleSearchEvent">
                    <i class="el-icon-search"></i>
                    搜索
                </el-button>
            </el-col>
            <el-col :span="1">
                <el-button size="small" @click="handleReset">
                    <i class="el-icon-refresh"></i>
                    重置
                </el-button>
            </el-col>
        </el-row>

        <el-table v-loading="pagination.loading" border :data="pagination.currentTableData" style="width: 100%"
            :header-cell-style="{ 'text-align': 'center' }">
            <el-table-column label="编码" width="130px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.id }}
                </template>
            </el-table-column>
            <el-table-column label="姓名" width="100px" align='center'>
                <template slot-scope="scope">
                    <!-- {{ scope.row.e_title | tableTile }} -->
                    {{ scope.row.uName }}
                </template>
            </el-table-column>
            <el-table-column label="开始时间" width="130px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.startDate }}
                </template>
            </el-table-column>
            <el-table-column label="结束时间" width="130px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.endDate }}
                </template>
            </el-table-column>
            <el-table-column label="持续天数" width="80px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.duration }}
                </template>
            </el-table-column>
            <el-table-column label="经血量" width="80px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.bloodVolume }}ml
                </template>
            </el-table-column>
            <el-table-column label="周期长度" width="80px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.cycle }}
                </template>
            </el-table-column>
            <el-table-column label="症状">
                <template slot-scope="scope">
                    {{ scope.row.symptom | tableTile }}
                </template>
            </el-table-column>
            <el-table-column label="备注">
                <template slot-scope="scope">
                    {{ scope.row.note | tableTile }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align='center'>
                <template slot-scope="scope">
                    <el-button size="mini" type="success" @click="handleEdit(scope.$index, scope.row)">查看\编辑
                    </el-button>
                    <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div style="margin-top: 20px;" class="my-border-padding">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
            </el-pagination>
        </div>
        <!--dialog  -->
        <el-dialog title="本次实际记录" :visible.sync="dialogFormVisible">
            <el-form v-model="realAuntForm">
                <el-form-item label="姓名" label-width="120px">
                    <el-input v-model="realAuntForm.name" autocomplete="off" disabled style="width: 50%;">{{
                        realAuntForm.name }}</el-input>
                </el-form-item>
                <el-form-item label="周期长度" label-width="120px">
                    <el-input class="el-form-item__label" v-model="realAuntForm.cycle" autocomplete="off" disabled
                        style="width: 10%;"></el-input>
                    <label class="el-form-item__label">持续时间</label>
                    <el-input class="el-form-item__label" v-model="realAuntForm.duration" autocomplete="off" disabled
                        style="width: 10%;"></el-input>
                    <label class="el-form-item__label">血量</label>
                    <el-input class="el-form-item__label" v-model="realAuntForm.bloodVolume" autocomplete="off"
                        style="width: 10%;"></el-input>ml
                </el-form-item>
                <el-form-item label="开始日期" label-width="120px">
                    <el-date-picker class="el-form-item__label" disabled v-model="realAuntForm.startDate" type="date"
                        placeholder="选择开始时间">
                    </el-date-picker>
                    <label class="el-form-item__label">结束日期</label>
                    <el-date-picker class="el-form-item__label" disabled v-model="realAuntForm.endDate" type="date"
                        placeholder="选择结束时间">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="症状" label-width="120px">
                    <el-input class="el-form-item__label" type="textarea" :rows="5"
                        placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等" v-model="realAuntForm.symptom"
                        style="width: 40%;"></el-input>
                    <label class="el-form-item__label">备注</label>
                    <el-input class="el-form-item__label" type="textarea" placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。" :rows="5"
                        v-model="realAuntForm.note" style="width: 40%;"></el-input>
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
import { _getByALL, _selectByName, _updAunt, _delAunt } from "@/api/api.js";
export default {
    name: "AuntList",
    data() {
        return {
            dialogFormVisible: false,
            tableData: [],
            pagination: {
                listData: null,  //总data
                currentPage: 1, //当前页
                pageSizes: [5, 10, 15, 20],
                pageSize: 10,    //每页显示条数
                totalPage: 1,		//总页数
                currentTableData: [],		//当前页data
                total: 0,  //总数量
                loading: true, 		//table加载样式是否显示
            },
            searchName: '',  //搜索的用户名
            realAuntForm: {
                id: '',
                name: '',
                startDate: '',   //开始日期
                endDate: '',     //结束日期
                duration: '',    //持续时间
                cycle: '',      //周期长度
                bloodVolume: '', //姨妈血量
                symptom: '', //症状
                note: '', //备注
                uStatus: ''
            },
        }
    },
    filters: {
        tableTile(value) {
            if (!value) return '';
            if (value.length > 19) {
                return value.slice(0, 19) + '...'
            }
            return value
        },
    },
    methods: {
        //初始化
        init() {
            _getByALL().then(res => {
                if (res.status === 200) {
                    if (res.data.obj != '') {
                        this.pagination.listData = res.data.obj;
                        this.pagination.total = res.data.obj.length;

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
                _delAunt(row.id).then(res => {
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
            this.realAuntForm.id = row.id
            this.realAuntForm.name = row.uName
            this.realAuntForm.startDate = row.startDate
            this.realAuntForm.endDate = row.endDate
            this.realAuntForm.duration = row.duration
            this.realAuntForm.cycle = row.cycle
            this.realAuntForm.bloodVolume = row.bloodVolume
            this.realAuntForm.symptom = row.symptom
            this.realAuntForm.note = row.note
            this.dialogFormVisible = true
        },
        // 保存当前记录
        handleEditAunt() {
            _updAunt(this.realAuntForm).then(res => {
                this.$message.success("编辑成功!");
                this.dialogFormVisible = false
            })
            this.$store.state.date = new Date().getTime()
        },
        /*搜索*/
        handleSearchEvent() {
            _selectByName(this.searchName).then(res => {
                if (res.data.status === 200) {
                    this.pagination.listData = res.data.obj;
                    this.pagination.total = res.data.obj.length;

                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                    this.pagination.loading = false;
                } else {
                    this.$message.error(res.data.msg);
                }
            })
        },
        handleReset() {
            this.searchName = ''
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
    },
    created() {
        this.init();
    }
}
</script>
  
<style  lang="less" scoped>
.span-txt {
    font-size: 14px;
    color: black;
}
</style>
  