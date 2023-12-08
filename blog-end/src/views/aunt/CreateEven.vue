<template>
    <div>
        <el-menu :default-active="activeTab" class="el-menu-demo" mode="horizontal" @select="handleSelect">
            <el-menu-item index="0">日程</el-menu-item>
            <el-menu-item index="1">生日</el-menu-item>
            <el-menu-item index="2">纪念日</el-menu-item>
            <el-menu-item index="3">倒数日</el-menu-item>
            <el-menu-item index="4">其他</el-menu-item>

        </el-menu>
        <div class="line">
            <h1></h1>
        </div>
        <el-form :model="scheduleForm" :rules="scheduleFormRules" ref="scheduleForm" label-width="100px" class="demo-ruleForm"
            v-show="this.isScheduleTab">
            <el-form-item label="事件标题" prop="scheduleTitle">
                <el-input v-model="scheduleForm.scheduleTitle"></el-input>
            </el-form-item>
            <el-form-item label="事件地址" prop="scheduleAddress">
                <el-input v-model="scheduleForm.scheduleAddress"></el-input>
            </el-form-item>
            <el-form-item  label="开始时间" required>
                <el-date-picker v-model="scheduleForm.startDate" @change="handleDateChange(scheduleForm.startDate,'bg')" format="yyyy-MM-dd HH:mm:ss" type="datetime" placeholder="选择开始时间">
                </el-date-picker>
            </el-form-item>
            <el-form-item label="结束时间" required>
                <el-date-picker v-model="scheduleForm.endDate" @change="handleDateChange(scheduleForm.endDate,'ed')" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="选择结束时间">
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
            <el-form-item>
                <el-button type="primary" @click="submitForm('scheduleForm')">立即创建</el-button>
                <el-button @click="$router.push('/home/aunt/AuntDashBoard')">返回</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import { _addEvent } from "@/api/api.js";
import { parseTime } from '../../utils/formatDate';


export default {
    data() {
        return {
            uid:"",
            createDateDef:"",
            activeTab: '0', //选中的分页idex
            isScheduleTab: true, //日程
            isBirthdayTab: false,    //生日
            isMarkTab: false,    //纪念日
            isReciprocalTab: false,  //倒数日
            isOtherTab: false,   //其他
            scheduleForm: {
                type:'普通',   //事件类型
                scheduleTitle: '',  //事件标题
                scheduleAddress: '',    //事件地址
                startDate: '',  //事件开始时间
                endDate: '',    //事件结束时间
                content: '',    //事件内容
                isNotice: false,    //是否通知
                note: '',   //事件备注   
                name:'',    //当事人名称
                createNameID:'',  //创建人ID
                createDate:parseTime(new Date().getTime())

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
        };
    },
    methods: {
        handleSelect(key, keyPath) {
            if (key === "0") {
                console.log(key, keyPath);
                this.isScheduleTab = true
                this.isBirthdayTab = false   //生日
                this.isMarkTab = false   //纪念日
                this.isReciprocalTab = false //倒数日
                this.isOtherTab = false //其他
            } else if (key === "1") {
                this.isScheduleTab = false
                this.isBirthdayTab = true   //生日
                this.isMarkTab = false   //纪念日
                this.isReciprocalTab = false //倒数日
                this.isOtherTab = false //其他
            } else if (key === "2") {
                this.isScheduleTab = false
                this.isBirthdayTab = false   //生日
                this.isMarkTab = true   //纪念日
                this.isReciprocalTab = false //倒数日
                this.isOtherTab = false //其他
            } else if (key === "3") {
                this.isScheduleTab = false
                this.isBirthdayTab = false   //生日
                this.isMarkTab = false   //纪念日
                this.isReciprocalTab = true //倒数日
                this.isOtherTab = false //其他
            } else if (key === "4") {
                this.isScheduleTab = false
                this.isBirthdayTab = false   //生日
                this.isMarkTab = false   //纪念日
                this.isReciprocalTab = false //倒数日
                this.isOtherTab = true //其他
            }
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    if(this.scheduleForm.isNotice){
                        this.scheduleForm.isNotice = "1"
                    }else{
                        this.scheduleForm.isNotice = "0"
                    }
                    console.log(this.scheduleForm)
                    _addEvent(this.scheduleForm).then(res => {
                        if(res.status == 200){
                            this.$message.success("创建成功!");
                            this.$router.push("/home/aunt/AuntDashBoard")
                        }else{
                            this.$message.error(res.data.msg);
                        }
                    })
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        },
        handleDateChange(val,type){
            if(type === 'bg') this.scheduleForm.startDate = parseTime(val)
            else this.scheduleForm.endDate = parseTime(val)
        }
    },
    created(){
        const data = JSON.parse(this.$route.params.data)
        this.scheduleForm.createNameID = this.$store.state.login_user.id //获取登录用户ID
        this.scheduleForm.name = data.name //获取当事人名称
        this.scheduleForm.startDate = parseTime(data.date)
        this.scheduleForm.endDate = parseTime(data.date)
    }
}
</script>