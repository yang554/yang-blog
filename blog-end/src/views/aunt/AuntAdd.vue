<template>
    <div class="container">
        <div class="card">
            <div class="form-title">
                <i class="fas fa-edit"></i>
                <span>填写姨妈记录信息</span>
            </div>

            <el-form :model="auntForm" :rules="rules" ref="auntForm" label-width="120px" class="demo-auntForm">
                <div class="form-section">
                    <el-form-item label="姓名" prop="uID">
                        <el-select v-model="auntForm.uID" filterable placeholder="请选择用户" style="width: 100%">
                            <el-option v-for="(item, index) in uNameAll" :key="index" :label="item.username"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </div>

                <div class="form-section">
                    <el-form-item label="上次起始日" prop="auntDate" required>
                        <el-date-picker @change="auntDateChange" v-model="auntForm.auntDate" type="daterange"
                            range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
                            :picker-options="pickerOptions" style="width: 100%">
                        </el-date-picker>
                    </el-form-item>


                </div>

                <div class="form-section">
                    <el-form-item label="血量(ml)" prop="bloodVolume">
                        <el-input v-model="auntForm.bloodVolume" placeholder="请输入血量"
                            @input="auntForm.bloodVolume = auntForm.bloodVolume.replace(/[^\d]/g, '')">
                            <template slot="append">ml</template>
                        </el-input>
                        <div style="margin-top: 10px; font-size: 13px; color: #888;">
                            <span v-if="auntForm.bloodVolume < 20">非常少</span>
                            <span v-else-if="auntForm.bloodVolume < 50">较少</span>
                            <span v-else-if="auntForm.bloodVolume < 80">正常</span>
                            <span v-else-if="auntForm.bloodVolume < 120">较多</span>
                            <span v-else>非常多</span>
                        </div>
                    </el-form-item>
                </div>

                <div class="form-section">
                    <el-form-item label="周期长度" prop="cycle">
                        <div class="cycle-options">
                            <el-radio v-for="day in cycleDays" :key="day" :label="`${day}天`" v-model="auntForm.cycle">
                                {{ day }}天
                            </el-radio>
                        </div>

                        <div class="custom-cycle">
                            自定义
                            <el-input v-model="customCycle" placeholder="输入天数" style="width: 150px; margin-left: 10px;"
                                @input="customCycle = customCycle.replace(/[^\d^\.]+/g, '')
                                        .replace(/^0+(\d)/, '$1')
                                        .replace(/^\./, '0.')
                                        .match(/^\d*(\.?\d{0,2})/g)[0]
                                        .slice(0, 5)">
                                <template slot="append">天</template>
                            </el-input>
                        </div>
                    </el-form-item>
                    <div class="stats-container">
                        <div class="stat-card">
                            <div class="stat-label">持续时间</div>
                            <div class="stat-value">{{ auntForm.duration || 0 }}</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-label">平均周期长度</div>
                            <div class="stat-value">{{ auntForm.cycle || 0 }}</div>
                        </div>

                        <!-- <div class="stat-card">
                            <div class="stat-label">距离上次</div>
                            <div class="stat-value">{{ daysSinceLast || 0 }} 天</div>
                        </div> -->
                    </div>
                </div>

                <div class="form-section">
                    <el-form-item label="症状/表现" prop="symptom">
                        <el-input type="textarea" :rows="3" placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等"
                            v-model="auntForm.symptom"></el-input>

                        <div class="symptom-tags">
                            <div class="symptom-tags">
                                <div v-for="(symptom, index) in commonSymptoms" :key="index" class="symptom-tag"
                                    :class="{ active: selectedSymptoms.includes(symptom) }" @click="toggleSymptom(symptom)">
                                    {{ symptom }}
                                </div>
                            </div>
                        </div>
                    </el-form-item>
                </div>

                <div class="form-section">
                    <el-form-item label="备注" prop="note">
                        <el-input type="textarea" :rows="3" placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。"
                            v-model="auntForm.note"></el-input>
                    </el-form-item>
                </div>

                <el-form-item class="footer-buttons">
                    <el-button type="primary" @click="auntAddForm('auntForm')">
                        <i class="fas fa-save"></i> 保存记录
                    </el-button>
                    <el-button @click="resetForm('auntForm')">
                        <i class="fas fa-redo"></i> 重置表单
                    </el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
