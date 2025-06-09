<template>
  <div class="nutrition-history-container">
    <a-card title="营养推荐历史" :bordered="false">
      <template #extra>
        <a-button type="primary" @click="generateRecommendation">生成新推荐</a-button>
      </template>
      
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="queryParams" @finish="handleSearch" style="margin-bottom: 16px">
        <a-form-item label="日期范围">
          <a-range-picker 
            v-model:value="dateRange" 
            :format="dateFormat"
            @change="onDateRangeChange"
          />
        </a-form-item>
        <a-form-item label="推荐类型">
          <a-select v-model:value="queryParams.recommendationType" style="width: 120px" allowClear>
            <a-select-option value="DAILY">每日推荐</a-select-option>
            <a-select-option value="WEEKLY">每周推荐</a-select-option>
            <a-select-option value="CUSTOM">自定义</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button style="margin-left: 8px" @click="resetSearch">重置</a-button>
        </a-form-item>
      </a-form>
      
      <!-- 数据表格 -->
      <a-table 
        :columns="columns" 
        :data-source="recommendationList" 
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        rowKey="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'recommendationDate'">
            {{ formatDate(record.recommendationDate) }}
          </template>
          <template v-if="column.dataIndex === 'calories'">
            {{ record.calories }} 千卡
          </template>
          <template v-if="column.dataIndex === 'protein'">
            {{ record.protein }} 克
          </template>
          <template v-if="column.dataIndex === 'carbohydrate'">
            {{ record.carbohydrate }} 克
          </template>
          <template v-if="column.dataIndex === 'fat'">
            {{ record.fat }} 克
          </template>
          <template v-if="column.dataIndex === 'recommendationType'">
            <a-tag :color="getRecommendationTypeColor(record.recommendationType)">
              {{ getRecommendationTypeText(record.recommendationType) }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="viewRecommendation(record)">查看</a-button>
            <a-popconfirm
              title="确定要删除这条记录吗？"
              @confirm="deleteRecommendation(record.id)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- 查看推荐详情对话框 -->
    <a-modal
      v-model:visible="detailModalVisible"
      title="营养推荐详情"
      width="800px"
      :footer="null"
    >
      <a-descriptions title="用户信息" bordered>
        <a-descriptions-item label="姓名">{{ currentRecommendation.userName || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="性别">{{ currentRecommendation.gender || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="年龄">{{ currentRecommendation.age || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="身高">{{ currentRecommendation.height ? `${currentRecommendation.height} 厘米` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="体重">{{ currentRecommendation.weight ? `${currentRecommendation.weight} 千克` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="BMI">{{ currentRecommendation.bmi || '未计算' }}</a-descriptions-item>
      </a-descriptions>
      
      <a-divider />
      
      <a-descriptions title="营养推荐" bordered>
        <a-descriptions-item label="推荐日期">{{ formatDate(currentRecommendation.recommendationDate) }}</a-descriptions-item>
        <a-descriptions-item label="推荐类型">{{ getRecommendationTypeText(currentRecommendation.recommendationType) }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ formatDateTime(currentRecommendation.createTime) }}</a-descriptions-item>
        <a-descriptions-item label="每日卡路里">{{ currentRecommendation.calories || 0 }} 千卡</a-descriptions-item>
        <a-descriptions-item label="蛋白质">{{ currentRecommendation.protein || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="碳水化合物">{{ currentRecommendation.carbohydrate || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="脂肪">{{ currentRecommendation.fat || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="膳食纤维">{{ currentRecommendation.fiber || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="钠">{{ currentRecommendation.sodium || 0 }} 毫克</a-descriptions-item>
      </a-descriptions>
      
      <a-divider />
      
      <a-card title="备注" v-if="currentRecommendation.notes">
        <p>{{ currentRecommendation.notes }}</p>
      </a-card>
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
        <a-descriptions-item label="姓名">{{ newRecommendation.userName || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="性别">{{ newRecommendation.gender || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="年龄">{{ newRecommendation.age || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="身高">{{ newRecommendation.height ? `${newRecommendation.height} 厘米` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="体重">{{ newRecommendation.weight ? `${newRecommendation.weight} 千克` : '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="BMI">{{ newRecommendation.bmi || '未计算' }}</a-descriptions-item>
      </a-descriptions>
      
      <a-divider />
      
      <a-descriptions title="营养推荐" bordered>
        <a-descriptions-item label="每日卡路里">{{ newRecommendation.calories || 0 }} 千卡</a-descriptions-item>
        <a-descriptions-item label="蛋白质">{{ newRecommendation.protein || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="碳水化合物">{{ newRecommendation.carbohydrate || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="脂肪">{{ newRecommendation.fat || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="膳食纤维">{{ newRecommendation.fiber || 0 }} 克</a-descriptions-item>
        <a-descriptions-item label="钠">{{ newRecommendation.sodium || 0 }} 毫克</a-descriptions-item>
      </a-descriptions>
      
      <a-divider />
      
      <a-form :model="newRecommendation" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="备注">
          <a-textarea v-model:value="newRecommendation.notes" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, reactive, computed } from 'vue';
import { message } from 'ant-design-vue';
import { 
  generateNutritionRecommendation, 
  saveNutritionRecommendation, 
  queryNutritionRecommendations,
  getNutritionRecommendationById,
  deleteNutritionRecommendations
} from '@/api/nutrition';
import dayjs from 'dayjs';

export default defineComponent({
  name: 'NutritionHistory',
  setup() {
    // 日期格式
    const dateFormat = 'YYYY-MM-DD';
    
    // 查询参数
    const queryParams = ref({
      userId: null,
      startDate: null,
      endDate: null,
      recommendationType: null,
      pageNum: 0,
      pageSize: 10
    });
    
    // 日期范围
    const dateRange = ref([]);
    
    // 表格列定义
    const columns = [
      {
        title: '推荐日期',
        dataIndex: 'recommendationDate',
        key: 'recommendationDate',
        sorter: true
      },
      {
        title: '卡路里',
        dataIndex: 'calories',
        key: 'calories',
        sorter: true
      },
      {
        title: '蛋白质',
        dataIndex: 'protein',
        key: 'protein'
      },
      {
        title: '碳水化合物',
        dataIndex: 'carbohydrate',
        key: 'carbohydrate'
      },
      {
        title: '脂肪',
        dataIndex: 'fat',
        key: 'fat'
      },
      {
        title: '推荐类型',
        dataIndex: 'recommendationType',
        key: 'recommendationType',
        filters: [
          { text: '每日推荐', value: 'DAILY' },
          { text: '每周推荐', value: 'WEEKLY' },
          { text: '自定义', value: 'CUSTOM' }
        ]
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        fixed: 'right',
        width: 150
      }
    ];
    
    // 数据和加载状态
    const recommendationList = ref([]);
    const loading = ref(false);
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showTotal: (total) => `共 ${total} 条记录`
    });
    
    // 当前查看的推荐
    const currentRecommendation = ref({});
    const detailModalVisible = ref(false);
    
    // 新推荐
    const newRecommendation = ref({
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
    const recommendationModalVisible = ref(false);
    const confirmLoading = ref(false);
    
    // 获取推荐列表
    const fetchRecommendationList = async () => {
      loading.value = true;
      try {
        const res = await queryNutritionRecommendations(queryParams.value);
        if (res.code === 200) {
          recommendationList.value = res.data;
          pagination.total = res.count || 0;
        } else {
          message.error(res.msg || '获取营养推荐列表失败');
        }
      } catch (error) {
        console.error('获取营养推荐列表出错:', error);
        message.error('获取营养推荐列表失败');
      } finally {
        loading.value = false;
      }
    };
    
    // 搜索处理
    const handleSearch = () => {
      pagination.current = 1;
      queryParams.value.pageNum = 0;
      fetchRecommendationList();
    };
    
    // 重置搜索
    const resetSearch = () => {
      queryParams.value = {
        userId: null,
        startDate: null,
        endDate: null,
        recommendationType: null,
        pageNum: 0,
        pageSize: 10
      };
      dateRange.value = [];
      handleSearch();
    };
    
    // 日期范围变化处理
    const onDateRangeChange = (dates, dateStrings) => {
      if (dates && dates.length === 2) {
        queryParams.value.startDate = dateStrings[0];
        queryParams.value.endDate = dateStrings[1];
      } else {
        queryParams.value.startDate = null;
        queryParams.value.endDate = null;
      }
    };
    
    // 表格变化处理
    const handleTableChange = (pag, filters, sorter) => {
      pagination.current = pag.current;
      pagination.pageSize = pag.pageSize;
      queryParams.value.pageNum = (pag.current - 1) * pag.pageSize;
      queryParams.value.pageSize = pag.pageSize;
      
      // 处理筛选
      if (filters.recommendationType && filters.recommendationType.length > 0) {
        queryParams.value.recommendationType = filters.recommendationType[0];
      } else {
        queryParams.value.recommendationType = null;
      }
      
      // 处理排序
      if (sorter.field && sorter.order) {
        queryParams.value.sortField = sorter.field;
        queryParams.value.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc';
      } else {
        queryParams.value.sortField = null;
        queryParams.value.sortOrder = null;
      }
      
      fetchRecommendationList();
    };
    
    // 查看推荐详情
    const viewRecommendation = async (record) => {
      try {
        const res = await getNutritionRecommendationById(record.id);
        if (res.code === 200 && res.data) {
          currentRecommendation.value = res.data;
          detailModalVisible.value = true;
        } else {
          message.error(res.msg || '获取营养推荐详情失败');
        }
      } catch (error) {
        console.error('获取营养推荐详情出错:', error);
        message.error('获取营养推荐详情失败');
      }
    };
    
    // 删除推荐
    const deleteRecommendation = async (id) => {
      try {
        const res = await deleteNutritionRecommendations([id]);
        if (res.code === 200) {
          message.success('删除营养推荐成功');
          fetchRecommendationList();
        } else {
          message.error(res.msg || '删除营养推荐失败');
        }
      } catch (error) {
        console.error('删除营养推荐出错:', error);
        message.error('删除营养推荐失败');
      }
    };
    
    // 生成新推荐
    const generateRecommendation = async () => {
      try {
        const res = await generateNutritionRecommendation();
        if (res.code === 200 && res.data) {
          newRecommendation.value = res.data;
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
        const res = await saveNutritionRecommendation(newRecommendation.value);
        if (res.code === 200) {
          message.success('保存营养推荐成功');
          recommendationModalVisible.value = false;
          fetchRecommendationList();
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
    
    // 格式化日期
    const formatDate = (date) => {
      if (!date) return '';
      return dayjs(date).format(dateFormat);
    };
    
    // 格式化日期时间
    const formatDateTime = (dateTime) => {
      if (!dateTime) return '';
      return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss');
    };
    
    // 获取推荐类型文本
    const getRecommendationTypeText = (type) => {
      const typeMap = {
        'DAILY': '每日推荐',
        'WEEKLY': '每周推荐',
        'CUSTOM': '自定义'
      };
      return typeMap[type] || type;
    };
    
    // 获取推荐类型颜色
    const getRecommendationTypeColor = (type) => {
      const colorMap = {
        'DAILY': 'blue',
        'WEEKLY': 'green',
        'CUSTOM': 'orange'
      };
      return colorMap[type] || 'default';
    };
    
    onMounted(() => {
      fetchRecommendationList();
    });
    
    return {
      dateFormat,
      queryParams,
      dateRange,
      columns,
      recommendationList,
      loading,
      pagination,
      currentRecommendation,
      detailModalVisible,
      newRecommendation,
      recommendationModalVisible,
      confirmLoading,
      handleSearch,
      resetSearch,
      onDateRangeChange,
      handleTableChange,
      viewRecommendation,
      deleteRecommendation,
      generateRecommendation,
      saveRecommendation,
      formatDate,
      formatDateTime,
      getRecommendationTypeText,
      getRecommendationTypeColor
    };
  }
});
</script>

<style scoped>
.nutrition-history-container {
  padding: 24px;
}
</style>