<template>
    <div>
        <div>
            <!-- 配置表单 -->
            <el-form :model="entity" :rules="rules" ref="formRef" :inline="true">
                <el-form-item label="教师评级">
                    <el-select style="width: 80px;" v-model="entity.teacherLevel" placeholder="请选择" size="small">
                        <el-option v-for="level in 12" :key="level" :label="`${level}级`" :value="level">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="年级">
                    <el-select style="width: 100px;" v-model="entity.grade" placeholder="请选择" size="small">
                        <el-option label="小学" value="小学"></el-option>
                        <el-option label="初中" value="初中"></el-option>
                        <el-option label="高一二" value="高一二"></el-option>
                        <el-option label="高三" value="高三"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="授课类型">
                    <el-select style="width: 100px;" v-model="entity.classType" placeholder="请选择" size="small">
                        <el-option label="一对一" value="一对一"></el-option>
                        <el-option label="班课" value="班课"></el-option>
                    </el-select>
                </el-form-item>
                <!-- 班课相关字段 -->
                <el-form-item label="学生人数">
                    <el-input-number size="small" :disabled="entity.classType === '一对一'" v-model="entity.studentCount"
                        :min="1" :max="300">
                    </el-input-number>
                </el-form-item>
                <el-form-item label="班课类型">
                    <el-select size="small" :disabled="entity.classType === '一对一'" style="width: 100px;"
                        v-model="entity.classCategory" placeholder="请选择">
                        <el-option label="正价班" value="正价班"></el-option>
                        <el-option label="流量班" value="流量班"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="addClassFee" size="small">添加课时</el-button>
                </el-form-item>
                <el-form-item>
                    <el-upload class="upload-demo" action="" :auto-upload="false" :on-change="handleImport"
                        :show-file-list="false">
                        <el-button type="success" size="small">
                            导入课时
                        </el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item>
                    <el-button type="warning" @click="exportToExcel" size="small">导出课时</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="weight-config">
            <el-table :data="pagination.currentTableData" border style="width: 150%" height="600"
                :max-height="this.screenHeight" :header-cell-style="{ 'text-align': 'center' }">
                <el-table-column prop="teacherLevel" label="教师评级" align="center">
                    <template #default="{ row }">
                        <!-- <el-input v-model="row.teacherLevel"></el-input> -->
                        {{ row.teacherLevel }}级
                    </template>
                </el-table-column>
                <el-table-column prop="grade" label="年级" align="center">
                    <template #default="{ row }">
                        <!-- <el-input v-model="row.grade"></el-input> -->
                        {{ row.grade }}
                    </template>
                </el-table-column>
                <el-table-column prop="classType" label="授课类型" align="center">
                    <template #default="{ row }">
                        <!-- <el-input v-model="row.classType"></el-input> -->
                        {{ row.classType }}
                    </template>
                </el-table-column>
                <el-table-column prop="classCategory" label="班课类型" align="center">
                    <template #default="{ row }">
                        <!-- <el-input v-model="row.classCategory"></el-input> -->
                        {{ row.classCategory }}
                    </template>
                </el-table-column>
                <el-table-column prop="studentCount" label="学生人数" align="center">
                    <template #default="{ row }">
                        <el-input-number v-model="row.studentCount" :min="1"></el-input-number>
                    </template>
                </el-table-column>
                <el-table-column label="课时费(￥)" align="center">
                    <template #default="{ row }">
                        <!-- <el-input v-model="row.fee"></el-input> -->
                        {{ calculateFee(row) }}
                    </template>
                </el-table-column>
                <el-table-column label="日期" align="center">
                    <template #default="{ row }">
                        <el-date-picker v-model="row.date" type="date" placeholder="选择日期">
                        </el-date-picker>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" align="center">
                    <template #default="{ row }">
                        <el-button type="danger" size="mini" @click="deleteClassFee(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

        </div>
        <!-- 分页 -->
        <div style="margin-top: 20px" class="my-border-padding">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="pagination.total"></el-pagination>

        </div>
        <!-- 统计信息 -->
        <div class="stats">
            <span v-if="this.pagination.total > 0" style="color: #FFD700;">总金额：{{ totalFee }}元</span>&ensp;&ensp;
            <el-button type="success" @click="saveBtn">保存</el-button>
        </div>
    </div>
</template>
  
