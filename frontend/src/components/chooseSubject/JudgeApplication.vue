<template>
  <div>
    <!--    面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/welcome' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>选题管理</el-breadcrumb-item>
      <el-breadcrumb-item>教师审核</el-breadcrumb-item>
    </el-breadcrumb>
    <!--    搜索框、表格、分页-->
    <el-row type="flex" justify="center">
      <el-card class="outer_detail">
        <!--        表格区域-->
        <el-row class="table_zone" type="flex" justify="center">
          <el-card>
            <el-table
              size="small"
              :highlight-current-row="true"
              :data="allApplicationInfo"
              :default-sort = "{prop: 'date', order: 'descending'}"
              style="width: 100%; font-size: 15px">
              <!--          标题-->
              <el-table-column
                :show-overflow-tooltip="true"
                prop="subName"
                label="课题名称"
                width="200">
              </el-table-column>
              <!--          申请日期-->
              <el-table-column
                sortable
                :show-overflow-tooltip="true"
                prop="applyTime"
                label="申请日期"
                width="110">
              </el-table-column>
              <!--          审核日期-->
              <el-table-column
                sortable
                :show-overflow-tooltip="true"
                prop="auditTime"
                label="审核日期"
                width="110">
              </el-table-column>
              <!--          申请人-->
              <el-table-column
                :show-overflow-tooltip="true"
                prop="studentName"
                label="申请人"
                width="110">
              </el-table-column>
              <!--          审核人-->
              <el-table-column
                :show-overflow-tooltip="true"
                prop="teacherName"
                label="审核人"
                width="110">
              </el-table-column>
              <!--          审核状态-->
              <el-table-column
                :show-overflow-tooltip="true"
                prop="status"
                label="审核状态"
                width="100"
                :filters="[{ text: '待审核', value: 'WSH' }, { text: '已通过', value: 'YTG' }, { text: '未通过', value: 'WTG' }]"
                :filter-method="filterStatus"
                filter-placement="bottom-end">
                <template slot-scope="scope">
                  <a-tag v-if="scope.row.status=='WSH'" style="font-size: 15px" color="cyan">未审核</a-tag>
                  <a-tag v-if="scope.row.status=='YTG'" style="font-size: 15px" color="green">已通过</a-tag>
                  <a-tag v-if="scope.row.status=='WTG'" style="font-size: 15px" color="red">未通过</a-tag>
                </template>
              </el-table-column>
              <!--              开始审核-->
              <el-table-column
                :show-overflow-tooltip="true"
                width="110"
                label="开始审核">
                <template slot-scope="scope">
                  <a-tag color="#87d068" @click="beginJudge(scope.row)" style="font-size: 15px">开始审核</a-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-row>
        <!--         分页区-->
        <el-row type="flex" justify="center" style="margin-top: 20px">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryInfo.page"
            :page-sizes="[5, 10, 20, 50]"
            :page-size="queryInfo.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalRecordNum">
          </el-pagination>
        </el-row>
      </el-card>
    </el-row>
    <!--    查看详情对话框-->
    <el-dialog
      :visible.sync="beginJudgePageVisible"
      width="60%"
      center>
      <el-row style="font-size: 20px; font-weight: bold" type="flex" justify="center">审核学生选题申请</el-row>
      <el-divider></el-divider>
      <el-card style="margin: 30px">
        <!--        课题名称-->
        <el-row>
          课题名称：<a-tag color="blue" style="font-size: 15px">{{currentApplicationInfo.subName}}</a-tag>
        </el-row>
        <div class="divider"></div>
        <!--        出题老师、申请学生-->
        <el-row :gutter="20">
          <!--          课题领域-->
          <el-col :span="12">出题老师：<a-tag color="#2db7f5" style="font-size: 15px">{{currentApplicationInfo.teacherName}}</a-tag></el-col>
          <!--          所属专业-->
          <el-col :span="12">申请学生：<a-tag color="#87d068" style="font-size: 15px">{{currentApplicationInfo.studentName}}</a-tag></el-col>
        </el-row>
        <div class="divider"></div>
        <!--          申请理由-->
        <el-row type="flex" align="center">
          <el-col :span="3" class="item_label">申请理由：</el-col>
          <el-col :span="21">
            <div class="ql-container ql-snow">
              <div class="ql-editor" v-html="currentApplicationInfo.applyReason" style="font-size: 15px"></div>
            </div>
          </el-col>
        </el-row>
        <div class="divider"></div>
        <!--        相关附件-->
        <el-row type="flex" align="center">
          <el-col :span="3" class="item_label"><span>相关附件：</span></el-col>
          <el-col :span="21">
            <el-row type="flex" justify="center">
              <Downloader :doc-id="docId"></Downloader>
            </el-row>
          </el-col>
        </el-row>
        <div class="divider"></div>
        <!--          审核意见-->
        <el-row type="flex" align="center" style="height: auto">
          <el-col :span="3" class="item_label"><span>审核意见：</span></el-col>
          <el-col :span="21">
            <div class="ql-container ql-snow">
              <div class="ql-editor"
                   style="font-size: 15px"
                   @click="useQuillEditor"
                   v-html="currentApplicationInfo.feedback">
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <span slot="footer" class="dialog-footer">
        <el-button type="info" @click="beginJudgePageVisible = false">暂不审核</el-button>
        <el-button type="success" @click="passApplication">通过申请</el-button>
        <el-button type="danger" @click="rollbackApplication">驳回申请</el-button>
      </span>
    </el-dialog>
    <!--      符文本编辑器对话框-->
    <el-dialog
      title="请输入审核意见"
      :visible.sync="quillEditorVisible"
      :before-close="resetQuillEditorContent"
      width="60%">
      <quill-editor ref="quillEditor"
                    :init-content="currentApplicationInfo.feedback">
      </quill-editor>
      <span slot="footer" class="dialog-footer">
        <el-row style="margin-top: 40px">
          <el-button @click="resetQuillEditorContent">清 空</el-button>
          <el-button type="primary" @click="submitQuillEditorContent">确 定</el-button>
        </el-row>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import quillEditor from '@/plugins/quill-editor/VueQuillEditor'
