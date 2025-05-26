<template>
    <el-row>
        <el-col :span="18">
            <div style="padding-right: 50px;box-sizing: border-box;">
                <div>
                    <h1>{{ newsInfo.name }}</h1>
                    <div
                        style="font-size: 12px;background-color: rgb(250, 250, 250);padding: 16px 6px;border-radius: 5px;">
                        <span class="news-tags">{{ newsInfo.tagName }}</span>
                        <span style="margin-left: 10px;">{{ parseTime(newsInfo.createTime) }}</span>
                        <span style="margin-left: 10px;">发布者：{{ newsInfo.publisherName }}</span>
                        <span style="margin-left: 10px;"><i class="el-icon-view"></i> {{ newsInfo.viewsNumber || 0 }}</span>
                        <el-button style="margin-left: 20px;" @click="saveNewsOperation" class="customer" size="mini">{{
                            !saveFlag ?
                                '立即收藏' : '取消收藏' }}</el-button>
                    </div>
                    <div v-html="newsInfo.content"></div>
                </div>
                <div>
                    <Evaluations :contentId="newsInfo.id" contentType="NEWS" />
                </div>
            </div>
        </el-col>
        <el-col :span="6">
            <h3 style="padding: 10px 30px;">资讯推荐</h3>
            <el-col @click.native="newsItemClick(news)" :span="24" :key="index" v-for="(news, index) in newsTopList">
                <div style="padding: 25px 30px;box-sizing: border-box;">
                    <img :src="news.cover" :alt="news.name" style="width: 100%;height: 118px;border-radius: 5px;">
                    <h3 class="news-title">{{ news.name }}</h3>
                    <div style="font-size: 12px;">
                        <span class="news-tags">{{ news.tagName }}</span>
                        <span style="margin-left: 10px;">{{ parseTime(news.createTime) }}</span>
                    </div>
                </div>
            </el-col>
        </el-col>
    </el-row>
</template>
<script>
import { timeAgo } from "@/utils/data"
import Evaluations from "@/components/Evaluations.vue";
export default {
    components: { Evaluations },
    name: "NewsDetail",
    data() {
        return {
            newsInfo: {},
            newsTopList: [],
            saveFlag: false,
            newsSaveList: []
        }
    },
    beforeRouteEnter(to, from, next) {
        const newsInfo = sessionStorage.getItem('newsInfo');
        if (!newsInfo) {
            next('/');
            return;
        }
        next(vm => {
            vm.newsInfo = JSON.parse(newsInfo);
            vm.loadSaveStatus();
            // 使用时间戳来确保每次访问都能增加浏览量
            const lastViewTime = sessionStorage.getItem(`lastView_${vm.newsInfo.id}`);
            const currentTime = new Date().getTime();
            // 如果距离上次访问超过30秒，则增加浏览量
            if (!lastViewTime || (currentTime - parseInt(lastViewTime)) > 5000) {
                vm.increaseViews();
                sessionStorage.setItem(`lastView_${vm.newsInfo.id}`, currentTime.toString());
            }
        });
    },
    created() {
        this.loadAllTopNews();
    },
    methods: {
        // 增加浏览次数
        increaseViews() {
            if (!this.newsInfo || !this.newsInfo.id) return;
            
            this.$axios.post('/news/increaseViews', { id: this.newsInfo.id }).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.newsInfo.viewsNumber = data.data;
                }
            });
        },
        loadSaveStatus() {
            if (!this.newsInfo || !this.newsInfo.id) return;
            
            const newsSaveQueryDto = {
                newsId: this.newsInfo.id
            }
            this.$axios.post('/news-save/queryUser', newsSaveQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.saveFlag = data.data.length !== 0;
                }
            })
        },
        // 收藏或取消收藏操作
        saveNewsOperation() {
            if (!this.newsInfo || !this.newsInfo.id) return;
            
            this.$axios.post('/news-save/operation', { newsId: this.newsInfo.id }).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.$message.success(!this.saveFlag ? '收藏成功' : '取消收藏成功');
                    this.saveFlag = !this.saveFlag;
                }
            })
        },
        newsItemClick(news) {
            // 清除之前文章的浏览记录
            if (this.newsInfo && this.newsInfo.id) {
                sessionStorage.removeItem(`lastView_${this.newsInfo.id}`);
            }
            // 更新sessionStorage中的资讯信息
            sessionStorage.setItem('newsInfo', JSON.stringify(news));
            // 重新加载页面以确保所有状态都被正确重置
            this.$router.go(0);
        },
        // 转换时间
        parseTime(time) {
            return timeAgo(time);
        },
        // 查询推荐资讯
        loadAllTopNews() {
            const newQueryDto = { isTop: true };
            this.$axios.post('/news/query', newQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.newsTopList = data.data;
                }
            })
        }
    }
};
</script>

<style scoped lang="scss">
.news-tags {
    display: inline-block;
    padding: 4px 10px;
    color: #1d3cc4;
    border-radius: 3px;
}
</style>
