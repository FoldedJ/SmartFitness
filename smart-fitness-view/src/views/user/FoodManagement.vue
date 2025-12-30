<template>
  <div class="food-management">
    <div class="header">
      <h2>饮食管理</h2>
      <div class="search-bar">
        <el-input
          v-model="searchForm.foodName"
          placeholder="请输入食物名称"
          style="width: 300px; margin-right: 10px;"
          @keyup.enter.native="searchFood"
        >
          <el-button slot="append" icon="el-icon-search" @click="searchFood"></el-button>
        </el-input>
        <el-button type="primary" @click="resetSearch">重置</el-button>
        <el-button type="success" @click="showAddFoodDialog">新建食物</el-button>
      </div>
    </div>

    <div class="content">
      <el-table
        :data="foodList"
        style="width: 100%"
        v-loading="loading"
        element-loading-text="加载中..."
      >
        <el-table-column prop="foodName" label="食物名称" width="150"></el-table-column>
        <el-table-column prop="servingSize" label="份量" width="80">
          <template slot-scope="scope">
            {{ scope.row.servingSize }}{{ scope.row.servingUnit }}
          </template>
        </el-table-column>
        <el-table-column prop="calories" label="热量(kcal)" width="100">
          <template slot-scope="scope">
            {{ scope.row.calories || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="protein" label="蛋白质(g)" width="100">
          <template slot-scope="scope">
            {{ scope.row.protein || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="carbohydrate" label="碳水化合物(g)" width="120">
          <template slot-scope="scope">
            {{ scope.row.carbohydrate || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="fat" label="脂肪(g)" width="100">
          <template slot-scope="scope">
            {{ scope.row.fat || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="fiber" label="纤维(g)" width="100">
          <template slot-scope="scope">
            {{ scope.row.fiber || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="sodium" label="钠(mg)" width="100">
          <template slot-scope="scope">
            {{ scope.row.sodium || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="120"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
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
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        >
        </el-pagination>
      </div>
    </div>

    <!-- 食物详情对话框 -->
    <el-dialog
      title="食物详情"
      :visible.sync="detailDialogVisible"
      width="600px"
    >

      <div v-if="selectedFood" class="food-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="食物名称">
            <span style="font-weight: bold;">{{ selectedFood.foodName }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="份量">
            {{ selectedFood.servingSize }}{{ selectedFood.servingUnit }}
          </el-descriptions-item>
          <el-descriptions-item label="来源">
            <el-tag size="small" :type="selectedFood.source === '官方' ? 'success' : 'info'">{{ selectedFood.source || '未知' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(selectedFood.createTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px;">
          <span style="font-size: 16px; font-weight: bold; color: #303133; margin-bottom: 15px; display: block;">营养成分 (每份)</span>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="hover" :body-style="{ padding: '15px', textAlign: 'center' }">
                <div style="color: #F56C6C; font-size: 24px; font-weight: bold; margin-bottom: 5px;">
                  {{ selectedFood.calories || 0 }}
                </div>
                <div style="color: #909399; font-size: 14px;">热量 (kcal)</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" :body-style="{ padding: '15px', textAlign: 'center' }">
                <div style="color: #67C23A; font-size: 24px; font-weight: bold; margin-bottom: 5px;">
                  {{ selectedFood.protein || 0 }}
                </div>
                <div style="color: #909399; font-size: 14px;">蛋白质 (g)</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" :body-style="{ padding: '15px', textAlign: 'center' }">
                <div style="color: #E6A23C; font-size: 24px; font-weight: bold; margin-bottom: 5px;">
                  {{ selectedFood.fat || 0 }}
                </div>
                <div style="color: #909399; font-size: 14px;">脂肪 (g)</div>
              </el-card>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 15px;">
            <el-col :span="8">
              <el-card shadow="hover" :body-style="{ padding: '15px', textAlign: 'center' }">
                <div style="color: #409EFF; font-size: 24px; font-weight: bold; margin-bottom: 5px;">
                  {{ selectedFood.carbohydrate || 0 }}
                </div>
                <div style="color: #909399; font-size: 14px;">碳水化合物 (g)</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" :body-style="{ padding: '15px', textAlign: 'center' }">
                <div style="color: #909399; font-size: 24px; font-weight: bold; margin-bottom: 5px;">
                  {{ selectedFood.fiber || 0 }}
                </div>
                <div style="color: #909399; font-size: 14px;">纤维 (g)</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" :body-style="{ padding: '15px', textAlign: 'center' }">
                <div style="color: #909399; font-size: 24px; font-weight: bold; margin-bottom: 5px;">
                  {{ selectedFood.sodium || 0 }}
                </div>
                <div style="color: #909399; font-size: 14px;">钠 (mg)</div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 新建食物对话框 -->
    <el-dialog
      title="新建食物"
      :visible.sync="addFoodDialogVisible"
      width="600px"
    >
      <el-form :model="foodForm" :rules="foodRules" ref="foodForm" label-width="120px">
        <el-form-item label="食物名称" prop="foodName">
          <el-input v-model="foodForm.foodName" placeholder="请输入食物名称"></el-input>
        </el-form-item>
        <el-form-item label="份量" prop="servingSize">
          <el-input-number v-model="foodForm.servingSize" :min="0" :precision="2"></el-input-number>
          <el-input v-model="foodForm.servingUnit" style="width: 80px; margin-left: 10px;" placeholder="单位"></el-input>
        </el-form-item>
        <el-form-item label="热量(kcal)" prop="calories">
          <el-input-number v-model="foodForm.calories" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="蛋白质(g)" prop="protein">
          <el-input-number v-model="foodForm.protein" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="碳水化合物(g)" prop="carbohydrate">
          <el-input-number v-model="foodForm.carbohydrate" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="脂肪(g)" prop="fat">
          <el-input-number v-model="foodForm.fat" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="纤维(g)" prop="fiber">
          <el-input-number v-model="foodForm.fiber" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="钠(mg)" prop="sodium">
          <el-input-number v-model="foodForm.sodium" :min="0" :precision="2"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addFoodDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddFood">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request.js";

export default {
  name: "FoodManagement",
  data() {
    return {
      loading: false,
      foodList: [],
      searchForm: {
        foodName: "",
        source: ""
      },
      pagination: {
        current: 1,
        size: 20,
        total: 0
      },
      detailDialogVisible: false,
      selectedFood: null,
      addFoodDialogVisible: false,
      foodForm: {
        foodName: "",
        servingSize: "100",
        servingUnit: "g",
        calories: "0",
        protein: "0",
        carbohydrate: "0",
        fat: "0",
        fiber: "0",
        sodium: "0"
      },
      foodRules: {
        foodName: [
          { required: true, message: "请输入食物名称", trigger: "blur" }
        ],
        servingSize: [
          { required: true, message: "请输入份量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "请输入有效的数字", trigger: "blur" }
        ],
        calories: [
          { required: true, message: "请输入卡路里", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "请输入有效的数字", trigger: "blur" }
        ],
        protein: [
          { required: true, message: "请输入蛋白质含量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "请输入有效的数字", trigger: "blur" }
        ],
        carbohydrate: [
          { required: true, message: "请输入碳水化合物含量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "请输入有效的数字", trigger: "blur" }
        ],
        fat: [
          { required: true, message: "请输入脂肪含量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "请输入有效的数字", trigger: "blur" }
        ],
        fiber: [
          { required: true, message: "请输入膳食纤维含量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "请输入有效的数字", trigger: "blur" }
        ],
        sodium: [
          { required: true, message: "请输入钠含量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "请输入有效的数字", trigger: "blur" }
        ]
      },
      userInfo: {}
    };
  },
  mounted() {
    this.loadFoodList();
    this.getUserInfo();
  },
  methods: {
    // 加载食物列表
    async loadFoodList() {
      this.loading = true;
      try {
        const params = {
          ...this.searchForm,
          current: this.pagination.current,
          size: this.pagination.size
        };
        
        const response = await request.post("/food/query", params);
        if (response.data.code === 200) {
          this.foodList = response.data.data || [];
          this.pagination.total = response.data.total || 0;
        } else {
          this.$message.error(response.data.msg || "加载食物列表失败");
        }
      } catch (error) {
        console.error("加载食物列表错误:", error);
        this.$message.error("加载食物列表失败，请重试");
      } finally {
        this.loading = false;
      }
    },

    // 搜索食物
    searchFood() {
      this.pagination.current = 1;
      this.loadFoodList();
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        foodName: "",
        source: ""
      };
      this.pagination.current = 1;
      this.loadFoodList();
    },

    // 查看详情
    viewDetail(food) {
      this.selectedFood = food;
      this.detailDialogVisible = true;
    },

    // 分页大小改变
    handleSizeChange(val) {
      this.pagination.size = val;
      this.pagination.current = 1;
      this.loadFoodList();
    },

    // 当前页改变
    handleCurrentChange(val) {
      this.pagination.current = val;
      this.loadFoodList();
    },

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

    // 获取用户信息
    getUserInfo() {
      const userInfoStr = sessionStorage.getItem('userInfo');
      if (userInfoStr) {
        this.userInfo = JSON.parse(userInfoStr);
      }
    },

    // 显示添加食物对话框
    showAddFoodDialog() {
      this.resetFoodForm();
      this.addFoodDialogVisible = true;
    },

    // 重置食物表单
    resetFoodForm() {
      this.foodForm = {
        foodName: "",
        servingSize: "100",
        servingUnit: "g",
        calories: "0",
        protein: "0",
        carbohydrate: "0",
        fat: "0",
        fiber: "0",
        sodium: "0"
      };
      if (this.$refs.foodForm) {
        this.$refs.foodForm.resetFields();
      }
    },

    // 提交添加食物
    submitAddFood() {
      this.$refs.foodForm.validate(async (valid) => {
        if (valid) {
          try {
            this.loading = true;
            const foodData = {
              ...this.foodForm,
              source: this.userInfo.userName || '用户添加'
            };
            
            console.log("提交的食物数据:", foodData);
            
            const response = await request.post("/food/save", foodData);
            if (response.data.code === 200) {
              this.$message.success("添加食物成功");
              this.addFoodDialogVisible = false;
              this.resetFoodForm();
              this.loadFoodList();
            } else {
              this.$message.error(response.data.msg || "添加食物失败");
              console.error("服务器返回错误:", response.data);
            }
          } catch (error) {
            console.error("添加食物错误:", error);
            let errorMsg = "添加食物失败，请稍后重试";
            if (error.response && error.response.data) {
              errorMsg = error.response.data.msg || errorMsg;
              console.error("服务器错误详情:", error.response.data);
            }
            this.$message.error(errorMsg);
          } finally {
            this.loading = false;
          }
        } else {
          console.log("表单验证失败");
          this.$message.warning("请正确填写所有必填字段");
        }
      });
    }
  }
};
</script>

<style scoped>
.food-management {
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

.search-bar {
  display: flex;
  align-items: center;
}

.content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.food-detail {
  padding: 10px 0;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.detail-item label {
  font-weight: bold;
  color: #666;
  width: 100px;
  flex-shrink: 0;
}

.detail-item span {
  color: #333;
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
</style>