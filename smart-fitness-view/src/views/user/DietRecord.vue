<template>
  <div class="diet-record">
    <div class="header">
      <h2>饮食记录</h2>
      <div class="date-selector">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          @change="loadDietRecords"
        >
        </el-date-picker>
        <el-button type="primary" @click="showAddDietDialog">添加饮食记录</el-button>
      </div>
    </div>

    <!-- 营养汇总卡片 -->
    <div class="nutrition-summary">
      <el-card>
        <div slot="header">
          <span>今日营养摄入汇总</span>
        </div>
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-value">{{ totalNutrition.calories }}</div>
              <div class="nutrition-label">热量 (kcal)</div>
              <div class="nutrition-progress">
                <el-progress :percentage="getProgressPercentage('calories')" :show-text="false"></el-progress>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-value">{{ totalNutrition.protein.toFixed(1) }}</div>
              <div class="nutrition-label">蛋白质 (g)</div>
              <div class="nutrition-progress">
                <el-progress :percentage="getProgressPercentage('protein')" :show-text="false"></el-progress>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-value">{{ totalNutrition.carbohydrate.toFixed(1) }}</div>
              <div class="nutrition-label">碳水化合物 (g)</div>
              <div class="nutrition-progress">
                <el-progress :percentage="getProgressPercentage('carbohydrate')" :show-text="false"></el-progress>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-value">{{ totalNutrition.fat.toFixed(1) }}</div>
              <div class="nutrition-label">脂肪 (g)</div>
              <div class="nutrition-progress">
                <el-progress :percentage="getProgressPercentage('fat')" :show-text="false"></el-progress>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 餐食记录 -->
    <div class="meal-records">
      <el-tabs v-model="activeMealType" @tab-click="handleMealTypeChange">
        <el-tab-pane label="早餐" name="1">
          <meal-record-list 
            :records="getMealRecords(1)" 
            @delete="deleteDietRecord"
          ></meal-record-list>
        </el-tab-pane>
        <el-tab-pane label="午餐" name="2">
          <meal-record-list 
            :records="getMealRecords(2)" 
            @delete="deleteDietRecord"
          ></meal-record-list>
        </el-tab-pane>
        <el-tab-pane label="晚餐" name="3">
          <meal-record-list 
            :records="getMealRecords(3)" 
            @delete="deleteDietRecord"
          ></meal-record-list>
        </el-tab-pane>
        <el-tab-pane label="加餐" name="4">
          <meal-record-list 
            :records="getMealRecords(4)" 
            @delete="deleteDietRecord"
          ></meal-record-list>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 添加饮食记录对话框 -->
    <el-dialog
      title="添加饮食记录"
      :visible.sync="addDietDialogVisible"
      width="800px"
      @close="resetDietForm"
    >
      <div class="diet-form">
        <el-form :model="dietForm" ref="dietForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="记录日期" prop="recordDate">
                <el-date-picker
                  v-model="dietForm.recordDate"
                  type="date"
                  placeholder="选择日期"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="餐食类型" prop="mealType">
                <el-select v-model="dietForm.mealType" placeholder="请选择餐食类型" style="width: 100%">
                  <el-option label="早餐" :value="1"></el-option>
                  <el-option label="午餐" :value="2"></el-option>
                  <el-option label="晚餐" :value="3"></el-option>
                  <el-option label="加餐" :value="4"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <!-- 食物搜索 -->
        <div class="food-search">
          <el-input
            v-model="foodSearchKeyword"
            placeholder="搜索食物"
            @input="searchFood"
            clearable
          >
            <el-button slot="append" icon="el-icon-search" @click="searchFood"></el-button>
          </el-input>
        </div>

        <!-- 食物列表 -->
        <div class="food-list" v-if="foodList.length > 0">
          <el-table :data="foodList" style="width: 100%" max-height="300">
            <el-table-column prop="foodName" label="食物名称" width="150"></el-table-column>
            <el-table-column prop="servingSize" label="份量" width="100">
              <template slot-scope="scope">
                {{ scope.row.servingSize }}{{ scope.row.servingUnit }}
              </template>
            </el-table-column>
            <el-table-column prop="calories" label="热量(kcal)" width="100"></el-table-column>
            <el-table-column prop="protein" label="蛋白质(g)" width="100"></el-table-column>
            <el-table-column label="摄入量" width="150">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.consumeAmount"
                  :min="0"
                  :precision="0"
                  size="small"
                  style="width: 100px"
                ></el-input-number>
                <span style="margin-left: 5px">{{ scope.row.servingUnit }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  type="primary"
                  size="mini"
                  @click="addFoodToRecord(scope.row)"
                  :disabled="!scope.row.consumeAmount || scope.row.consumeAmount <= 0"
                >
                  添加
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 已选择的食物 -->
        <div class="selected-foods" v-if="selectedFoods.length > 0">
          <h4>已选择的食物：</h4>
          <el-table :data="selectedFoods" style="width: 100%">
            <el-table-column prop="foodName" label="食物名称" width="150"></el-table-column>
            <el-table-column label="摄入量" width="150">
              <template slot-scope="scope">
                {{ scope.row.consumeAmount }}{{ scope.row.servingUnit }}
              </template>
            </el-table-column>
            <el-table-column label="热量" width="100">
              <template slot-scope="scope">
                {{ calculateNutrition(scope.row, 'calories') }} kcal
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  type="danger"
                  size="mini"
                  @click="removeFoodFromRecord(scope.$index)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="addDietDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDietRecord" :disabled="selectedFoods.length === 0">保存记录</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request.js";
import MealRecordList from "@/components/MealRecordList.vue";

export default {
  name: "DietRecord",
  components: {
    MealRecordList
  },
  data() {
    return {
      selectedDate: new Date().toISOString().split('T')[0],
      activeMealType: "1",
      dietRecords: [],
      totalNutrition: {
        calories: 0,
        protein: 0,
        carbohydrate: 0,
        fat: 0
      },
      nutritionTargets: {
        calories: 2000,
        protein: 60,
        carbohydrate: 250,
        fat: 70
      },
      addDietDialogVisible: false,
      dietForm: {
        recordDate: new Date().toISOString().split('T')[0],
        mealType: 1
      },
      foodSearchKeyword: "",
      foodList: [],
      selectedFoods: [],
      loading: false,
      userInfo: {}
    };
  },
  mounted() {
    this.getUserInfo();

    this.loadDietRecords();
  },
  methods: {
    // 获取用户信息
    getUserInfo() {
      const userInfoStr = sessionStorage.getItem('userInfo');
      if (userInfoStr) {
        this.userInfo = JSON.parse(userInfoStr);
      }
    },



    // 加载饮食记录
    async loadDietRecords() {
      try {
        this.loading = true;
        const response = await request.get(`/diet/records`, {
          params: {
            userId: this.userInfo.id,
            recordDate: this.selectedDate
          }
        });
        if (response.data.code === 200) {
          this.dietRecords = response.data.data || [];
          this.calculateTotalNutrition();
        }
      } catch (error) {
        console.error("加载饮食记录失败:", error);
        this.$message.error("加载饮食记录失败");
      } finally {
        this.loading = false;
      }
    },

    // 计算总营养
    calculateTotalNutrition() {
      this.totalNutrition = {
        calories: 0,
        protein: 0,
        carbohydrate: 0,
        fat: 0
      };
      
      this.dietRecords.forEach(record => {
        this.totalNutrition.calories += record.actualCalories || 0;
        this.totalNutrition.protein += parseFloat(record.actualProtein || 0);
        this.totalNutrition.carbohydrate += parseFloat(record.actualCarbohydrate || 0);
        this.totalNutrition.fat += parseFloat(record.actualFat || 0);
      });
    },

    // 获取进度百分比
    getProgressPercentage(nutrient) {
      const current = this.totalNutrition[nutrient];
      const target = this.nutritionTargets[nutrient];
      return Math.min(Math.round((current / target) * 100), 100);
    },

    // 根据餐食类型获取记录
    getMealRecords(mealType) {
      return this.dietRecords.filter(record => record.mealType === mealType);
    },

    // 餐食类型切换
    handleMealTypeChange(tab) {
      this.activeMealType = tab.name;
    },

    // 显示添加饮食记录对话框
    showAddDietDialog() {
      this.dietForm.recordDate = this.selectedDate;
      this.dietForm.mealType = parseInt(this.activeMealType);
      this.addDietDialogVisible = true;
    },

    // 搜索食物
    async searchFood() {
      if (!this.foodSearchKeyword.trim()) {
        this.foodList = [];
        return;
      }
      
      try {
        const response = await request.post("/food/query", {
          foodName: this.foodSearchKeyword,
          current: 1,
          size: 20
        });
        if (response.data.code === 200) {
          this.foodList = (response.data.data || []).map(food => ({
            ...food,
            consumeAmount: food.servingSize
          }));
        }
      } catch (error) {
        console.error("搜索食物失败:", error);
        this.$message.error("搜索食物失败");
      }
    },

    // 添加食物到记录
    addFoodToRecord(food) {
      if (!food.consumeAmount || food.consumeAmount <= 0) {
        this.$message.warning("请输入有效的摄入量");
        return;
      }
      
      const existingIndex = this.selectedFoods.findIndex(f => f.id === food.id);
      if (existingIndex >= 0) {
        this.selectedFoods[existingIndex].consumeAmount += food.consumeAmount;
      } else {
        this.selectedFoods.push({
          ...food,
          consumeAmount: food.consumeAmount
        });
      }
      
      // 重置搜索结果中的摄入量
      food.consumeAmount = food.servingSize;
    },

    // 从记录中移除食物
    removeFoodFromRecord(index) {
      this.selectedFoods.splice(index, 1);
    },

    // 计算营养素
    calculateNutrition(food, nutrient) {
      const ratio = food.consumeAmount / food.servingSize;
      const value = parseFloat(food[nutrient] || 0) * ratio;
      return nutrient === 'calories' ? Math.round(value) : value.toFixed(1);
    },

    // 提交饮食记录
    async submitDietRecord() {
      if (this.selectedFoods.length === 0) {
        this.$message.warning("请至少选择一种食物");
        return;
      }
      
      try {
        this.loading = true;
        const dietData = {
          userId: this.userInfo.id,
          recordDate: this.dietForm.recordDate,
          mealType: this.dietForm.mealType,
          foodIds: this.selectedFoods.map(food => food.id),
          foodNums: this.selectedFoods.map(food => food.consumeAmount)
        };
        
        const response = await request.post("/diet/save", dietData);
        if (response.data.code === 200) {
          this.$message.success("保存饮食记录成功");
          this.addDietDialogVisible = false;
          this.resetDietForm();
          this.loadDietRecords();
        } else {
          this.$message.error(response.data.msg || "保存饮食记录失败");
        }
      } catch (error) {
        console.error("保存饮食记录失败:", error);
        this.$message.error("保存饮食记录失败");
      } finally {
        this.loading = false;
      }
    },

    // 重置饮食表单
    resetDietForm() {
      this.selectedFoods = [];
      this.foodList = [];
      this.foodSearchKeyword = "";
      this.dietForm = {
        recordDate: this.selectedDate,
        mealType: parseInt(this.activeMealType)
      };
    },

    // 删除饮食记录
    async deleteDietRecord(recordId) {
      try {
        const response = await request.delete(`/diet/delete/${recordId}`);
        if (response.data.code === 200) {
          this.$message.success("删除记录成功");
          this.loadDietRecords();
        } else {
          this.$message.error(response.data.msg || "删除记录失败");
        }
      } catch (error) {
        console.error("删除记录失败:", error);
        this.$message.error("删除记录失败");
      }
    }
  }
};
</script>

<style scoped>
.diet-record {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
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

.header h2 {
  margin: 0;
  color: #333;
}

.date-selector {
  display: flex;
  align-items: center;
  gap: 15px;
}

.nutrition-summary {
  margin-bottom: 20px;
}

.nutrition-item {
  text-align: center;
  padding: 10px;
}

.nutrition-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.nutrition-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.nutrition-progress {
  margin-top: 10px;
}

.meal-records {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.diet-form {
  max-height: 600px;
  overflow-y: auto;
}

.food-search {
  margin: 20px 0;
}

.food-list {
  margin: 20px 0;
}

.selected-foods {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.selected-foods h4 {
  margin-bottom: 15px;
  color: #333;
}

.dialog-footer {
  text-align: right;
}

.el-card {
  border-radius: 8px;
}

.el-tabs {
  margin-top: 10px;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}
</style>