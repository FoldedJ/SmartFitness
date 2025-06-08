<template>
  <div class="meal-record-list">
    <div v-if="records.length === 0" class="empty-state">
      <el-empty description="暂无饮食记录">
        <el-button type="primary" size="small" @click="$emit('add-record')">添加记录</el-button>
      </el-empty>
    </div>
    
    <div v-else>
      <el-table :data="records" style="width: 100%">
        <el-table-column prop="foodName" label="食物名称" width="150">
          <template slot-scope="scope">
            {{ scope.row.food ? scope.row.food.foodName : '未知食物' }}
          </template>
        </el-table-column>
        
        <el-table-column label="摄入量" width="120">
          <template slot-scope="scope">
            {{ scope.row.consumeAmount }}
            {{ scope.row.food ? scope.row.food.servingUnit : 'g' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="actualCalories" label="热量(kcal)" width="100">
          <template slot-scope="scope">
            {{ scope.row.actualCalories || 0 }}
          </template>
        </el-table-column>
        
        <el-table-column label="蛋白质(g)" width="100">
          <template slot-scope="scope">
            {{ parseFloat(scope.row.actualProtein || 0).toFixed(1) }}
          </template>
        </el-table-column>
        
        <el-table-column label="碳水化合物(g)" width="120">
          <template slot-scope="scope">
            {{ parseFloat(scope.row.actualCarbohydrate || 0).toFixed(1) }}
          </template>
        </el-table-column>
        
        <el-table-column label="脂肪(g)" width="100">
          <template slot-scope="scope">
            {{ parseFloat(scope.row.actualFat || 0).toFixed(1) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="记录时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="viewDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button
              type="text"
              size="small"
              style="color: #f56c6c"
              @click="confirmDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 营养汇总 -->
      <div class="meal-summary">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="summary-item">
              <span class="summary-label">总热量:</span>
              <span class="summary-value">{{ mealTotalCalories }} kcal</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <span class="summary-label">总蛋白质:</span>
              <span class="summary-value">{{ mealTotalProtein.toFixed(1) }} g</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <span class="summary-label">总碳水:</span>
              <span class="summary-value">{{ mealTotalCarbohydrate.toFixed(1) }} g</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <span class="summary-label">总脂肪:</span>
              <span class="summary-value">{{ mealTotalFat.toFixed(1) }} g</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <!-- 详情对话框 -->
    <el-dialog
      title="饮食记录详情"
      :visible.sync="detailDialogVisible"
      width="500px"
    >
      <div v-if="selectedRecord" class="record-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="食物名称">
            {{ selectedRecord.food ? selectedRecord.food.foodName : '未知食物' }}
          </el-descriptions-item>
          <el-descriptions-item label="摄入量">
            {{ selectedRecord.consumeAmount }}
            {{ selectedRecord.food ? selectedRecord.food.servingUnit : 'g' }}
          </el-descriptions-item>
          <el-descriptions-item label="实际热量">
            {{ selectedRecord.actualCalories || 0 }} kcal
          </el-descriptions-item>
          <el-descriptions-item label="实际蛋白质">
            {{ parseFloat(selectedRecord.actualProtein || 0).toFixed(1) }} g
          </el-descriptions-item>
          <el-descriptions-item label="实际碳水化合物">
            {{ parseFloat(selectedRecord.actualCarbohydrate || 0).toFixed(1) }} g
          </el-descriptions-item>
          <el-descriptions-item label="实际脂肪">
            {{ parseFloat(selectedRecord.actualFat || 0).toFixed(1) }} g
          </el-descriptions-item>
          <el-descriptions-item label="记录时间" :span="2">
            {{ formatDate(selectedRecord.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ selectedRecord.notes || '无' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 食物营养信息 -->
        <div v-if="selectedRecord.food" class="food-nutrition">
          <h4>食物营养信息 (每{{ selectedRecord.food.servingSize }}{{ selectedRecord.food.servingUnit }})</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="热量">
              {{ selectedRecord.food.calories || 0 }} kcal
            </el-descriptions-item>
            <el-descriptions-item label="蛋白质">
              {{ parseFloat(selectedRecord.food.protein || 0).toFixed(1) }} g
            </el-descriptions-item>
            <el-descriptions-item label="碳水化合物">
              {{ parseFloat(selectedRecord.food.carbohydrate || 0).toFixed(1) }} g
            </el-descriptions-item>
            <el-descriptions-item label="脂肪">
              {{ parseFloat(selectedRecord.food.fat || 0).toFixed(1) }} g
            </el-descriptions-item>
            <el-descriptions-item label="纤维">
              {{ parseFloat(selectedRecord.food.fiber || 0).toFixed(1) }} g
            </el-descriptions-item>
            <el-descriptions-item label="钠">
              {{ parseFloat(selectedRecord.food.sodium || 0).toFixed(1) }} mg
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "MealRecordList",
  props: {
    records: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      detailDialogVisible: false,
      selectedRecord: null
    };
  },
  computed: {
    // 计算该餐的总热量
    mealTotalCalories() {
      return this.records.reduce((total, record) => {
        return total + (record.actualCalories || 0);
      }, 0);
    },
    
    // 计算该餐的总蛋白质
    mealTotalProtein() {
      return this.records.reduce((total, record) => {
        return total + parseFloat(record.actualProtein || 0);
      }, 0);
    },
    
    // 计算该餐的总碳水化合物
    mealTotalCarbohydrate() {
      return this.records.reduce((total, record) => {
        return total + parseFloat(record.actualCarbohydrate || 0);
      }, 0);
    },
    
    // 计算该餐的总脂肪
    mealTotalFat() {
      return this.records.reduce((total, record) => {
        return total + parseFloat(record.actualFat || 0);
      }, 0);
    }
  },
  methods: {
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
      });
    },
    
    // 查看详情
    viewDetail(record) {
      this.selectedRecord = record;
      this.detailDialogVisible = true;
    },
    
    // 确认删除
    confirmDelete(record) {
      this.$confirm('确定要删除这条饮食记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$emit('delete', record.id);
      }).catch(() => {
        // 用户取消删除
      });
    }
  }
};
</script>

<style scoped>
.meal-record-list {
  min-height: 200px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.meal-summary {
  margin-top: 15px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.summary-item {
  text-align: center;
}

.summary-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.summary-value {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}

.record-detail {
  padding: 10px 0;
}

.food-nutrition {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.food-nutrition h4 {
  margin-bottom: 15px;
  color: #333;
  font-size: 14px;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

.el-table th {
  background-color: #fafafa;
  color: #333;
  font-weight: 600;
}

.el-table td {
  border-bottom: 1px solid #ebeef5;
}

.el-table tr:hover {
  background-color: #f5f7fa;
}

.dialog-footer {
  text-align: right;
}
</style>