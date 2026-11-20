<template>
  <div class="fee-calculator">
    <div style="float: left;">
      <el-card class="calculator-card">
        <h2>基础课时费</h2>
        <el-table :data="baseFeesData" stripe style="width: 100%">
          <el-table-column prop="level" label="教师评级">
          </el-table-column>
          <el-table-column prop="primary" label="小学">
          </el-table-column>
          <el-table-column prop="junior" label="初中">
          </el-table-column>
          <el-table-column prop="seniorOne" label="高一二">
          </el-table-column>
          <el-table-column prop="seniorThree" label="高三">
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <div style="float: left;margin-left: 60px;">
      <el-card class="calculator-card">
        <h2>班课正价班课时费</h2>
        <el-table :data="feeExtraData" stripe style="width: 100%" :header-cell-style="{ 'text-align': 'center' }">
          <el-table-column prop="studentCount" label="班级人数" width="100" align="center">
          </el-table-column>
          <el-table-column prop="primaryMiddle" label="小学初中（增加额）" width="200" align="center">
          </el-table-column>
          <el-table-column prop="highSchool" label="高中（增加额)" width="200" align="center">
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <div style="float: left;margin-left: 60px;">
      <div>
        <el-card class="calculator-card">
          <h2>班课流量班课时费</h2>
          <div>小于五人，按照基础课时费计算</div>
          <div>大于五人，基础课时费+10*（班级人数-1）</div>
        </el-card>
      </div>
      <div style="margin-top: 30px;">
        <el-card class="calculator-card">
          <h2>课时费计算器</h2>
          <el-form label-width="120px">
            <!-- 教师等级 -->
            <el-form-item label="教师等级">
              <el-select v-model="form.teacherLevel" placeholder="请选择">
                <el-option v-for="level in 12" :key="level" :label="`${level}级`" :value="level">
                </el-option>
              </el-select>
            </el-form-item>
            <!-- 年级 -->
            <el-form-item label="年级">
              <el-select v-model="form.grade" placeholder="请选择">
                <el-option label="小学" value="小学"></el-option>
                <el-option label="初中" value="初中"></el-option>
                <el-option label="高一二" value="高一二"></el-option>
                <el-option label="高三" value="高三"></el-option>
              </el-select>
            </el-form-item>
            <!-- 授课类型 -->
            <el-form-item label="授课类型">
              <el-radio-group v-model="form.classType">
                <el-radio class="el-radio-cs" label="一对一">一对一</el-radio>
                <el-radio class="el-radio-cs" label="班课">班课</el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- 班课相关字段 -->
            <template v-if="form.classType === '班课'">
              <el-form-item label="学生人数">
                <el-input-number v-model="form.studentCount" :min="1" :max="30">
                </el-input-number>
              </el-form-item>
              <el-form-item label="班课类型">
                <el-radio-group v-model="form.classCategory">
                  <el-radio class="el-radio-cs" label="正价班">正价班</el-radio>
                  <el-radio class="el-radio-cs" label="流量班">流量班</el-radio>
                </el-radio-group>
              </el-form-item>
            </template>
            <el-form-item>
              <el-button type="primary" @click="calculateFee" :disabled="!isFormValid">
                计算课时费
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 结果显示 -->
          <div v-if="result !== null" class="result-section">
            <h3>计算结果</h3>
            <p>课时费：<span class="fee-amount">{{ result }} 元</span></p>

            <div class="calculation-details">
              <h4>计算说明：</h4>
              <p v-html="calculationDetails"></p>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>
  
<script>
import axios from 'axios';
import { number } from 'echarts';

