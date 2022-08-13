<template>
  <div style="margin-top: 20px">
    <el-table
        :data="queueList"
        style="width: 100%">
      <el-table-column
          prop="queueName"
          label="队列名">
      </el-table-column>
      <el-table-column
          prop="consumerCount"
          label="消费者数量"
          width="200">
      </el-table-column>
      <el-table-column
          prop="delayCount"
          label="延时消息"
          width="100">
      </el-table-column>
      <el-table-column
          prop="unConsume"
          label="待消费"
          width="100">
      </el-table-column>
      <el-table-column
          label="操作"
          width="240">
        <template slot-scope="scope">
          <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>
          <el-button type="text" size="small">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
// 导入组件
export default {
  name: "index",
  data(){
    return{
      queueList:[]
    }
  },
  created() {
    this.getList();
  },
  methods: {
    getList(){
      axios.get("/xy/data/list").then(Response=>{
        this.queueList = Response.data
      })
    }
  },

}
</script>

<style>

</style>