<script>
import * as XLSX from 'xlsx';
import FileSaver from 'file-saver';
import { _getPrizeAll, _operationPrize } from "@/api/api.js";
import { getTime, parseTime } from "@/utils/formatDate";
export default {
    data() {
        return {
            screenHeight: 950,
            // 基础课时费表
            baseFees: {
                '小学': {
                    1: 100, 2: 105, 3: 110, 4: 115, 5: 130, 6: 135,
                    7: 140, 8: 145, 9: 150, 10: 155, 11: 160, 12: 170
                },
                '初中': {
                    1: 105, 2: 110, 3: 115, 4: 120, 5: 140, 6: 150,
                    7: 160, 8: 170, 9: 180, 10: 190, 11: 200, 12: 210
                },
                '高一二': {
                    1: 120, 2: 125, 3: 130, 4: 140, 5: 160, 6: 170,
                    7: 180, 8: 190, 9: 200, 10: 210, 11: 220, 12: 230
                },
                '高三': {
                    1: 125, 2: 130, 3: 140, 4: 150, 5: 170, 6: 180,
                    7: 190, 8: 200, 9: 210, 10: 220, 11: 230, 12: 250
                }
            },
            // 小学初中班课增加额表
            primaryMiddleExtra: {
                5: 50, 6: 70, 7: 90, 8: 120, 9: 140, 10: 160,
                11: 180, 12: 220, 13: 240, 14: 280, 15: 300
            },
            // 高中班课增加额表
            highSchoolExtra: {
                5: 60, 6: 80, 7: 100, 8: 130, 9: 150, 10: 170,
                11: 190, 12: 230, 13: 250, 14: 290, 15: 310
            },
            entity: {
                id: '' + getTime("start"),
                teacherLevel: 2,
                grade: '初中',
                classType: '一对一',
                studentCount: 1,
                classCategory: '正价班',
                fee: '0',
                date: parseTime(new Date().toLocaleDateString())
            },
            records: [], // 存储批量计算记录
            pagination: {
                listData: [], //总data
                currentPage: 1, //当前页
                pageSizes: [5, 10],
                pageSize: 10, //每页显示条数
                totalPage: 1, //总页数
                currentTableData: [], //当前页data
                total: 0, //总数量
                loading: false //table加载样式是否显示
            },
            form: {
                id: '' + getTime("start"),
                name: '',
                number: 1,
                weight: 1,
                calc: '0',
            },
            rules: {
                name: [{ required: true, message: '请输入奖品名称', trigger: 'blur' }],
                number: [{ required: true, message: '请输入奖品数量', trigger: 'blur' }],
                weight: [{ required: true, message: '请输入权重值', trigger: 'blur' }]
            }
        }
    },
    mounted() {
        this.updateScreenHeight(); // 组件挂载时获取屏幕高度
        window.addEventListener("resize", this.updateScreenHeight); // 监听窗口大小变化
        // 监听Tab键事件
        window.addEventListener("keyup", this.handleKeyUp);
    },
    beforeDestroy() {
        window.removeEventListener("resize", this.updateScreenHeight); // 组件销毁前移除事件监听
    },
    computed: {
        totalFee() {
            return this.pagination.listData.reduce((sum, item) => sum + item.fee, 0)
        },
        isFormValid() {
            return this.entity.teacherLevel && this.entity.grade;
        }
    },
    methods: {
        updateScreenHeight() {
            this.screenHeight = window.innerHeight - 300; // 更新屏幕高度
        },
        /*分页事件*/
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.getCurrentPageData(
                this.pagination.listData,
                this.pagination.currentPage,
                this.pagination.pageSize
            );
        },
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.getCurrentPageData(
                this.pagination.listData,
                this.pagination.currentPage,
                this.pagination.pageSize
            );
        },
        //分页
        getCurrentPageData(listData, currentPage, pageSize) {
            let begin = (currentPage - 1) * pageSize;
            let end = currentPage * pageSize;
            this.pagination.currentTableData = listData.slice(begin, end);
        },
        getBaseFee(grade, level) {
            if (this.baseFees[grade] && this.baseFees[grade][level]) {
                return this.baseFees[grade][level];
            }
            throw new Error(`无效的年级或教师等级: ${grade} ${level}`);
        },
        getExtraFee(grade, studentCount) {
            const isHighSchool = grade === '高一二' || grade === '高三';
            console.log(isHighSchool)
            const extraMap = isHighSchool ? this.highSchoolExtra : this.primaryMiddleExtra;
            // 处理学生人数大于15的情况
            let count = studentCount //Math.min(studentCount, 15);
            // 查找最接近的学生人数
            while (count > 4 && !extraMap.hasOwnProperty(count)) {
                count--;
            }
            return extraMap[count] || 0;
        },
        // 添加课时
        addClassFee() {
            this.$refs.formRef.validate(valid => {
                if (valid) {
                    if (this.pagination.pageSize == this.pagination.currentTableData.length) {
                        this.pagination.currentPage = this.pagination.currentPage + 1;
                    }
                    if (this.entity.classType === '一对一') {
                        this.entity.classCategory = '-';
                        this.entity.studentCount = '1';
                    }
                    this.pagination.listData.push({ ...this.entity });
                    // this.entity = { id: '' + getTime("start"), teacherLevel: '2', grade: '初中', classType: '一对一', studentCount: 1, classCategory: '正价班', fee: 0 } // 初始化表单
                    this.entity.id = '' + getTime("start");

                    this.pagination.total = this.pagination.listData.length;
                    this.pagination.totalPage = Math.ceil(
                        this.pagination.listData.length / this.pagination.pageSize
                    );
                    this.pagination.totalPage =
                        this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                    this.getCurrentPageData(
                        this.pagination.listData,
                        this.pagination.currentPage,
                        this.pagination.pageSize
                    );

                }
            })
        },
        // 删除课时
        deleteClassFee(row) {
            this.pagination.listData = this.pagination.listData.filter(
                item => item.id !== row.id
            );
            this.pagination.total = this.pagination.listData.length;

            this.pagination.totalPage = Math.ceil(
                this.pagination.listData.length / this.pagination.pageSize
            );
            this.pagination.totalPage =
                this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
            this.getCurrentPageData(
                this.pagination.listData,
                this.pagination.currentPage,
                this.pagination.pageSize
            );
            if (
                this.pagination.currentTableData.length == 0 &&
                this.pagination.listData.length > 0
            ) {
                this.pagination.currentPage = this.pagination.currentPage - 1;
                this.getCurrentPageData(
                    this.pagination.listData,
                    this.pagination.currentPage,
                    this.pagination.pageSize
                );
            }
        },
        // 处理文件导入
        handleImport(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                try {
                    const data = new Uint8Array(e.target.result);
                    const workbook = XLSX.read(data, { type: 'array' });

                    // 获取第一个工作表
                    const firstSheetName = workbook.SheetNames[0];
                    const worksheet = workbook.Sheets[firstSheetName];

                    //处理Excel日期
                    const convertExcelDate = (excelDate) => {
                        // Excel 日期起点 (1900-01-01) 到 Unix 时间戳的偏移量
                        const offset = (excelDate - 25569) * 86400 * 1000;
                        const date = new Date(offset);

                        // 处理时区偏移（可选）
                        date.setMinutes(date.getMinutes() + date.getTimezoneOffset());

                        return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
                    };

                    // 转换为JSON
                    const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

                    // 移除表头
                    if (jsonData.length > 0) jsonData.shift();

                    // 处理导入的数据并添加到记录列表
                    this.pagination.listData = jsonData.filter(row => row && row.length >= 3).map(row => {
                        // 确保行数据有足够的列
                        const record = {
                            id: '' + getTime("start"),
                            teacherLevel: parseInt(row[0]) || 1,
                            grade: row[1] || '初中',
                            classType: row[2] || '一对一',
                            studentCount: parseInt(row[3]) || 1,
                            classCategory: row[4] || '正价班',
                            date: typeof row[6] === 'number' && row[6] > 10000 // 简单判断是否为日期值
                                ? convertExcelDate(row[6])
                                : row[6]
                        };

                        // 计算课时费
                        record.fee = this.calculateFee(record);
                        return record;
                    });
                    this.pagination.total = this.pagination.listData.length;

                    this.pagination.totalPage = Math.ceil(
                        this.pagination.listData.length / this.pagination.pageSize
                    );
                    this.pagination.totalPage =
                        this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                    this.getCurrentPageData(
                        this.pagination.listData,
                        this.pagination.currentPage,
                        this.pagination.pageSize
                    );
                    this.$message.success(`成功导入 ${this.pagination.listData.length} 条记录`);
                } catch (error) {
                    console.error('导入失败:', error);
                    this.$message.error('导入失败: ' + error.message);
                }
            };

            reader.onerror = () => {
                this.$message.error('文件读取失败');
            };

            reader.readAsArrayBuffer(file.raw);
        },
        // 导出到Excel
        exportToExcel() {
            // 创建工作簿
            const wb = XLSX.utils.book_new();
            // 创建表头
            const headers = [
                [
                    '教师等级', '年级', '授课类型', '学生人数', '班课类型', '课时费(元)', '日期'
                ]
            ];
            // 创建数据
            const data = this.pagination.listData.map(record => [
                record.teacherLevel,
                record.grade,
                record.classType,
                record.classType === '班课' ? record.studentCount : '-',
                record.classType === '班课' ? record.classCategory : '-',
                record.fee,
                record.date,
            ]);

            // 合并表头和数据
            const wsData = [...headers, ...data];

            // 创建工作表
            const ws = XLSX.utils.aoa_to_sheet(wsData);

            // 设置列宽
            const colWidths = [
                { wch: 10 }, // 教师等级
                { wch: 8 },  // 年级
                { wch: 12 }, // 授课类型
                { wch: 10 }, // 学生人数
                { wch: 12 }, // 班课类型
                { wch: 12 },  // 课时费
                { wch: 20 }  // 日期
            ];
            ws['!cols'] = colWidths;

            // 将工作表添加到工作簿
            XLSX.utils.book_append_sheet(wb, ws, '课时费计算结果');

            // 生成Excel文件
            const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });

            // 保存文件
            const blob = new Blob([wbout], { type: 'application/octet-stream' });
            FileSaver.saveAs(blob, '课时费计算结果.xlsx');

            this.$message.success('导出成功');
        },
        // 计算单个课时费
        calculateFee(row) {
            try {
                const { teacherLevel, grade, classType, studentCount, classCategory, fee } = row;
                const baseFee = this.getBaseFee(grade, teacherLevel);
                if (classType === '一对一') {
                    row.fee = baseFee;
                    return row.fee;
                }

                if (studentCount < 5) {
                    row.fee = baseFee;
                } else if (classCategory === '流量班') {
                    row.fee = baseFee + 10 * (studentCount - 1);
                } else {
                    row.fee = baseFee + this.getExtraFee(grade, studentCount);
                }
                return row.fee
            } catch (error) {
                console.error(error);
                this.$message.error('计算失败: ' + error.message);
                row.fee = null;
                return row.fee
            }
        },
        // 保存配置（用本地存储）
        saveBtn() {

            this.$message.info('暂未开发')
            // localStorage.setItem('prizeConfig', JSON.stringify(this.pagination.listData))
            // _operationPrize(this.pagination.listData).then((res) => {
            //     this.$message.success('配置已保存')
            // })
        },

        // 加载配置
        loadConfig() {
            const data = localStorage.getItem('prizeConfig')
            if (data) {
                let res = JSON.parse(data)
                this.pagination.listData = res;
                this.pagination.total = res.length;

                this.pagination.totalPage = Math.ceil(
                    this.pagination.listData.length / this.pagination.pageSize
                );
                this.pagination.totalPage =
                    this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                this.getCurrentPageData(
                    this.pagination.listData,
                    this.pagination.currentPage,
                    this.pagination.pageSize
                );
            } else {
                _getPrizeAll().then((res) => {
                    this.pagination.listData = res.data;
                    this.pagination.total = res.length;

                    this.pagination.totalPage = Math.ceil(
                        this.pagination.listData.length / this.pagination.pageSize
                    );
                    this.pagination.totalPage =
                        this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                    this.getCurrentPageData(
                        this.pagination.listData,
                        this.pagination.currentPage,
                        this.pagination.pageSize
                    );
                })
            }
        }
    },
    created() {
        // this.loadConfig()
    }
}
</script>
  
<style scoped>
.el-pagination__jump {
    color: #f3f3f3 !important;
}

.weight-config {
    /* max-width: 1000px; */
    /* margin: 20px auto; */
    /* padding: 20px; */
    /* background: #fff; */
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stats {
    text-align: right;
    margin-right: 60px;
    /* background: #f5f7fa; */
    border-radius: 4px;
}

.el-form-item {
    margin-bottom: 15px;
}
</style>