export default {
  data() {
    return {
      baseFeesData: [
        { level: '一级', primary: 100, junior: 105, seniorOne: 120, seniorThree: 125 },
        { level: '二级', primary: 105, junior: 110, seniorOne: 125, seniorThree: 130 },
        { level: '三级', primary: 110, junior: 115, seniorOne: 130, seniorThree: 140 },
        { level: '四级', primary: 115, junior: 120, seniorOne: 140, seniorThree: 150 },
        { level: '五级', primary: 130, junior: 140, seniorOne: 160, seniorThree: 170 },
        { level: '六级', primary: 135, junior: 150, seniorOne: 170, seniorThree: 180 },
        { level: '七级', primary: 140, junior: 160, seniorOne: 180, seniorThree: 190 },
        { level: '八级', primary: 145, junior: 170, seniorOne: 190, seniorThree: 200 },
        { level: '九级', primary: 150, junior: 180, seniorOne: 200, seniorThree: 210 },
        { level: '十级', primary: 155, junior: 190, seniorOne: 210, seniorThree: 220 },
        { level: '十一级', primary: 160, junior: 200, seniorOne: 220, seniorThree: 230 },
        { level: '十二级', primary: 170, junior: 210, seniorOne: 230, seniorThree: 250 }
      ],
      feeExtraData: [
        { studentCount: '4个学生', primaryMiddle: 0, highSchool: 0 },
        { studentCount: '5个学生', primaryMiddle: 50, highSchool: 60 },
        { studentCount: '6个学生', primaryMiddle: 70, highSchool: 80 },
        { studentCount: '7个学生', primaryMiddle: 90, highSchool: 100 },
        { studentCount: '8个学生', primaryMiddle: 120, highSchool: 130 },
        { studentCount: '9个学生', primaryMiddle: 140, highSchool: 150 },
        { studentCount: '10个学生', primaryMiddle: 160, highSchool: 170 },
        { studentCount: '11个学生', primaryMiddle: 180, highSchool: 190 },
        { studentCount: '12个学生', primaryMiddle: 220, highSchool: 230 },
        { studentCount: '13个学生', primaryMiddle: 240, highSchool: 250 },
        { studentCount: '14个学生', primaryMiddle: 280, highSchool: 290 },
        { studentCount: '15个学生', primaryMiddle: 300, highSchool: 310 },
      ],
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
      form: {
        teacherLevel: 1,
        grade: '小学',
        classType: '一对一',
        studentCount: 1,
        classCategory: '正价班',
        fee: 0
      },
      result: null,
      calculationDetails: ''
    };
  },
  computed: {
    isFormValid() {
      return this.form.teacherLevel && this.form.grade;
    }
  },
  methods: {
    getBaseFee(grade, level) {
      if (this.baseFees[grade] && this.baseFees[grade][level]) {
        return this.baseFees[grade][level];
      }
      throw new Error(`无效的年级或教师等级: ${grade} ${level}`);
    },
    getExtraFee(grade, studentCount) {
            const isHighSchool = grade === '高一二' || grade === '高三';
            const extraMap = isHighSchool ? this.highSchoolExtra : this.primaryMiddleExtra;
            // 处理学生人数大于15的情况
            let count = studentCount //Math.min(studentCount, 15);
            // 查找最接近的学生人数
            while (count > 4 && !extraMap.hasOwnProperty(count)) {
                count--;
            }
            return extraMap[count] || 0;
        },
    calculateFee() {
      try {
        const { teacherLevel, grade, classType, studentCount, classCategory } = this.form;
        const baseFee = this.getBaseFee(grade, teacherLevel);
        let details = `基础课时费（${grade} ${teacherLevel}级）：${baseFee}元`;
        if (classType === '一对一') {
          this.result = baseFee;
          this.calculationDetails = details;
          return;
        }
        if (studentCount < 5) {
          this.result = baseFee;
        } else if (classCategory === '流量班') {
          this.result = baseFee + 10 * (studentCount - 1);
          details += `<br>流量班公式：${baseFee} + 10 × (${studentCount} - 1) = ${this.result}元`;
        } else {
          this.result = baseFee + this.getExtraFee(grade, studentCount);
          details += `<br>正价班增加额${this.getExtraFee(grade, studentCount)}元`;
        }
        this.calculationDetails = details;
      } catch (error) {
        this.$message.error('计算失败，请检查输入');
        this.result = null;
      }
    },
  }
};
</script>
  
<style scoped>
.fee-calculator {
  margin: 10px auto;
}

.calculator-card {
  padding: 10px;
}

.result-section {
  margin-top: 5px;
  padding: 5px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.fee-amount {
  font-size: 24px;
  font-weight: bold;
  color: #e6a23c;
}

.calculation-details {
  margin-top: 15px;
  padding: 15px;
  background-color: #ecf5ff;
  border-radius: 4px;
}

.el-radio-cs {
  color: #909399 !important;
}
</style>