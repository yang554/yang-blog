<template>
    <div class="fee-calculator">
      <el-card class="calculator-card">
        <h2>课时费计算器</h2>
        
        <el-form label-width="120px">
          <!-- 教师等级 -->
          <el-form-item label="教师等级">
            <el-select v-model="form.teacherLevel" placeholder="请选择">
              <el-option 
                v-for="level in 12" 
                :key="level" 
                :label="`${level}级`" 
                :value="level">
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
              <el-radio class="el-radio-cs" label="ONE_TO_ONE">一对一</el-radio>
              <el-radio class="el-radio-cs" label="GROUP">班课</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <!-- 班课相关字段 -->
          <template v-if="form.classType === 'GROUP'">
            <el-form-item label="学生人数">
              <el-input-number 
                v-model="form.studentCount" 
                :min="1" 
                :max="30">
              </el-input-number>
            </el-form-item>
            
            <el-form-item label="班课类型">
              <el-radio-group v-model="form.classCategory">
                <el-radio class="el-radio-cs" label="REGULAR">正价班</el-radio>
                <el-radio class="el-radio-cs" label="DISCOUNT">流里班</el-radio>
              </el-radio-group>
            </el-form-item>
          </template>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="calculateFee"
              :disabled="!isFormValid">
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
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        form: {
          teacherLevel: 1,
          grade: '小学',
          classType: 'ONE_TO_ONE',
          studentCount: 1,
          classCategory: 'REGULAR'
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
      async calculateFee() {
        try {
          const response = await axios.post('http://localhost:8080/api/fee/calculate', this.form);
          this.result = response.data.fee;
          this.generateCalculationDetails();
        } catch (error) {
          this.$message.error('计算失败，请检查输入');
          this.result = null;
        }
      },
      
      generateCalculationDetails() {
        const baseFeeMap = {
          '小学': {1: 100, 2: 105, 3: 110, 4: 115, 5: 130, 6: 135, 7: 140, 8: 145, 9: 150, 10: 155, 11: 160, 12: 170},
          // ... 其他年级数据
        };
        
        const baseFee = baseFeeMap[this.form.grade][this.form.teacherLevel];
        let details = `基础课时费（${this.form.grade} ${this.form.teacherLevel}级）：${baseFee}元`;
        
        if (this.form.classType === 'GROUP') {
          if (this.form.studentCount < 5) {
            details += `<br>班课人数小于5人，按一对一计算`;
          } else if (this.form.classCategory === 'DISCOUNT') {
            details += `<br>流里班公式：${baseFee} + 10 × (${this.form.studentCount} - 1) = ${baseFee + 10 * (this.form.studentCount - 1)}元`;
          } else {
            // 正价班增加额逻辑
            details += `<br>正价班增加额计算`;
          }
        }
        
        this.calculationDetails = details;
      }
    }
  };
  </script>
  
  <style scoped>
  .fee-calculator {
    max-width: 800px;
    margin: 40px auto;
  }
  
  .calculator-card {
    padding: 20px;
  }
  
  .result-section {
    margin-top: 30px;
    padding: 20px;
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

  .el-radio-cs{
    color: #909399 !important;
  }
  </style>