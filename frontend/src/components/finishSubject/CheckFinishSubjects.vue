<template>
  <div>
    <!--    面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/welcome' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>结题管理</el-breadcrumb-item>
      <el-breadcrumb-item>结题申请审核</el-breadcrumb-item>
    </el-breadcrumb>
    <!--卡片视图区-->
    <el-card class="el-card">
      <!--      表格区-->
      <el-row type="flex" justify="center">
        <el-card>
          <el-table
            :data="reportlist"
            size="medium "
            :default-sort = "{prop: 'date', order: 'descending'}"
            style="width: 100%; font-size: 15px">
            <!--          课题名称-->
            <el-table-column
              :show-overflow-tooltip="true"
              prop="subName"
              label="课题名称"
              width="300">
            </el-table-column>
            <!--          申请日期-->
            <el-table-column
              sortable
              :show-overflow-tooltip="true"
              prop="poseTime"
              label="提交日期"
              width="110">
            </el-table-column>
            <!--          申请人-->
            <el-table-column
              sortable
              :show-overflow-tooltip="true"
              prop="studentName"
              label="报告人"
              width="110">
            </el-table-column>
            <!--         课题状态-->
            <el-table-column
              :show-overflow-tooltip="true"
              prop="status"
              width="120"
              :filters="[{ text: '待审核', value: 1 }, { text: '已通过', value: 2 }, { text: '未通过', value: 3 }]"
              :filter-method="filterStatus"
              filter-placement="bottom-end"
              label="报告状态">
              <template slot-scope="scope">
                <el-tag type="success" v-if=" scope.row.status == 'YTG'">已通过</el-tag>
                <el-tag type="warning" v-if=" scope.row.status == 'WSH'">待审核</el-tag>
                <el-tag type="danger" v-if=" scope.row.status == 'WTG'">未通过</el-tag>
              </template>
            </el-table-column>
            <!--          操作-->
            <el-table-column
              :show-overflow-tooltip="true"
              width="200"
              label="操作">
              <template slot-scope="scope">
                <!--              查看详细内容-->
                <el-tooltip class="item" effect="dark" content="查看课题详细内容" placement="top" :enterable="false">
                  <el-button type="primary" icon="el-icon-view" circle size="mini" @click="viewSubject(scope.row)"></el-button>
                </el-tooltip>
                <!--              查看开题阶段内容-->
                <el-tooltip class="item" effect="dark" content="查看答辩申请及毕业论文" placement="top" :enterable="false">
                  <el-button type="danger" icon="el-icon-s-flag" circle size="mini" @click="viewReport(scope.row)"></el-button>
                </el-tooltip>
                <!-- 给学生反馈信息-->
                <el-tooltip class="item" effect="dark" content="给学生反馈信息" placement="top" :enterable="false">
                  <el-button type="info" icon="el-icon-chat-line-square" circle size="mini" @click="editFeedback(scope.row)"></el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-row>
      <!--      分页区-->
      <el-row type="flex" justify="center" style="margin-top: 10px">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryInfo.page"
          :page-sizes="[5, 10, 20]"
          :page-size="queryInfo.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalPageNum">
        </el-pagination>
      </el-row>
    </el-card>
    <!--    查看课题详情对话框-->
    <el-dialog
      :visible.sync="viewPageVisible"
      width="60%">
      <el-row type="flex" justify="center" style="font-size: 20px; font-weight: bold">课题详情</el-row>
      <el-divider></el-divider>
      <el-form ref="subject" :model="currentSubjectInfo" label-width="80px">
        <el-row>
          <el-col :span="20">
            <el-form-item label="课题名称">
              <el-input v-model="currentSubjectInfo.subName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="题目类型">
              <el-tag type="success" v-if=" currentSubjectInfo.zone == 'KXTS'">科学探索与技术创新</el-tag>
              <el-tag type="success" v-if=" currentSubjectInfo.zone == 'SMGH'">生命关怀与社会认知</el-tag>
              <el-tag type="success" v-if=" currentSubjectInfo.zone == 'ZXZH'">哲学智慧与创新思维</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="申报时间">
              <el-row>
                <el-input v-model="currentSubjectInfo.poseTime"></el-input>
              </el-row>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="指导教师">
              <el-input v-model="currentSubjectInfo.teacherName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="导师电话">
              <el-input v-model="currentSubjectInfo.tel"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="20">
            <el-form-item label="课题内容">
              <div class="ql-container ql-snow" style="margin-left: 20px; margin-top: 20px">
                <div class="ql-editor" v-html="currentSubjectInfo.description"></div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="20">
            <el-form-item label="课题要求">
              <div class="ql-container ql-snow" style="margin-left: 20px; margin-top: 20px">
                <div class="ql-editor" v-html="currentSubjectInfo.requirement"></div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewPageVisible = false">退出查看</el-button>
      </span>
    </el-dialog>
    <!--    查看答辩申请及毕业论文-->
    <el-dialog
      :visible.sync="viewReportVisible"
      width="50%">
      <el-row type="flex" justify="center" style="font-size: 20px; font-weight: bold; margin-bottom: 20px">答辩申请及毕业论文</el-row>
      <el-divider>学生信息</el-divider>
      <el-row style="font-size: 15px">
        <el-col :span="3" :offset="2">申请人:</el-col>
        <el-col :span="5">{{currentReportInfo.studentName}}</el-col>
        <el-col :span="3" :offset="1">申请时间:</el-col>
        <el-col :span="8">{{currentReportInfo.poseTime}}</el-col>
      </el-row>
      <el-divider>答辩申请内容</el-divider>
      <el-row>
        <div class="ql-container ql-snow" style="margin-left: 20px; margin-top: 20px">
          <div class="ql-editor" v-html="currentReportInfo.applyReason"></div>
        </div>
      </el-row>
      <el-divider>毕业论文</el-divider>
      <el-row type="flex" justify="center">
        <Downloader :doc-id="docId"></Downloader>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewReportVisible = false">退出查看</el-button>
      </span>
    </el-dialog>
    <!--    给学生反馈信息抽屉-->
    <el-drawer
      :visible.sync="drawer"
      size="50%">
      <el-row class="drawer-bg">
      <!--      富文本编辑器输入框-->
      <el-form ref="form" :model="feedBack" label-width="80px">
      <el-row>
        <el-col style="padding: 40px">
        <el-card class="feedBackCard">
        <el-row type="flex" justify="center" class="item_label"><span class="card_header">审核毕业答辩申请</span></el-row>
        <el-row>
          <el-col :span="24" style="margin-bottom: 10px">
            <div class="ql-container ql-snow">
              <div class="notice_content ql-editor"
                   @click="useQuillEditor"
                   v-html="feedBack.comment">
              </div>
            </div>
          </el-col>
        </el-row>
        <el-col>
          <el-form-item label="审核结果">
            <el-radio-group v-model="feedBack.status">
              <el-radio label="YTG">通过审核</el-radio>
              <el-radio label="WTG">未通过</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        </el-card>
        </el-col>
      </el-row>
        <el-row type="flex" justify="center">
          <el-button type="success" @click="feedBackSubmit" icon="el-icon-upload">提交审核意见</el-button>
        </el-row>
      </el-form>
      </el-row>
    </el-drawer>
    <!--      符文本编辑器对话框-->
    <el-dialog
      title="请输入反馈信息"
      :visible.sync="quillEditorVisible"
      :before-close="resetQuillEditorContent"
      width="60%">
      <quill-editor ref="quillEditor"
                    :init-content="feedBack.comment">
      </quill-editor>
      <span slot="footer" class="dialog-footer">
        <el-row style="margin-top: 30px">
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
  name: 'checkSubjects',
  components: { quillEditor, Downloader },
  data() {
    return {
      // 查看详情的课题信息
      currentSubjectInfo: {},
      // （符合要求）课题总数
      totalPageNum: 0,
      // 获取报告列表
      queryInfo: {
        page: 1, // 当前页号
        size: 10, // 页面大小
        stage: 'JT'
      },
      reportlist: [], // 开题报告列表信息
      total: 0,
      feedBack: {
        comment: '',
        fileScore: 0,
        status: '',
        id: 0
      },
      // 查看课题详情对话框可见性
      viewPageVisible: false,
      // 查看开题详情对话框可见性
      viewReportVisible: false,
      // 反馈抽屉可见性
      drawer: false,
      // 富文本编辑器可见性
      quillEditorVisible: false,
      docId: '',
      currentReportInfo: ''
    }
  },
  created() {
    this.getReportList()
  },
  methods: {
    async getReportList() {
      const { data: res } = await this.$http.get('http://127.0.0.1:9528/report', { params: this.queryInfo })
      if (res.meta.code !== 200) {
        this.$message.error('获取课题列表失败')
      } else {
        this.totalPageNum = res.data.total
        this.reportlist = res.data.records // 与后端对接
      }
    },
    async getSubjectInfo(row) {
      const { data: res } = await this.$http.get('http://127.0.0.1:9528/subject/teacher/my', { params: { subId: row.subId } })
      if (res.meta.code !== 200) {
        this.$message.error('获取课题列表失败')
      } else {
        this.currentSubjectInfo = res.data[0] // 与后端对接
      }
    },
    // 提交表单
    async feedBackSubmit() {
      this.drawer = false
      const { data: res } = await this.$http.put('http://127.0.0.1:9528/report', this.feedBack)
      if (res.meta.code !== 200) this.$message.error('提交反馈信息失败！')
      else {
        this.$message.success('提交反馈信息成功！')
        await this.getReportList()
      }
    },
    // 当页面大小变化时触发
    handleSizeChange(newSize) {
      this.queryInfo.size = newSize
      this.getSubjectInfo()
    },
    // 当页面编号变化时触发
    handleCurrentChange(newPage) {
      this.queryInfo.page = newPage
      this.getSubjectInfo()
    },
    // 筛选课题类型
    filterType(value, row) {
      return row.type === value
    },
    // 筛选报告状态
    filterStatus(value, row) {
      return row.status === value
    },
    // 查看课题详情
    viewSubject(row) {
      this.getSubjectInfo(row)
      this.viewPageVisible = true
      this.currentSubjectInfo = row
    },
    // 查看学生开题报告详情
    viewReport(row) {
      this.viewReportVisible = true
      this.currentReportInfo = row
      this.docId = row.docId
    },
    // 编辑反馈表单
    editFeedback(row) {
      this.feedBack.id = row.id
      this.drawer = true
    },
    // 调用富文本编辑器
    useQuillEditor() {
      this.quillEditorVisible = true
    },
    // 重置富文本编辑框
    resetQuillEditorContent() {
      this.$refs.quillEditor.reset()
      this.feedBack.comment = '请输入反馈信息'
      this.quillEditorVisible = false
    },
    // 提交（采用）富文本编辑器框
    submitQuillEditorContent() {
      this.feedBack.comment = this.$refs.quillEditor.returnContent()
      this.quillEditorVisible = false
    }
  }
}
</script>

<style Lang="less" scoped>
.el-card{
  box-shadow: 0 1px 1px rgba(0,0,0, 0.15) !important
}
/*表格状态颜色*/
.el-table .warning-row {
  background: oldlace;
}
/*表格状态颜色*/
.el-table .success-row {
  background: #f0f9eb;
}
.drawer-bg{
  height: 90%;
  border-radius: 24px;
  background: #f0f0f0;
  box-shadow:  20px 20px 32px #959595,
    -20px -20px 32px #ffffff;
}
.feedBackCard{
  display: flex;
  justify-content: center;
}
.card_header{
  margin-bottom: 10px;
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  align-items: center;
}
</style>
