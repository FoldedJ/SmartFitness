<template>
  <div class="nutrition-target-container">
    <a-card title="我的营养目标" :bordered="false">
      <template #extra>
        <a-button type="primary" @click="editTarget">编辑目标</a-button>
        <a-button style="margin-left: 8px" @click="generateRecommendation">生成推荐</a-button>
      </template>
      
      <a-row :gutter="16">
        <a-col :span="8">
          <a-statistic title="每日卡路里" :value="targetData.calories" suffix="千卡" :precision="0" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="蛋白质" :value="targetData.protein" suffix="克" :precision="1" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="碳水化合物" :value="targetData.carbohydrate" suffix="克" :precision="1" />
        </a-col>
      </a-row>
      
      <a-divider />
      
      <a-row :gutter="16">
        <a-col :span="8">
          <a-statistic title="脂肪" :value="targetData.fat" suffix="克" :precision="1" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="膳食纤维" :value="targetData.fiber" suffix="克" :precision="1" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="钠" :value="targetData.sodium" suffix="毫克" :precision="1" />
        </a-col>
      </a-row>
      
      <a-divider />
      
      <a-descriptions title="用户信息" bordered>
        <a-descriptions-item label="姓名">{{ targetData.userName || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="性别">{{ targetData.gender || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="年龄">{{ targetData.age || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="身高">{{ targetData.height ? `${targetData.height} 厘米` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="体重">{{ targetData.weight ? `${targetData.weight} 千克` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="BMI">{{ targetData.bmi || '未计算' }}</a-descriptions-item>
      </a-descriptions>
    </a-card>
    
    <!-- 编辑目标对话框 -->
    <a-modal
      v-model:visible="targetModalVisible"
      title="编辑营养目标"
      @ok="saveTarget"
      :confirmLoading="confirmLoading"
      width="600px"
    >
      <a-form :model="editingTarget" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="每日卡路里">
          <a-input-number v-model:value="editingTarget.calories" :min="0" :max="10000" style="width: 100%" addon-after="千卡" />
        </a-form-item>
        <a-form-item label="蛋白质">
          <a-input-number v-model:value="editingTarget.protein" :min="0" :max="1000" :precision="1" style="width: 100%" addon-after="克" />
        </a-form-item>
        <a-form-item label="碳水化合物">
          <a-input-number v-model:value="editingTarget.carbohydrate" :min="0" :max="1000" :precision="1" style="width: 100%" addon-after="克" />
        </a-form-item>
        <a-form-item label="脂肪">
          <a-input-number v-model:value="editingTarget.fat" :min="0" :max="1000" :precision="1" style="width: 100%" addon-after="克" />
        </a-form-item>
        <a-form-item label="膳食纤维">
          <a-input-number v-model:value="editingTarget.fiber" :min="0" :max="100" :precision="1" style="width: 100%" addon-after="克" />
        </a-form-item>
        <a-form-item label="钠">
          <a-input-number v-model:value="editingTarget.sodium" :min="0" :max="10000" :precision="1" style="width: 100%" addon-after="毫克" />
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 营养推荐对话框 -->
    <a-modal
      v-model:visible="recommendationModalVisible"
      title="营养推荐"
      @ok="saveRecommendation"
      :confirmLoading="confirmLoading"
      width="800px"
    >
      <a-descriptions title="用户信息" bordered>
        <a-descriptions-item label="姓名">{{ recommendationData.userName || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="性别">{{ recommendationData.gender || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="年龄">{{ recommendationData.age || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="身高">{{ recommendationData.height ? `${recommendationData.height} 厘米` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="体重">{{ recommendationData.weight ? `${recommendationData.weight} 千克` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="BMI">{{ recommendationData.bmi || '未计算' }}</a-descriptions-item>
      </a-descriptions>
      
      <a-divider />
      
      <a-descriptions title="营养推荐" bordered>
        <a-descriptions-item label="每日卡路里">{{ recommendationData.calories || 0 }} 千卡</a-descriptions-item>
        <a-descriptions-item label="蛋白质">{{ recommendationData.protein || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="碳水化合物">{{ recommendationData.carbohydrate || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="脂肪">{{ recommendationData.fat || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="膳食纤维">{{ recommendationData.fiber || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="钠">{{ recommendationData.sodium || 0 }} 毫克</a-descriptions-item>
      </a-descriptions>
      
      <a-divider />
      
      <a-form :model="recommendationData" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="备注">
          <a-textarea v-model:value="recommendationData.notes" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { getNutritionTarget, saveNutritionTarget, generateNutritionRecommendation, saveNutritionRecommendation } from '@/api/nutrition';

export default defineComponent({
  name: 'NutritionTarget',
  setup() {
    // 营养目标数据
    const targetData = ref({
      id: null,
      userId: null,
      calories: 0,
      protein: 0,
      carbohydrate: 0,
      fat: 0,
      fiber: 0,
      sodium: 0,
      userName: '',
      gender: '',
      age: null,
      height: null,
      weight: null,
      bmi: null
    });
    
    // 编辑中的目标数据
    const editingTarget = ref({
      id: null,
      userId: null,
      calories: 0,
      protein: 0,
      carbohydrate: 0,
      fat: 0,
      fiber: 0,
      sodium: 0
    });
    
    // 营养推荐数据
    const recommendationData = ref({
      id: null,
      userId: null,
      recommendationDate: null,
      calories: 0,
      protein: 0,
      carbohydrate: 0,
      fat: 0,
      fiber: 0,
      sodium: 0,
      recommendationType: 'DAILY',
      notes: '',
      userName: '',
      gender: '',
      age: null,
      height: null,
      weight: null,
      bmi: null
    });
    
    // 对话框控制
    const targetModalVisible = ref(false);
    const recommendationModalVisible = ref(false);
    const confirmLoading = ref(false);
    
    // 获取用户营养目标
    const fetchNutritionTarget = async () => {
      try {
        const res = await getNutritionTarget();
        if (res.code === 200 && res.data) {
          targetData.value = res.data;
        } else {
          message.error(res.msg || '获取营养目标失败');
        }
      } catch (error) {
        console.error('获取营养目标出错:', error);
        message.error('获取营养目标失败');
      }
    };
    
    // 编辑目标
    const editTarget = () => {
      editingTarget.value = {
        id: targetData.value.id,
        userId: targetData.value.userId,
        calories: targetData.value.calories,
        protein: targetData.value.protein,
        carbohydrate: targetData.value.carbohydrate,
        fat: targetData.value.fat,
        fiber: targetData.value.fiber,
        sodium: targetData.value.sodium
      };
      targetModalVisible.value = true;
    };
    
    // 保存目标
    const saveTarget = async () => {
      confirmLoading.value = true;
      try {
        const res = await saveNutritionTarget(editingTarget.value);
        if (res.code === 200) {
          message.success('保存营养目标成功');
          targetModalVisible.value = false;
          await fetchNutritionTarget(); // 重新获取数据
        } else {
          message.error(res.msg || '保存营养目标失败');
        }
      } catch (error) {
        console.error('保存营养目标出错:', error);
        message.error('保存营养目标失败');
      } finally {
        confirmLoading.value = false;
      }
    };
    
    // 生成营养推荐
    const generateRecommendation = async () => {
      try {
        const res = await generateNutritionRecommendation();
        if (res.code === 200 && res.data) {
          recommendationData.value = res.data;
          recommendationModalVisible.value = true;
        } else {
          message.error(res.msg || '生成营养推荐失败');
        }
      } catch (error) {
        console.error('生成营养推荐出错:', error);
        message.error('生成营养推荐失败');
      }
    };
    
    // 保存推荐
    const saveRecommendation = async () => {
      confirmLoading.value = true;
      try {
        const res = await saveNutritionRecommendation(recommendationData.value);
        if (res.code === 200) {
          message.success('保存营养推荐成功');
          recommendationModalVisible.value = false;
        } else {
          message.error(res.msg || '保存营养推荐失败');
        }
      } catch (error) {
        console.error('保存营养推荐出错:', error);
        message.error('保存营养推荐失败');
      } finally {
        confirmLoading.value = false;
      }
    };
    
    onMounted(() => {
      fetchNutritionTarget();
    });
    
    return {
      targetData,
      editingTarget,
      recommendationData,
      targetModalVisible,
      recommendationModalVisible,
      confirmLoading,
      editTarget,
      saveTarget,
      generateRecommendation,
      saveRecommendation
    };
  }
});
</script>

<style scoped>
.nutrition-target-container {
  padding: 24px;
}

.ant-statistic {
  text-align: center;
}
</style>