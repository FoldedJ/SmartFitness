<template>
    <div style="padding: 20px 50px;">
        <div>
            <p style="font-size: 24px;padding: 10px 0;font-weight: bolder;">
                <span @click="goBack" style="cursor: pointer;display: inline-block;padding: 0 20px 0 0;">
                    <i class="el-icon-arrow-left"></i>
                    返回首页
                </span>
                发布资讯
            </p>
        </div>
        <div style="height: 6px;background-color: rgb(248, 248, 248);"></div>
        <div style="padding: 20px 0;">
            <el-form :model="newsForm" :rules="rules" ref="newsForm" label-width="100px">
                <el-form-item label="标题" prop="name">
                    <el-input v-model="newsForm.name" placeholder="请输入资讯标题"></el-input>
                </el-form-item>
                <el-form-item label="标签" prop="tagId">
                    <el-select v-model="newsForm.tagId" placeholder="请选择标签" style="width: 100%;">
                        <el-option
                            v-for="tag in tags"
                            :key="tag.id"
                            :label="tag.name"
                            :value="tag.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="封面" prop="cover">
                    <el-upload class="avatar-uploader" action="http://localhost:21090/api/personal-heath/v1.0/file/upload"
                        :show-file-list="false" :on-success="handleAvatarSuccess">
                        <img v-if="newsForm.cover" :src="newsForm.cover" style="height: 200px;width: 200px;">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <el-input type="textarea" v-model="newsForm.content" :rows="10" placeholder="请输入资讯内容"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('newsForm')">发布</el-button>
                    <el-button @click="resetForm('newsForm')">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            newsForm: {
                name: '',
                tagId: '',
                cover: '',
                content: ''
            },
            tags: [],
            rules: {
                name: [
                    { required: true, message: '请输入资讯标题', trigger: 'blur' },
                    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
                ],
                tagId: [
                    { required: true, message: '请选择标签', trigger: 'change' }
                ],
                cover: [
                    { required: true, message: '请上传封面图片', trigger: 'change' }
                ],
                content: [
                    { required: true, message: '请输入资讯内容', trigger: 'blur' },
                    { min: 10, message: '内容不能少于10个字符', trigger: 'blur' }
                ]
            }
        }
    },
    created() {
        this.loadTags();
    },
    methods: {
        goBack() {
            this.$router.push('/user');
        },
        loadTags() {
            const queryDto = {
                current: 1,
                size: 100  // 设置一个较大的值以获取所有标签
            };
            this.$axios.post('/tags/query', queryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.tags = data.data.records || data.data;  // 兼容分页和非分页返回
                } else {
                    this.$message.error('标签加载失败');
                }
            }).catch(error => {
                console.error('加载标签失败:', error);
                this.$message.error('标签加载失败');
            });
        },
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error('封面上传失败');
                return;
            }
            this.$message.success('封面上传成功');
            this.newsForm.cover = res.data;
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.$axios.post('/news/save', this.newsForm).then(response => {
                        const { data } = response;
                        if (data.code === 200) {
                            this.$message.success('发布成功');
                            this.$router.push('/news-record');
                        } else {
                            this.$message.error(data.msg || '发布失败');
                        }
                    }).catch(error => {
                        console.error('发布失败:', error);
                        this.$message.error('发布失败');
                    });
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        }
    }
}
</script>

<style scoped lang="scss">
.avatar-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 200px;
    height: 200px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.avatar-uploader:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 200px;
    line-height: 200px;
    text-align: center;
}
</style> 