<script>
import { parseTime } from '../../utils/calendar'
import { _addAunt, _getUserAll } from "@/api/api.js"
export default {
    data() {
        return {
            auntForm: {
                uID: '',
                auntDate: '',  //日期
                startDate: '',   //开始日期
                endDate: '',     //结束日期
                duration: '',    //持续时间
                symptom: '',    //症状
                cycle: '',      //周期长度
                bloodVolume: '', //姨妈血量
                note: '',     //备注
                uStatus: '0'    //状态
            },
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now();
                },
            },
            rules: {
                uID: [
                    { required: true, message: '请输入姓名', trigger: 'blur' },
                ],
                auntDate: [
                    {
                        type: 'array',
                        required: true,
                        message: '请选择日期区间',
                        fields: {
                            0: { type: 'date', required: true, message: '请选择开始日期' },
                            1: { type: 'date', required: true, message: '请选择结束日期' },
                        }
                    }
                ],
                cycle: [
                    { required: true, message: '请选择周期长度', trigger: 'change' }
                ]
            },
            uNameAll: [],
            cycleDays: [26, 27, 28, 29, 30, 31, 32, 33, 34, 35],
            commonSymptoms: [
                '腹痛', '乳房胀痛', '情绪波动', '疲劳',
                '头痛', '腰酸背痛', '食欲变化', '失眠', '水肿'
            ],
            selectedSymptoms: [],
            customCycle: '',
        };
    },
    watch: {
        customCycle(newVal) {
            if (newVal) {
                this.auntForm.cycle = `${newVal}天`;
            }
        }
    },
    methods: {
        auntAddForm(anutTable) {
            this.$refs[anutTable].validate((valid) => {
                if (valid) {
                    setTimeout(() => {
                        _addAunt(this.auntForm).then(res => {
                            this.$message.success("创建成功!");
                            this.resetForm('auntForm');
                        })
                    }, 800);

                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(anutTable) {
            this.$refs[anutTable].resetFields();
            this.selectedSymptoms = [];
            this.auntForm.duration = 0;
        },
        auntDateChange(val) {
            const sd = Date.parse(val[0])
            const ed = Date.parse(val[1])
            const num = ed - sd

            this.auntForm.duration = Math.floor(num / (1000 * 3600 * 24)) + "天"
            this.auntForm.startDate = parseTime(new Date(sd))
            this.auntForm.endDate = parseTime(new Date(ed))
        },
        getUserNameAll() {
            _getUserAll().then(res => {
                this.uNameAll = res.data
            })
        },
        toggleSymptom(symptom) {
            const index = this.selectedSymptoms.indexOf(symptom);
            if (index === -1) {
                this.selectedSymptoms.push(symptom);
            } else {
                this.selectedSymptoms.splice(index, 1);
            }

            // 更新症状文本
            this.auntForm.symptom = this.selectedSymptoms.join('，');
        }

    },
    created() {
        this.getUserNameAll();
    }



}
</script>
<style lang="less" scoped>
.container {
    width: 100%;
    margin: 0 auto;
}

.header {
    text-align: center;
    margin-bottom: 30px;
    position: relative;
}

.header h1 {
    color: #8a4fff;
    font-size: 32px;
    margin-bottom: 10px;
    position: relative;
    display: inline-block;
}

.header h1::after {
    content: "";
    position: absolute;
    bottom: -8px;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 4px;
    background: linear-gradient(to right, #ff7eb3, #8a4fff);
    border-radius: 2px;
}

.header p {
    color: #7d7d7d;
    font-size: 16px;
    max-width: 600px;
    margin: 0 auto;
    line-height: 1.6;
}

.card {
    background-color: white;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(138, 79, 255, 0.15);
    overflow: hidden;
    padding: 30px;
    margin-bottom: 30px;
    transition: transform 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
}

.form-title {
    display: flex;
    align-items: center;
    color: #8a4fff;
    font-size: 20px;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #f0e6ff;
}

.form-title i {
    margin-right: 10px;
    font-size: 24px;
}

.el-form-item {
    margin-bottom: 22px;
}

.el-input,
.el-select,
.el-textarea,
.el-date-editor {
    width: 100%;
}

.duration-box {
    display: flex;
    align-items: center;
    background: #f9f4ff;
    padding: 8px 15px;
    border-radius: 4px;
    border: 1px solid #e6d6ff;
}

.duration-value {
    font-weight: bold;
    color: #8a4fff;
    margin-left: 8px;
}

.symptom-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 10px;
}

.symptom-tag {
    cursor: pointer;
    padding: 6px 15px;
    border-radius: 20px;
    background: #f5eeff;
    border: 1px solid #e6d6ff;
    color: #7a5cbd;
    transition: all 0.2s;
}

.symptom-tag:hover {
    background: #eadaff;
}

.symptom-tag.active {
    background: #8a4fff;
    color: white;
    border-color: #8a4fff;
}

.footer-buttons {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 30px;
}

.el-button {
    padding: 12px 28px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s;
}

.el-button--primary {
    background: linear-gradient(to right, #ff7eb3, #8a4fff);
    border: none;
}

.el-button--primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(138, 79, 255, 0.3);
}

.cycle-options {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
}

.custom-cycle {
    display: flex;
    align-items: center;
    margin-top: 10px;
}

.custom-cycle input {
    width: 80px;
    margin: 0 10px;
}

.stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 20px;
    margin-top: 20px;
}

.stat-card {
    background: #f9f4ff;
    border-radius: 12px;
    padding: 20px;
    text-align: center;
    border: 1px solid #e6d6ff;
}

.stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #8a4fff;
    margin: 10px 0;
}

.stat-label {
    color: #7a5cbd;
    font-size: 14px;
}

.success-animation {
    text-align: center;
    padding: 30px;
}

.success-animation i {
    font-size: 64px;
    color: #8a4fff;
    margin-bottom: 20px;
    animation: pulse 1.5s infinite;
}

@keyframes pulse {
    0% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.1);
    }

    100% {
        transform: scale(1);
    }
}

.form-section {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px dashed #eee;
}

@media (max-width: 768px) {
    .cycle-options {
        grid-template-columns: repeat(2, 1fr);
    }

    .stats-container {
        grid-template-columns: 1fr;
    }

    .card {
        padding: 20px;
    }

    .el-form-item__label {
        display: block;
        width: 100% !important;
        text-align: left;
        margin-bottom: 8px;
    }

    .el-form-item__content {
        margin-left: 0 !important;
    }

    .footer-buttons {
        flex-direction: column;
        gap: 10px;
    }

    .footer-buttons .el-button {
        width: 100%;
    }
}
</style>