<template>
    <div class="user-container">
        <div class="menus-container"
            style="position: sticky;top: 0;z-index: 1000;background-color: rgb(255, 255, 255);">
            <UserMenu :menus="routers" :userInfo="userInfo" @eventListener="eventListener" />
        </div>
        <div class="content-container">
            <router-view class="route-container"></router-view>
        </div>
        <!-- 个人中心 -->
        <el-dialog :show-close="false" :visible.sync="dialogOperaion" width="26%">
            <div slot="title" style="padding: 25px 0 0 20px;">
                <span style="font-size: 18px;font-weight: 800;">个人中心</span>
            </div>
            <el-row style="padding: 10px 20px 20px 20px;">
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;margin-bottom: 10px;">
                        <span class="modelName">*头像</span>
                    </p>
                    <el-upload class="avatar-uploader" action="http://localhost:21090/api/personal-heath/v1.0/file/upload"
                        :show-file-list="false" :on-success="handleAvatarSuccess">
                        <img v-if="data.url" :src="data.url" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-row>
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*用户名</span>
                    </p>
                    <input class="modelInput" type="text" v-model="data.name" placeholder="用户名">
                </el-row>
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*个人邮箱</span>
                    </p>
                    <input class="modelInput" type="text" v-model="data.email" placeholder="用户名">
                </el-row>
            </el-row>
            <span slot="footer" class="dialog-footer">
                <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
                    @click="dialogOperaion = false">取 消</el-button>
                <el-button size="small" style="background-color: #15559a;border: none;" class="customer" type="info"
                    @click="updateUserInfo">修改</el-button>
            </span>
        </el-dialog>
        <!-- 重置密码 -->
        <el-dialog :show-close="false" :visible.sync="dialogRetPwdOperaion" width="26%">
            <div slot="title" style="padding: 25px 0 0 20px;">
                <span style="font-size: 18px;font-weight: 800;">重置密码</span>
            </div>
            <el-row style="padding: 10px 20px 20px 20px;">
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;margin-bottom: 10px;">
                        <span class="modelName">*原始密码</span>
                    </p>
                    <input class="modelInput" type="password" v-model="pwdEntity.oldPwd" placeholder="原始密码">
                </el-row>
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;margin-bottom: 10px;">
                        <span class="modelName">*新密码</span>
                    </p>
                    <input class="modelInput" type="password" v-model="pwdEntity.newPwd" placeholder="新密码">
                </el-row>
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;margin-bottom: 10px;">
                        <span class="modelName">*确认密码</span>
                    </p>
                    <input class="modelInput" type="password" v-model="pwdEntity.againPwd" placeholder="确认密码">
                </el-row>
            </el-row>
            <span slot="footer" class="dialog-footer">
                <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
                    @click="dialogRetPwdOperaion = false">取 消</el-button>
                <el-button size="small" style="background-color: #15559a;border: none;" class="customer" type="info"
                    @click="updateUserPwd">修改</el-button>
            </span>
        </el-dialog>
            <div style="padding: 10px 20px;">
                <el-row v-for="(checkFood, index) in isCheckFood" :key="index">
                    <el-col :span="10">
                        <img style="width: 100%;height: 100px;border-radius: 5px;" :src="checkFood.cover" />
                    </el-col>
                    <el-col :span="14" style="padding: 0 20px;">
                        <div>
                            <input class="modelInput" type="text" v-model="checkFood.mgValue" placeholder="克数">
                            <span>克</span>
                        </div>
                        <div style="margin: 10px 5px;">
                            <div style="font-size: 16px;">
                                <span>{{ checkFood.name }} - {{ checkFood.tag }}</span>
                            </div>
                            <div style="margin-top: 6px;">
                                <span class="removeFood" @click="removeFood(checkFood)">
                                    <i class="el-icon-circle-close"></i>
                                    移除
                                </span>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </el-dialog>
        <!-- 记录健康指标 -->
        <el-dialog :visible.sync="healthModelConfigDialog" width="28%" :show-close="false">
            <div slot="title">
                <p style="color: rgba(0, 1, 4, 0.8);margin: 0;padding: 20px;font-size: 18px;font-weight: 600;">
                    记录健康指标</p>
            </div>
            <div style="padding: 10px 20px;">
                <el-row>
                    <el-col :span="6">
                        <span @click="addUserHealthHistory"
                            style="margin-top: 10px;display: inline-block;color: white;background-color: #15559a;padding: 5px 8px;border-radius: 3px;font-size: 12px;">
                            <i class="el-icon-circle-plus"></i>
                            确认提交
                        </span>
                    </el-col>
                    <el-col :span="18">
                        <label for="nutrition-select">选择健康指标</label>
                        <select id="nutrition-select" v-model="selecedHealthModelIndex" @change="healthModelChange">
                            <option v-for="option in healthModelConfig" :key="option.modelName" :value="option.id">
                                {{ option.modelName }}
                            </option>
                        </select>
                    </el-col>
                </el-row>
            </div>
            <div style="padding: 10px 20px;">
                <el-row v-for="(healthModel, index) in isCheckHealthModelConfig" :key="index"
                    style="border-bottom: 1px solid #f1f1f1;padding: 15px 0;">
                    <el-col :span="4">
                        <img style="width: 90%;height: 60px;border-radius: 5px;margin-top: 26px;"
                            :src="healthModel.modelIcon" />
                    </el-col>
                    <el-col :span="20" style="padding: 0 20px;">
                        <div>
                            <input class="modelInput" type="text" v-model="healthModel.input" placeholder="数值">
                            <span>{{ healthModel.modelUnit }}</span>
                        </div>
                        <div style="margin: 10px 5px;">
                            <div style="font-size: 16px;">
                                <span>{{ healthModel.modelName }} - {{ healthModel.modelSymbol }}</span>
                            </div>
                            <div style="margin-top: 6px;">
                                <span class="removeFood" @click="removeHealthModel(healthModel)">
                                    <i class="el-icon-circle-close"></i>
                                    移除
                                </span>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import { clearToken } from "@/utils/storage.js";
