<template>
  <div class="nutrition-target">
    <div class="header">
      <h2>营养目标</h2>
      <div class="actions">
        <el-button type="primary" @click="generateRecommendation">生成推荐</el-button>
        <el-button type="success" @click="editTarget">编辑目标</el-button>
      </div>
    </div>

    <div class="content">
      <div class="user-info-card">
        <h3>用户信息</h3>
        <div class="user-info">
          <div class="info-item">
            <span class="label">姓名:</span>
            <span class="value">{{ userInfo.userName }}</span>
          </div>
          <div class="info-item">
            <span class="label">性别:</span>
            <span class="value">{{ userInfo.gender || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">年龄:</span>
            <span class="value">{{ userInfo.age || '未知' }} 岁</span>
          </div>
          <div class="info-item">
            <span class="label">身高:</span>
            <span class="value">{{ userInfo.height || '未知' }} cm</span>
          </div>
          <div class="info-item">
            <span class="label">体重:</span>
            <span class="value">{{ userInfo.weight || '未知' }} kg</span>
          </div>
          <div class="info-item">
            <span class="label">BMI:</span>
            <span class="value">{{ userInfo.bmi || '未知' }}</span>
          </div>
        </div>
      </div>

      <div class="nutrition-card">
        <h3>当前营养目标</h3>
        <div v-if="nutritionTarget" class="nutrition-info">
          <div class="nutrition-item">
            <span class="label">热量:</span>
            <span class="value">{{ nutritionTarget.calories }} 千卡</span>
          </div>
          <div class="nutrition-item">
            <span class="label">蛋白质:</span>
            <span class="value">{{ nutritionTarget.protein }} 克</span>
          </div>
          <div class="nutrition-item">
            <span class="label">碳水化合物:</span>
            <span class="value">{{ nutritionTarget.carbohydrate }} 克</span>
          </div>
          <div class="nutrition-item">
            <span class="label">脂肪:</span>
            <span class="value">{{ nutritionTarget.fat }} 克</span>
          </div>
          <div class="nutrition-item">
            <span class="label">纤维素:</span>
            <span class="value">{{ nutritionTarget.fiber }} 克</span>
          </div>
          <div class="nutrition-item">
            <span class="label">钠:</span>
            <span class="value">{{ nutritionTarget.sodium }} 毫克</span>
          </div>
        </div>
        <div v-else class="no-data">
          <p>暂无营养目标数据，请点击"编辑目标"设置或"生成推荐"自动生成</p>
        </div>
      </div>
    </div>

    <!-- 编辑目标对话框 -->
    <el-dialog title="编辑营养目标" :visible.sync="editDialogVisible" width="500px">
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="热量 (千卡)">
          <el-input-number v-model="editForm.calories" :min="0" :step="50"></el-input-number>
        </el-form-item>
        <el-form-item label="蛋白质 (克)">
          <el-input-number v-model="editForm.protein" :min="0" :step="5"></el-input-number>
        </el-form-item>
        <el-form-item label="碳水化合物 (克)">
          <el-input-number v-model="editForm.carbohydrate" :min="0" :step="5"></el-input-number>
        </el-form-item>
        <el-form-item label="脂肪 (克)">
          <el-input-number v-model="editForm.fat" :min="0" :step="5"></el-input-number>
        </el-form-item>
        <el-form-item label="纤维素 (克)">
          <el-input-number v-model="editForm.fiber" :min="0" :step="1"></el-input-number>
        </el-form-item>
        <el-form-item label="钠 (毫克)">
          <el-input-number v-model="editForm.sodium" :min="0" :step="50"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveTarget">保 存</el-button>
      </span>
    </el-dialog>

    <!-- 推荐对话框 -->
    <el-dialog title="营养推荐" :visible.sync="recommendDialogVisible" width="600px">
      <div v-if="recommendation" class="recommendation-content">
        <div class="user-metrics">
          <h4>用户指标</h4>
          <div class="metrics-grid">
            <div class="metric-item">
              <span class="label">BMI:</span>
              <span class="value">{{ recommendation.bmi }}</span>
            </div>
          </div>
        </div>
        
        <div class="recommendation-details">
          <h4>推荐营养摄入</h4>
          <div class="nutrition-grid">
            <div class="nutrition-item">
              <span class="label">热量:</span>
              <span class="value">{{ recommendation.calories }} 千卡</span>
            </div>
            <div class="nutrition-item">
              <span class="label">蛋白质:</span>
              <span class="value">{{ recommendation.protein }} 克</span>
            </div>
            <div class="nutrition-item">
              <span class="label">碳水化合物:</span>
              <span class="value">{{ recommendation.carbohydrate }} 克</span>
            </div>
            <div class="nutrition-item">
              <span class="label">脂肪:</span>
              <span class="value">{{ recommendation.fat }} 克</span>
            </div>
            <div class="nutrition-item">
              <span class="label">纤维素:</span>
              <span class="value">{{ recommendation.fiber }} 克</span>
            </div>
            <div class="nutrition-item">
              <span class="label">钠:</span>
              <span class="value">{{ recommendation.sodium }} 毫克</span>
            </div>
          </div>
        </div>

        <div class="recommendation-notes">
          <h4>备注</h4>
          <p>{{ recommendation.notes || '无' }}</p>
        </div>
      </div>
      <div v-else class="loading-recommendation">
        <p>正在生成推荐...</p>
        <el-progress :percentage="50" :indeterminate="true"></el-progress>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="recommendDialogVisible = false">取 消</el-button>
        <el-button type="success" @click="saveRecommendation">保存为目标</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getNutritionTarget, saveNutritionTarget, generateNutritionRecommendation } from '@/api/nutrition';

export default {
  name: 'NutritionTarget',
  data() {
    return {
      userInfo: {
        userName: '',
        gender: '',
        age: null,
        height: null,
        weight: null,
        bmi: null
      },
      nutritionTarget: null,
      editDialogVisible: false,
      recommendDialogVisible: false,
      recommendation: null,
      editForm: {
        calories: 0,
        protein: 0,
        carbohydrate: 0,
        fat: 0,
        fiber: 0,
        sodium: 0
      }
    };
  },
  created() {
    this.loadUserInfo();
    this.loadNutritionTarget();
  },
  methods: {
    loadUserInfo() {
      // 从sessionStorage获取用户信息
      const userInfoStr = sessionStorage.getItem('userInfo');
      if (userInfoStr) {
        const userInfo = JSON.parse(userInfoStr);
        this.userInfo.userName = userInfo.userName;
        this.userInfo.gender = userInfo.gender;
        
        // 计算年龄
        if (userInfo.birthDate) {
          const birthDate = new Date(userInfo.birthDate);
          const today = new Date();
          let age = today.getFullYear() - birthDate.getFullYear();
          const monthDiff = today.getMonth() - birthDate.getMonth();
          if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
          }
          this.userInfo.age = age;
        }
      }
      
      // 不再单独加载身高体重数据，将在loadNutritionTarget中获取
    },
    async loadNutritionTarget() {
      try {
        const response = await getNutritionTarget();
        if (response.data.code === 200) {
          this.nutritionTarget = response.data.data;
          
          // 从营养目标数据中获取身高体重信息
          if (this.nutritionTarget) {
            console.log('营养目标数据:', this.nutritionTarget);
            this.userInfo.height = this.nutritionTarget.height;
            this.userInfo.weight = this.nutritionTarget.weight;
            this.userInfo.age = this.nutritionTarget.age;
            this.userInfo.gender = this.nutritionTarget.gender || this.userInfo.gender;
            
            // 计算BMI
            if (this.userInfo.height && this.userInfo.weight) {
              const heightInMeters = this.userInfo.height / 100;
              this.userInfo.bmi = (this.userInfo.weight / (heightInMeters * heightInMeters)).toFixed(1);
            }
          }
        }
      } catch (error) {
        console.error('获取营养目标失败:', error);
        this.$message.error('获取营养目标失败');
      }
    },
    editTarget() {
      if (this.nutritionTarget) {
        this.editForm = { ...this.nutritionTarget };
      } else {
        this.editForm = {
          calories: 2000,
          protein: 75,
          carbohydrate: 250,
          fat: 70,
          fiber: 25,
          sodium: 2300
        };
      }
      this.editDialogVisible = true;
    },
    async saveTarget() {
      try {
        const response = await saveNutritionTarget(this.editForm);
        if (response.data.code === 200) {
          this.$message.success('保存营养目标成功');
          this.editDialogVisible = false;
          await this.loadNutritionTarget();
        } else {
          this.$message.error(response.data.msg || '保存失败');
        }
      } catch (error) {
        console.error('保存营养目标失败:', error);
        this.$message.error('保存营养目标失败');
      }
    },
    async generateRecommendation() {
      this.recommendDialogVisible = true;
      this.recommendation = null;
      
      try {
        const response = await generateNutritionRecommendation();
        if (response.data.code === 200) {
          this.recommendation = response.data.data;
        } else {
          this.$message.error(response.data.msg || '生成推荐失败');
          this.recommendDialogVisible = false;
        }
      } catch (error) {
        console.error('生成营养推荐失败:', error);
        this.$message.error('生成营养推荐失败');
        this.recommendDialogVisible = false;
      }
    },
    async saveRecommendation() {
      if (!this.recommendation) return;
      
      try {
        const targetData = {
          calories: this.recommendation.calories,
          protein: this.recommendation.protein,
          carbohydrate: this.recommendation.carbohydrate,
          fat: this.recommendation.fat,
          fiber: this.recommendation.fiber,
          sodium: this.recommendation.sodium
        };
        
        const response = await saveNutritionTarget(targetData);
        if (response.data.code === 200) {
          this.$message.success('保存为营养目标成功');
          this.recommendDialogVisible = false;
          await this.loadNutritionTarget();
        } else {
          this.$message.error(response.data.msg || '保存失败');
        }
      } catch (error) {
        console.error('保存为营养目标失败:', error);
        this.$message.error('保存为营养目标失败');
      }
    },

  }
};
</script>

<style scoped>
.nutrition-target {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.user-info-card,
.nutrition-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.user-info,
.nutrition-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.info-item,
.nutrition-item {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.value {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.recommendation-content {
  padding: 10px;
}

.user-metrics,
.recommendation-details,
.recommendation-notes {
  margin-bottom: 20px;
}

.metrics-grid,
.nutrition-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 5px;
}

.loading-recommendation {
  text-align: center;
  padding: 20px 0;
}
</style>