<template>
  <div>
  <!--    面包屑导航区域-->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/welcome' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>开题管理</el-breadcrumb-item>
    <el-breadcrumb-item>开题报告</el-breadcrumb-item>
  </el-breadcrumb>
      <el-card class="bg-top">
        <div class="card_header">我的课题基本信息</div>
        <!--    课题详情卡片-->
        <el-row type="flex" justify="center">
          <el-card class="subject_detail">
            <!--        课题名称-->
            <el-row>
              课题名称：<a-tag color="blue" style="font-size: 15px">{{currentSubjectInfo.subName}}</a-tag>
            </el-row>
            <el-divider></el-divider>
            <!--        课题领域、所属专业、指导老师-->
            <el-row>
              <!--          指导老师-->
              <el-col :span="8">指导老师：
                <a-tag color="cyan">
                  <el-link :href="currentSubjectInfo.teacherHomePage" target="_blank">
                    {{currentSubjectInfo.teacherName+' '}}<i class="el-icon-view"></i>
                  </el-link>
                </a-tag>
              </el-col>
              <!--          课题领域-->
              <el-col :span="8">课题领域：
                <a-tag color="orange" v-if=" currentSubjectInfo.zone == 'KXTS'">科学探索与技术创新</a-tag>
                <a-tag color="orange" v-if=" currentSubjectInfo.zone == 'SMGH'">生命关怀与社会认知</a-tag>
                <a-tag color="orange" v-if=" currentSubjectInfo.zone == 'ZXZH'">哲学智慧与创新思维</a-tag>
              </el-col>
            </el-row>
            <el-divider></el-divider>
            <!--          课题要求-->
            <el-row type="flex" align="center">
              <el-col :span="3" class="item_label">课题要求：</el-col>
              <el-col :span="21">
                <div class="ql-container ql-snow">
                  <div class="ql-editor" v-html="currentSubjectInfo.requirement"></div>
                </div>
              </el-col>
            </el-row>
            <el-divider></el-divider>
            <!--          课题描述-->
            <el-row type="flex" align="center">
              <el-col :span="3" class="item_label">课题内容：</el-col>
              <el-col :span="21">
                <div class="ql-container ql-snow">
                  <div class="ql-editor" v-html="currentSubjectInfo.description"></div>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-row>
        <!--      填写开题报告，上传附件区-->
        <div class="card_header">填写开题报告基本信息</div>
        <el-row type="flex" justify="center">
        <el-card class="subject_detail">
        <el-form ref="form" :model="report" label-width="80px">
          <el-row>
            <el-col :span="20">
              <el-col :span="3" class="item_label"><span>研究意义</span></el-col>
              <el-col :span="21" style="margin-bottom: 10px">
                <div class="ql-container ql-snow">
                  <div class="notice_content ql-editor"
                       @click="useQuillEditor1"
                       v-html="report.meaning">
                  </div>
                </div>
              </el-col>
            </el-col>
            <el-col :span="20">
              <el-col :span="3" class="item_label"><span>调研结果</span></el-col>
              <el-col :span="21" style="margin-bottom: 10px">
                <div class="ql-container ql-snow">
                  <div class="notice_content ql-editor"
                       @click="useQuillEditor2"
                       v-html="report.result">
                  </div>
                </div>
              </el-col>
            </el-col>
            <el-col :span="20">
              <el-col :span="3" class="item_label"><span>研究计划</span></el-col>
              <el-col :span="21" style="margin-bottom: 10px">
                <div class="ql-container ql-snow">
                  <div class="notice_content ql-editor"
                       @click="useQuillEditor3"
                       v-html="report.plan">
                  </div>
                </div>
              </el-col>
            </el-col>
          </el-row>
          <el-form-item>
            <el-row style="padding-left: 130px">
              <el-popconfirm
                @confirm="reportSubmit"
                title="确认提交开题报告吗?">
                <el-button slot="reference" type="primary">立即提交</el-button>
              </el-popconfirm>
              <el-popconfirm
                @confirm="uploaderVisible=true"
                title="上传附件前请确认已提交表单！">
                <el-button slot="reference" type="success" style="margin-left: 5px">上传附件</el-button>
              </el-popconfirm>
            </el-row>
          </el-form-item>
        </el-form>
        </el-card>
        </el-row>
      </el-card>
    <!--        文件上传-->
    <el-dialog
      title="请输入研究意义"
      :visible.sync="quillEditor1Visible"
      :before-close="resetQuillEditor1Content"
      width="60%">
      <quill-editor ref="quillEditor1"
                    :init-content="report.meaning">
      </quill-editor>
      <span slot="footer" class="dialog-footer">
        <el-row style="margin-top: 30px">
          <el-button @click="resetQuillEditor1Content">清 空</el-button>
          <el-button type="primary" @click="submitQuillEditor1Content">确 定</el-button>
        </el-row>
        </span>
    </el-dialog>
