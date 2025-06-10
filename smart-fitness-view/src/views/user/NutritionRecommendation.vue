<template>
  <div class="nutrition-recommendation">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>营养推荐</span>
      </div>
      
      <!-- 当前推荐 -->
      <div v-if="currentRecommendation">
        <h3>当前营养推荐</h3>
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-label">卡路里</div>
              <div class="nutrition-value">{{ currentRecommendation.calories || 0 }} kcal</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-label">蛋白质</div>
              <div class="nutrition-value">{{ formatNumber(currentRecommendation.protein) }} g</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-label">碳水化合物</div>
              <div class="nutrition-value">{{ formatNumber(currentRecommendation.carbohydrate) }} g</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-label">脂肪</div>
              <div class="nutrition-value">{{ formatNumber(currentRecommendation.fat) }} g</div>
            </div>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-label">膳食纤维</div>
              <div class="nutrition-value">{{ formatNumber(currentRecommendation.fiber) }} g</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="nutrition-item">
              <div class="nutrition-label">钠</div>
              <div class="nutrition-value">{{ formatNumber(currentRecommendation.sodium) }} mg</div>
            </div>
          </el-col>
        </el-row>
        
        <div v-if="currentRecommendation.notes" style="margin-top: 20px;">
          <h4>推荐说明</h4>
          <p>{{ currentRecommendation.notes }}</p>
        </div>
        

      </div>
      
      <!-- 无推荐时的提示 -->
      <div v-else>
        <el-empty description="暂无营养推荐">
        </el-empty>
      </div>
    </el-card>
    


  </div>
</template>

<script>
import { 
  generateNutritionRecommendation, 
  deleteNutritionRecommendations,
  getLatestNutritionRecommendation
} from '@/api/nutrition';

export default {
  name: 'NutritionRecommendation',
  data() {
    return {
      currentRecommendation: null,
      loading: false
    };
  },
  mounted() {
    this.loadLatestRecommendation();
  },
  methods: {
    // 加载最新的推荐
    async loadLatestRecommendation() {
      try {
        const response = await getLatestNutritionRecommendation();
        console.log('获取最新推荐响应:', response);
        if (response.data.code === 200 && response.data.data) {
          console.log('最新推荐数据:', response.data.data);
          this.currentRecommendation = response.data.data;
        } else {
          console.log('没有找到推荐数据:', response.data.msg);
        }
      } catch (error) {
        console.error('加载最新推荐失败:', error);
      }
    },
    
    // 生成新的营养推荐
    async generateRecommendation() {
      this.loading = true;
      // 先显示一个提示，告知用户生成推荐可能需要较长时间
      this.$message.info('正在生成营养推荐，这可能需要一些时间，请耐心等待...');
      try {
        const response = await generateNutritionRecommendation();
        console.log('生成推荐响应:', response);
        if (response.data.code === 200) {
          // 生成成功后，立即查询数据库获取最新数据
          await this.loadLatestRecommendation();
          this.$message.success('生成营养推荐成功');
        } else {
          console.error('生成推荐失败:', response.data.msg);
          this.$message.warning(response.data.msg || '生成营养推荐暂时失败，请稍后查看结果');
        }
      } catch (error) {
        console.error('生成营养推荐失败:', error);
        this.$message.warning('生成营养推荐暂时失败，请稍后查看结果');
      } finally {
        this.loading = false;
      }
    },
    

    

    
    // 使用推荐
    useRecommendation(recommendation) {
      this.currentRecommendation = { ...recommendation };
      this.$message.success('已应用该推荐');
    },
    
    // 删除推荐
    async deleteRecommendation(id) {
      try {
        await this.$confirm('确定要删除这条推荐记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        const response = await deleteNutritionRecommendations([id]);
        if (response.data.code === 200) {
          this.$message.success('删除成功');
        } else {
          this.$message.error(response.data.msg || '删除失败');
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除推荐失败:', error);
          this.$message.error('删除失败');
        }
      }
    },
    

    
    // 格式化日期
    formatDate(date) {
      if (!date) return '';
      return new Date(date).toLocaleDateString();
    },
    
    // 格式化数字
    formatNumber(value) {
      if (value === null || value === undefined) return '0';
      // 处理字符串或数字
      const num = parseFloat(value);
      return isNaN(num) ? '0' : num.toFixed(1);
    }
  }
};
</script>

<style scoped>
.nutrition-recommendation {
  padding: 20px;
}

.nutrition-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.nutrition-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.nutrition-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.no-recommendation {
  text-align: center;
  padding: 40px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}
</style>