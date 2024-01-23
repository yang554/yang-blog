<template>
    <el-form :model="auntForm" :rules="rules" ref="auntForm" label-width="100px" class="demo-auntForm">
        <el-form-item label="姓名" prop="name">
            <!-- <el-input v-model="auntForm.name" style="width: 30%;"></el-input> -->
            <el-select v-model="auntForm.name" filterable placeholder="请选择用户">
                <el-option v-for="item in uNameAll" :label="item.username" :value="item.username">
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="上次起始日" required>
            <el-col :span="6">
                <el-form-item prop="auntDate">
                    <el-date-picker @change="auntDateChange" v-model="auntForm.auntDate" type="daterange"
                        range-separator="~" start-placeholder="上次开始日期" end-placeholder="上次结束日期"
                        :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>
            </el-col>
        </el-form-item>
        <el-form-item label="持续时间(天)" prop="duration">
            <el-input :disabled="true" style="width: 6%;" v-model="auntForm.duration"></el-input>
        </el-form-item>
        <el-form-item label="血量(ml)" prop="bloodVolume">
            <el-input @input="auntForm.bloodVolume = auntForm.bloodVolume.replace(/[^\d.]/g, '')" style="width: 6%;"
                v-model="auntForm.bloodVolume"></el-input>
        </el-form-item>
        <el-form-item label="周期长度" prop="cycle">
            <el-radio-group v-model="auntForm.cycle">
                <el-radio label="28天"></el-radio>
                <el-radio label="29天"></el-radio>
                <el-radio label="30天"></el-radio>
                <el-radio label="31天"></el-radio>
                <el-radio label="32天"></el-radio>
                <el-radio label="33天"></el-radio>
                <el-radio label="34天"></el-radio>
                <el-radio label="35天"></el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="症状/表现" prop="symptom">
            <el-input type="textarea" :rows="5" placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等" v-model="auntForm.symptom"
                style="width: 40%;"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="note">
            <el-input type="textarea" placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。" :rows="5" v-model="auntForm.note"
                style="width: 40%;"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="auntAddForm('auntForm')">立即创建</el-button>
            <el-button @click="resetForm('auntForm')">重置</el-button>
        </el-form-item>
    </el-form>
</template>
<script>
import { parseTime } from '../../utils/calendar'
import { _addAunt, _getUserAll } from "@/api/api.js"
export default {
    data() {
        return {
            auntForm: {
                name: '',
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
                name: [
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
            uNameAll: []
        };
    },
    methods: {
        auntAddForm(anutTable) {
            this.$refs[anutTable].validate((valid) => {
                if (valid) {
                    _addAunt(this.auntForm).then(res => {
                        this.$message.success("创建成功!");
                        this.$refs["auntForm"].resetFields();
                    })
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(anutTable) {
            this.$refs[anutTable].resetFields();
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
                console.log(res.data)
                this.uNameAll = res.data
            })
        }

    },
    created() {
        this.getUserNameAll();
    }



}
</script>