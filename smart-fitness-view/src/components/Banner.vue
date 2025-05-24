<template>
    <div style="position: relative;">
        <img :src="activeData.cover" :style="{ width: width, height: height, borderRadius: borderRadius }" />
        <h3 @click="onClick" class="tip-name" style="position: absolute;bottom: 0;">{{ activeData.name }}</h3>
    </div>
</template>

<script>
// 轮播图组件
export default {
    name: "Banner",
    props: {
        data: {
            type: Array,
            required: true
        },
        width: { // 宽度
            type: String,
            default: '100%'
        },
        height: { // 高度
            type: String,
            default: '208px'
        },
        borderRadius: { // 图片边框曲度
            type: String,
            default: '5px'
        },
        time: { // 轮播时间
            type: Number,
            default: 3000
        }
    },
    watch: {
        data: {
            handler(v1, v2) {
                this.activeData = {... this.data[0]};
                this.config();
            },
            deep: true,
            immediate: true,
        },
    },
    data() {
        return {
            activeData: {},
            index: 0
        }
    },
    methods: {
        onClick(data) {
            this.$emit('on-click', this.activeData);
        },
        config() {
            setInterval(() => {
                this.index = (this.index >= this.data.length) ? 0 : this.index;
                this.activeData = this.data[this.index++];
            }, this.time);
        },
    }
};
</script>

<style scoped lang="scss">
.tip-name {
    text-align: center;
    width: 100%;
    padding: 15px 0;
    color: aliceblue;
    margin: 0;
    margin-bottom: 4px;
    border-bottom-left-radius: 5px;
    border-bottom-right-radius: 5px;
    cursor: pointer;
    background-color: rgba(5, 0, 0, 0.6);
}
</style>
