<template>
    <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
        <el-row style="margin: 0 20px;">
            <el-table row-key="id" @selection-change="handleSelectionChange" :data="tableData" style="width: 100%">
                <el-table-column prop="cover" width="80" label="首图">
                    <template slot-scope="scope">
                        <img :src="scope.row.cover" style="width: 48px;height: 34px;border-radius: 5px;" />
                    </template>
                </el-table-column>
                <el-table-column prop="tagName" width="138" label="所属分类">
                    <template slot-scope="scope">
                        <span><i class="el-icon-discount" style="margin-right: 3px;"></i>
                            {{ scope.row.tagName }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" width="168" label="发布时间"></el-table-column>
                <el-table-column prop="name" label="标题"></el-table-column>
                <el-table-column prop="viewsNumber" width="100" label="浏览量">
                    <template slot-scope="scope">
                        <span><i class="el-icon-view" style="margin-right: 3px;"></i>
                            {{ scope.row.viewsNumber || 0 }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                    <template slot-scope="scope">
                        <span class="text-button" @click="handleDelete(scope.row)">删除</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="currentPage" :page-sizes="[10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="totalItems"></el-pagination>
        </el-row>
    </el-row>
</template>

<script>
export default {
    data() {
        return {
            currentPage: 1,
            pageSize: 10,
            totalItems: 0,
            tableData: [],
            selectedRows: []
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        handleSelectionChange(selection) {
            this.selectedRows = selection;
        },
        async handleDelete(row) {
            const confirmed = await this.$swalConfirm({
                title: '删除帖子',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    const response = await this.$axios.post(`/news/deleteMyNews`, [row.id]);
                    if (response.data.code === 200) {
                        this.$message.success('删除成功');
                        this.fetchFreshData();
                    } else {
                        this.$message.error(response.data.message || '删除失败');
                    }
                } catch (error) {
                    this.$message.error('删除失败');
                }
            }
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchFreshData();
        },
        fetchFreshData() {
            const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
            const params = {
                current: this.currentPage,
                size: this.pageSize,
                userId: userInfo.id
            };
            this.$axios.post(`/news/queryMyNews`, params).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.tableData = data.data;
                    this.totalItems = data.total || 0;
                } else {
                    this.$message.error(data.message || '查询失败');
                }
            }).catch(error => {
                console.error('查询我的帖子失败:', error);
                this.$message.error('查询失败');
            });
        }
    }
};
</script>

<style scoped>
.text-button {
    cursor: pointer;
    color: #15559a;
    margin-right: 10px;
}
</style> 