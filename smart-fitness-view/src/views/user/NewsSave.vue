<template>
    <el-row>
        <el-row v-if="newsSaveList.length === 0">
            <el-empty description="暂未收藏资讯"></el-empty>
        </el-row>
        <el-row style="margin-top: 20px;">
            <el-col @click.native="newsItemClick(news)" :span="4" :key="index" v-for="(news, index) in newsSaveList">
                <div style="padding: 0 10px;">
                    <img :src="news.cover" :alt="news.name" style="width: 100%;height: 118px;border-radius: 5px;">
                    <h3 class="news-title">{{ news.name }}</h3>
                    <div style="font-size: 12px;">
                        <span class="news-tags">{{ news.tagName }}</span>
                        <span style="margin-left: 10px;">收藏于 {{ parseTime(news.createTime) }}</span>
                        <span style="margin-left: 10px;"><i class="el-icon-view"></i> {{ news.viewsNumber }}</span>
                    </div>
                </div>
            </el-col>
        </el-row>
    </el-row>

</template>
<script>
import { timeAgo } from '@/utils/data';
import { getUserInfo } from '@/utils/user';

export default {
    name: 'NewsSave',
    data() {
        return {
            newsSaveList: []
        };
    },
    created() {
        this.loadAllSaveNews();
    },
    methods: {
        // 转换时间
        parseTime(time) {
            return timeAgo(time);
        },
        // 查看详情
        toDetail(news) {
            const newsInfo = {
                title: news.newsTitle,
                content: news.newsContent,
                cover: news.newsCover,
                createTime: news.createTime,
                id: news.newsId
            };
            sessionStorage.setItem('newsInfo', JSON.stringify(newsInfo));
            this.$router.push('/news-detail');
        },
        loadAllSaveNews() {
            // 查询条件，带上ID
            const userInfoEntity = getUserInfo();
            if (!userInfoEntity) return;
            
            const newsSaveQueryDto = {
                userId: userInfoEntity.id
            }
            this.$axios.post('/news-save/query', newsSaveQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.newsSaveList = data.data;
                }
            })
        },
    }
};
</script>
<style scoped lang="scss">
.news-tags {
    display: inline-block;
    padding: 2px 5px;
    background-color: rgb(222, 243, 251);
    color: #1d3cc4;
    border-radius: 3px;
}

.news-title {
    overflow: hidden;
    /* 显示省略符号来代表被修剪的文本。 */
    text-overflow: ellipsis;
    /* 文本不换行 */
    white-space: nowrap;
}
</style>