<template>
    <el-row :gutter="40" class="panel-group">
        <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
            <el-row style="margin-top: 10px;margin-bottom: 10px;">
                <el-col :span="8">
                    <el-select size="small" v-model="uName" filterable placeholder="请选择用户">
                        <el-option v-for="item in uNameAll" :label="item.uName" :value="item.uName">
                        </el-option>
                    </el-select>
                    <el-button type="primary" size="small" @click="selectByNameAllFun">
                        <i class="el-icon-search"></i>
                        搜索
                    </el-button>
                </el-col>
            </el-row>
        </el-col>
        <el-col :xs="24" :lg="8" class="card-panel-col">
            <div class="card-panel">
                <el-descriptions class="margin-top" title="上次记录" :column="2" border>
                    <!-- <template slot="extra">
                        <el-button type="primary" size="small">操作</el-button>
                    </template> -->
                    <el-descriptions-item label="姓名">{{ auntForm.name }}</el-descriptions-item>
                    <el-descriptions-item label="周期长度">{{ auntForm.cycle }}</el-descriptions-item>
                    <el-descriptions-item label="开始日期">{{ auntForm.startDate }}</el-descriptions-item>
                    <el-descriptions-item label="结束日期">{{ auntForm.endDate }}</el-descriptions-item>
                    <el-descriptions-item label="持续时间">{{ auntForm.duration }}</el-descriptions-item>
                    <el-descriptions-item label="血量">{{ auntForm.bloodVolume }}ml</el-descriptions-item>
                </el-descriptions>
                <!-- <el-descriptions :column="1" border>
                    <el-descriptions-item label="症状">小腹微痛</el-descriptions-item>
                    <el-descriptions-item label="备注">kooriookami</el-descriptions-item>
                </el-descriptions> -->
            </div>
        </el-col>
        <el-col :xs="24" :lg="8" class="card-panel-col">
            <div class="card-panel">
                <el-descriptions class="margin-top" title="系统推测下次经期情况" :column="2" border>
                    <el-descriptions-item label="姓名">{{ auntForm.name }}</el-descriptions-item>
                    <el-descriptions-item label="周期长度">{{ nextAuntForm.cycleLength }}天</el-descriptions-item>
                    <el-descriptions-item label="开始日期">{{ nextAuntForm.nextPeriodStartDate }}</el-descriptions-item>
                    <el-descriptions-item label="结束日期">{{ nextAuntForm.nextPeriodEndDate }}</el-descriptions-item>
                    <el-descriptions-item label="持续时间">{{ nextAuntForm.periodLength }}天</el-descriptions-item>
                    <el-descriptions-item label="距离开始还有">{{ nextAuntForm.daysUntilNextPeriod }}天</el-descriptions-item>
                </el-descriptions>
                <!-- <el-descriptions :column="1" border>
                    <el-descriptions-item label="症状">小腹微痛</el-descriptions-item>
                    <el-descriptions-item label="备注">kooriookami</el-descriptions-item>
                </el-descriptions> -->
            </div>
        </el-col>
        <el-col :xs="24" :lg="8" class="card-panel-col">
            <div class="card-panel">
                <el-descriptions class="margin-top" title="本次实际记录" :column="2" border>
                    <template slot="extra">
                        <el-button type="primary" size="small">操作</el-button>
                    </template>
                    <el-descriptions-item label="姓名">kooriookami</el-descriptions-item>
                    <el-descriptions-item label="周期长度">29天</el-descriptions-item>
                    <el-descriptions-item label="开始日期">2022-10-19</el-descriptions-item>
                    <el-descriptions-item label="结束日期">2022-11-12</el-descriptions-item>
                    <el-descriptions-item label="持续时间">4天</el-descriptions-item>
                    <el-descriptions-item label="血量">100ml</el-descriptions-item>
                </el-descriptions>
                <!-- <el-descriptions :column="1" border>
                    <el-descriptions-item label="症状">小腹微痛</el-descriptions-item>
                    <el-descriptions-item label="备注">kooriookami</el-descriptions-item>
                </el-descriptions> -->
            </div>
        </el-col>

    </el-row>
</template>
        
<script>
import CountTo from 'vue-count-to'
import { _getUserAll, _getAuntNameAll, _getAuntByNameAll } from "@/api/api.js";

export default {
    components: {
        CountTo
    },
    data() {
        return {
            auntForm: {
                name: '',
                startDate: '',   //开始日期
                endDate: '',     //结束日期
                duration: '',    //持续时间
                cycle: '',      //周期长度
                bloodVolume: '', //姨妈血量
            },
            nextAuntForm:{
                daysUntilNextPeriod:'', //距离下次还剩的时间
                cycleLength:'',     //周期
                periodLength:'',    //持续时间
                nextPeriodEndDate:'',   //下次结束时间
                nextPeriodStartDate:''  //下次开始时间
            },
            uNameAll: [],    //所有用户
            uName: '',   //查询的用户
            userCount: 0,     //用户总数
            blogCount: 0,     //博客总数
            netDiskFileCount: 0,     //网盘文件总数
            messageCount: 0,     //留言总数
            value: new Date()
        }
    },
    methods: {
        init() {
            _getAuntNameAll().then(res => {
                this.uNameAll = res.data.obj
            })
        },
        selectByNameAllFun() {
            _getAuntByNameAll(this.uName).then(res => {
                console.log(res)
                this.nextAuntForm.daysUntilNextPeriod = res.data.daysUntilNextPeriod
                this.nextAuntForm.nextPeriodEndDate = res.data.nextPeriodEndDate
                this.nextAuntForm.nextPeriodStartDate = res.data.nextPeriodStartDate
                this.nextAuntForm.cycleLength = res.data.cycleLength
                this.nextAuntForm.periodLength = res.data.periodLength
                

                this.auntForm.name = res.data.auntList[0].uName
                this.auntForm.startDate = res.data.auntList[0].startDate
                this.auntForm.endDate = res.data.auntList[0].endDate
                this.auntForm.duration = res.data.auntList[0].duration
                this.auntForm.cycle = res.data.auntList[0].cycle
                this.auntForm.bloodVolume = res.data.auntList[0].bloodVolume
            })
        }
    },
    created() {
        this.init();
    }
}
</script>
        
<style lang="scss">
@import 'element-ui/packages/theme-chalk/src/table.scss';

.el-descriptions__body th {
    width: 80px;
}

.panel-group {
    margin-top: 18px;

    .card-panel-col {
        margin-bottom: 12px;
    }

    .card-panel {
        float: left;
        height: 195px;
        width: 100%;

        cursor: pointer;
        font-size: 12px;
        position: relative;
        overflow: hidden;
        color: #666;
        background: #fff;
        box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
        border-color: rgba(0, 0, 0, .05);
    }

}
</style>
        