<!--       调研结果 -->
    <el-dialog
      title="请输入调研结果"
      :visible.sync="quillEditor2Visible"
      :before-close="resetQuillEditor2Content"
      width="60%">
      <quill-editor ref="quillEditor2"
                    :init-content="report.result">
      </quill-editor>
      <span slot="footer" class="dialog-footer">
        <el-row style="margin-top: 30px">
          <el-button @click="resetQuillEditor2Content">清 空</el-button>
          <el-button type="primary" @click="submitQuillEditor2Content">确 定</el-button>
        </el-row>
        </span>
    </el-dialog>
<!--       研究计划 -->
    <el-dialog
      title="请输入研究计划"
      :visible.sync="quillEditor3Visible"
      :before-close="resetQuillEditor3Content"
      width="60%">
      <quill-editor ref="quillEditor3"
                    :init-content="report.plan">
      </quill-editor>
      <span slot="footer" class="dialog-footer">
        <el-row style="margin-top: 30px">
          <el-button @click="resetQuillEditor3Content">清 空</el-button>
          <el-button type="primary" @click="submitQuillEditor3Content">确 定</el-button>
        </el-row>
      </span>
    </el-dialog>
<!--       上传附件对话框-->
    <el-dialog
      title="上传附件"
      :visible.sync="uploaderVisible"
      width="30%">
      <uploader :doc-id="docId"></uploader>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" plain @click="uploaderVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import quillEditor from '@/plugins/quill-editor/VueQuillEditor'
import Uploader from '@/plugins/upload-download/Uploader'
export default {
  name: 'StartReport',
  components: { quillEditor, Uploader },
  data() {
    return {
      docId: null,
      uploaderVisible: false,
      subjectDocId: '',
      reportDocId: '',
      // 查看学生的课题信息
      // 当前选中的课题信息
      currentSubjectInfo: {
        subId: '',
        subName: '',
        zone: '', // 课题领域
        description: '',
        majorName: '',
        requirement: '',
        teacherTitle: '',
        teacherName: '',
        teacherHomePage: ''
      },
      report: {
        subId: '',
        meaning: '',
        result: '',
        plan: '',
        stage: 'KT'
      },
      // 富文本编辑器可见性
      quillEditor1Visible: false,
      quillEditor2Visible: false,
      quillEditor3Visible: false
    }
  },
  created() {
    this.getCurrentSubjectInfo()
  },
  methods: {
    // 获取课题信息
    async getCurrentSubjectInfo() {
      const { data: res } = await this.$http.get('http://127.0.0.1:9528/subject/student/my')
      if (res.meta.code !== 200) {
        return this.$message.error('获取课题列表失败')
      }
      this.currentSubjectInfo = res.data
      this.report.subId = res.data.subId
    },
    // 提交表单
    async reportSubmit() {
      const { data: res } = await this.$http.post('http://127.0.0.1:9528/report', this.report)
      if (res.meta.code !== 200) {
        this.$message.error('开题报告提交失败！')
      } else {
        this.docId = res.data
        this.$message.success('开题报告提交成功！')
      }
    },
    // 调用富文本编辑器
    useQuillEditor1() {
      this.quillEditor1Visible = true
    },
    useQuillEditor2() {
      this.quillEditor2Visible = true
    },
    useQuillEditor3() {
      this.quillEditor3Visible = true
    },
    // 重置富文本编辑框
    resetQuillEditor1Content() {
      this.$refs.quillEditor.reset()
      this.report.meaning = '请输入研究意义'
      this.quillEditor1Visible = false
    },
    resetQuillEditor2Content() {
      this.$refs.quillEditor.reset()
      this.report.research = '请输入研究结果'
      this.quillEditor2Visible = false
    },
    resetQuillEditor3Content() {
      this.$refs.quillEditor.reset()
      this.report.myPlan = '请输入研究计划'
      this.quillEditor3Visible = false
    },
    // 提交（采用）富文本编辑器框
    submitQuillEditor1Content() {
      this.report.meaning = this.$refs.quillEditor1.returnContent()
      this.quillEditor1Visible = false
    },
    submitQuillEditor2Content() {
      this.report.result = this.$refs.quillEditor2.returnContent()
      this.quillEditor2Visible = false
    },
    submitQuillEditor3Content() {
      this.report.plan = this.$refs.quillEditor3.returnContent()
      this.quillEditor3Visible = false
    }
  }
}
</script>

<style Lang="less" scoped>
.el-card{
  margin-bottom: 20px;
  margin-right: 5px;
}
.bg-top{
  background-color: #ececec;
}
.bg-bottom{
  background-color: #def3fa;
}
.card_header{
  margin-bottom: 10px;
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  align-items: center;
}
/*分割线*/
.el-divider{
  margin: 6px;
}
/*课题详情卡片*/
.subject_detail{
  width: 80%;
  padding-left: 50px;
  font-size: 16px;
  /*边框阴影*/
  border-radius: 4px;
  background: #ffffff;
  box-shadow:  28px 28px 56px #bababa,
    -28px -28px 56px #ffffff;
}
/*表单标签*/
.item_label{
  display: flex;
  align-items: center;
}
</style>