import Downloader from '@/plugins/upload-download/Downloader'
export default {
  name: 'JudgeApplication',
  components: { quillEditor, Downloader },
  mounted() {
    this.getApplicationRecords()
  },
  data() {
    return {
      docId: '',
      // 查询参数
      queryInfo: {
        keyWord: '', // 关键词
        page: 1, // 当前页号
        size: 10 // 页面大小
      },
      // 符合条件的记录数
      totalRecordNum: 0,
      // 申请记录
      allApplicationInfo: [],
      // 当前选中的申请记录详情
      currentApplicationInfo: {},
      // 查看申请记录详情对话框可见性
      beginJudgePageVisible: false,
      // 富文本编辑器可见性
      quillEditorVisible: false
    }
  },
  methods: {
    // 获取所有申请记录信息
    async getApplicationRecords() {
      const { data: res } = await this.$http.get('http://127.0.0.1:9528/subject/apply', { params: this.queryInfo })
      if (res.meta.code !== 200) {
        this.$message.error('拉取选题申请记录失败！')
      } else {
        this.allApplicationInfo = res.data.records
        this.totalRecordNum = res.data.total
      }
    },
    // 更新申请记录信息
    async updateApplicationRecords() {
      const { data: res } = await this.$http.put('http://127.0.0.1:9528/subject/apply', this.currentApplicationInfo)
      if (res.meta.code !== 200) {
        this.$message.error(res.meta.message)
      } else {
        this.$message.success(res.meta.message)
      }
    },
    // 当页面大小变化时触发
    handleSizeChange(newSize) {
      this.queryInfo.size = newSize
      this.getApplicationRecords()
    },
    // 当页面编号变化时触发
    handleCurrentChange(newPage) {
      this.queryInfo.page = newPage
      this.getApplicationRecords()
    },
    // 筛选申请记录状态
    filterStatus(value, row) {
      return row.status === value
    },
    // 查看申请记录详情
    beginJudge(row) {
      this.docId = row.docId
      this.beginJudgePageVisible = true
      this.currentApplicationInfo = row
    },
    // 调用富文本编辑器
    useQuillEditor() {
      this.quillEditorVisible = true
    },
    // 重置富文本编辑框
    resetQuillEditorContent() {
      this.$refs.quillEditor.reset()
      this.currentApplicationInfo.feedback = '请输入审核意见'
      this.quillEditorVisible = false
    },
    // 提交（采用）富文本编辑器框
    submitQuillEditorContent() {
      this.currentApplicationInfo.feedback = this.$refs.quillEditor.returnContent()
      this.quillEditorVisible = false
    },
    // 通过申请
    async passApplication() {
      this.currentApplicationInfo.status = 'YTG'
      await this.updateApplicationRecords()
      await this.getApplicationRecords()
      this.beginJudgePageVisible = false
    },
    // 驳回申请
    async rollbackApplication() {
      this.currentApplicationInfo.status = 'WTG'
      await this.updateApplicationRecords()
      await this.getApplicationRecords()
      this.beginJudgePageVisible = false
    }
  }
}
</script>

<style Lang="less" scoped>
/*最外层卡片*/
.outer_detail{
  width: 100%;
  border-radius: 4px;
  background: #ffffff;
  box-shadow:  28px 28px 56px #bababa,
    -28px -28px 56px #ffffff;
}
/*分割线*/
.el-divider{
  margin: 10px;
}
/*表单标签*/
.item_label{
  display: flex;
  align-items: center;
}
/*自定义分割线*/
.divider{
  height: 8px;
}
</style>
