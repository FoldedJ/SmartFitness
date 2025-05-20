<template>
    <div>
        <el-row>
            <el-col :span="7">
                <Banner :data="newsTopList" @on-click="onBannerClick" />
            </el-col>
            <el-col :span="17">
                <el-col @click.native="newsItemClick(news)" :span="6" :key="index" v-for="(news, index) in newsTopList">
                    <div style="padding: 0 10px;">
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
        <el-row>
            <TagLine :dataList="tagsList" @on-click="tagOnClick" />
        </el-row>
        <el-row>
            <el-row v-if="newsList.length === 0">
                <el-empty description="暂无资讯"></el-empty>
            </el-row>
            <el-row>
                <el-col class="new-item" @click.native="newsItemClick(news)" :span="4" :key="index"
                    v-for="(news, index) in newsList">
                    <img :src="news.cover" :alt="news.name">
                    <h3 class="news-title">{{ news.name }}</h3>
                    <div style="font-size: 12px;">
                        <span class="news-tags">{{ news.tagName }}</span>
                        <span style="margin-left: 10px;">{{ parseTime(news.createTime) }}</span>
                    </div>
                </el-col>
            </el-row>
        </el-row>

    </div>
</template>
<script>
import TagLine from "@/components/TagLine"
import Banner from "@/components/Banner"
import { timeAgo } from "@/utils/data"
export default {
    components: { TagLine, Banner },
    data() {
        return {
            tagsList: [], // 标签列表
            newsList: [], // 健康资讯列表
            newsTopList: [], // 推荐的健康资讯数据列表
            newQueryDto: { tagId: null },
        }
    },
    created() {
        this.loadAllTags();
        this.loadAllNews();
        this.loadAllTopNews();
    },
    methods: {
        // 轮播图点击事件回传
        onBannerClick(banner) {
            sessionStorage.setItem('newsInfo', JSON.stringify(banner));
            this.$router.push('/news-detail');
        },
        // 健康资讯列表的项点击事件
        newsItemClick(news) {
            sessionStorage.setItem('newsInfo', JSON.stringify(news));
            this.$router.push('/news-detail');
        },
        // 转换时间
        parseTime(time) {
            return timeAgo(time);
        },
        tagOnClick(tags) {
            this.newQueryDto.tagId = tags.id;
            this.loadAllNews();
        },
        // 查询全部的标签记录
        loadAllTags() {
            this.$axios.post('/tags/query', {}).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.tagsList = data.data;
                    this.tagsList.unshift({ name: '全部', id: null });
                }
            })
        },
        // 查标签下的资讯信息
        loadAllTopNews() {
            const newQueryDto = { isTop: true };
            this.$axios.post('/news/query', newQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.newsTopList = data.data;
                }
            })
        },
        // 查标签下的资讯信息
        loadAllNews() {
            this.$axios.post('/news/query', this.newQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.newsList = data.data;
                }
            })
        }
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

.new-item {
    margin-bottom: 10px;
    padding: 10px;
    box-sizing: border-box;

    img {
        width: 100%;
        height: 118px;
        border-radius: 3px;
    }
}

.new-item:hover {
    background-color: rgb(248, 248, 248);
}

.news-title {
    overflow: hidden;
    /* 显示省略符号来代表被修剪的文本。 */
    text-overflow: ellipsis;
    /* 文本不换行 */
    white-space: nowrap;
}
</style>