import router from "@/router";
import UserMenu from '@/components/LevelMenu.vue';
export default {
    name: "UserMain",
    components: {
        UserMenu
    },
    data() {
        return {
            userInfo: {},
            data: {},
            pwdEntity: { oldPwd: '', newPwd: '', againPwd: '' },
            dialogOperaion: false,
            dialogRetPwdOperaion: false,
            foodList: [],
            routers: [],
            isCheckFood: [],
            isCheckHealthModelConfig: [],
            healthModelConfig: [],
            selecedFoodIndex: 0,
            selecedHealthModelIndex: 0,
            dietDialog: false,
            healthModelConfigDialog: false
        };
    },
    created() {
        this.tokenCheckLoad();
    },
    methods: {
        healthModelChange() {
            const healthModel = this.healthModelConfig[this.selecedHealthModelIndex - 2];
            const exists = this.isCheckHealthModelConfig.some(item => item.id === healthModel.id);
            // 如果不存在，则添加新选的健康配置项
            if (!exists) {
                this.isCheckHealthModelConfig.unshift(healthModel);
            } else {
                console.log("指标项已经添加");
            }
        },
        updateUserPwd() {
            this.resetPwd();
        },
        async updateUserInfo() {
            try {
                const userUpdateDTO = {
                    userAvatar: this.data.url,
                    userName: this.data.name,
                    userEmail: this.data.email
                }
                const resposne = await this.$axios.put(`/user/update`, userUpdateDTO);
                const { data } = resposne;
                if (data.code === 200) {
                    this.dialogOperaion = false;
                    this.tokenCheckLoad();
                    this.$swal.fire({
                        title: '修改个人信息',
                        text: data.msg,
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    });
                }
            } catch (e) {
                this.dialogOperaion = false;
                this.$swal.fire({
                    title: '修改个人信息异常',
                    text: e,
                    icon: 'error',
                    showConfirmButton: false,
                    timer: 2000,
                });
                console.error(`修改个人信息异常:${e}`);
            }
        },
        async resetPwd() {
            try {
                const { oldPwd, newPwd, againPwd } = this.pwdEntity;
                if (!oldPwd || !newPwd || !againPwd) {
                    this.$message(`任意项不为空`);
                    return;
                }
                if (newPwd !== againPwd) {
                    this.$message(`前后密码输入不一致`);
                    return;
                }
                const pwdDTO = {
                    oldPwd: this.$md5(this.$md5(oldPwd)),
                    newPwd: this.$md5(this.$md5(newPwd))
                }
                const resposne = await this.$axios.put(`/user/updatePwd`, pwdDTO);
                const { data } = resposne;
                if (data.code === 200) {
                    this.dialogRetPwdOperaion = false;
                    this.$swal.fire({
                        title: '修改密码',
                        text: data.msg,
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    });
                    setTimeout(() => {
                        clearToken();
                        this.$router.push(`/login`);
                    }, 1200)
                } else {
                    this.$message.error(data.msg);
                }
            } catch (e) {
                this.dialogOperaion = false;
                this.$error(data.msg);
                console.error(`修改密码异常:${e}`);
            }
        },
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error(`头像上传异常`);
                return;
            }
            this.$message.success(`头像上传成功`);
            this.data.url = res.data;
        },
        // 监听菜单点击事件
        eventListener(event) {
            // 个人中心
            if (event === 'center') {
                this.dialogOperaion = !this.dialogOperaion;
            }
            // 密码重置
            else if (event === 'resetPwd') {
                this.dialogRetPwdOperaion = true;
                // 搜索页搜索
            } else if (event === 'search-detail') {
                this.$router.push('/search-detail');
            }
            // 退出登录
            else if (event === 'loginOut') {
                this.loginOutOperation();
            }
            // 健康指标记录
            else if (event === 'healthDataRecord') {
                this.$router.push('/record');
            }
        },
        removeFood(food) {
            // 清空输入项
            food.mgValue = '';
            this.isCheckFood = this.isCheckFood.filter(item => item.id !== food.id);
        },
        removeHealthModel(healthModel) {
            // 清空输入项
            healthModel.input = '';
            this.isCheckHealthModelConfig = this.isCheckHealthModelConfig.filter(item => item.id !== healthModel.id);
        },
        foodChange() {
            const food = this.foodList[this.selecedFoodIndex - 1];
            const exists = this.isCheckFood.some(item => item.id === food.id);
            // 如果不存在，则添加新选的菜单
            if (!exists) {
                this.isCheckFood.unshift(food);
            } else {
                console.log("菜谱已经添加");
            }
        },
        // 提交饮食记录
        async addUserHealthHistory() {
            const healthModels = this.isCheckHealthModelConfig.map(entity => {
                return {
                    healthModelId: entity.id,
                    inputValue: entity.input
                }
            });
            try {
                const response = await this.$axios.post(`/user-health/save`, healthModels);
                const { data } = response;
                if (data.code === 200) {
                    this.healthModelConfigDialog = false;
                    this.isCheckHealthModelConfig = [];
                    this.$swal.fire({
                        title: '记录健康指标',
                        text: '记录成功',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1500,
                    });
                }
            } catch (e) {
                console.error(`记录健康指标异常`, e);
            }
        },
        // 提交饮食记录
        async addDietHistory() {
            const foodIds = this.isCheckFood.map(entity => entity.id).join(",");
            const foodNum = this.isCheckFood.map(entity => entity.mgValue).join(",");
            const diet = {
                foodIds: foodIds,
                foodNum: foodNum
            }
            try {
                const response = await this.$axios.post(`/diet/save`, diet);
                const { data } = response;
                if (data.code === 200) {
                    this.dietDialog = false;
                    this.isCheckFood = [];
                    this.$swal.fire({
                        title: '记录饮食，拥抱生活',
                        text: data.msg,
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1500,
                    });
                }
            } catch (e) {
                console.error(`饮食新增异常：`, e);
            }
        },
        async loadHealthModelConfigList() {
            try {
                const response = await this.$axios.post(`/health-model-config/list`, {});
                const { data } = response;
                this.healthModelConfig = data.data;
            } catch (e) {
                console.error(`查询健康配置异常：`, e);
            }
        },
        async loadFoodList() {
            try {
                const response = await this.$axios.post(`/food/list`, {});
                const { data } = response;
                this.foodList = data.data;
            } catch (e) {
                console.error(`查询饮食配置异常：`, e);
            }
        },
        async loginOutOperation() {
            const confirmed = await this.$swalConfirm({
                title: '退出登录',
                text: `退出登录后，需重新登录才能使用系统功能!`,
                icon: 'warning',
            });
            if (confirmed) { // 清除Token，路由至登录页
                clearToken();
                this.$router.push('/login');
            }
        },
        // Token检验
        async tokenCheckLoad() {
            try {
                const res = await this.$axios.get('user/auth');
                // 校验失败
                if (res.data.code === 400) {
                    clearToken();
                    this.$message.error(res.data.msg);
                    this.$router.push('/login');
                    return;
                }
                const { id: userId, userAvatar, userName, userRole, userEmail } = res.data.data;
                // 将用户信息存储起来
                sessionStorage.setItem('userInfo', JSON.stringify(res.data.data));
                this.userInfo = {
                    url: userAvatar,
                    name: userName,
                    role: userRole,
                    email: userEmail
                };
                this.data = { ...this.userInfo };
                // 根据角色解析路由
                const roleRouteKey = userRole === 1 ? 'admin' : 'user';
                const roleRoute = router.options.routes.find(route => route.path.startsWith(`/${roleRouteKey}`));
                if (roleRoute) {
                    this.routers = roleRoute.children;
                } else {
                    console.error('未能找到对应角色的路由配置');
                }
            } catch (error) {
                console.error('Token检验时发生错误:', error);
            }
        }
    },
};
</script>
<style scoped lang="scss">
#nutrition-select {
    margin: 10px;
    padding: 5px;
    font-size: 16px;
    border: none;
    outline: none;
    width: 60%;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.6);
}

.removeFood {
    color: rgba(0, 0, 0, 0.6);
    user-select: none;
    cursor: pointer;
    display: inline-block;
    margin-top: 10px;
}

.removeFood:hover {
    color: #1c1c1c;
}

label {
    font-size: 14px;
    color: rgba(0, 0, 0, 0.6);
}

.content-container {
    padding: 10px 180px;
    box-sizing: border-box;
    min-height: calc(100vh - 200px);
    overflow-x: hidden;
}

.modelInput {
    outline: none;
    border: none;
    font-size: 22px;
    width: 60%;
    font-weight: 800;
}

.avatar {
    width: 88px;
    height: 88px;
}
</style>
