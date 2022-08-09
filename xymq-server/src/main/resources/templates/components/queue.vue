<template>
  <div style="margin-top: 20px">
    <el-table
        :data="tableData"
        style="width: 100%">
      <el-table-column
          fixed
          prop="date"
          label="序号"
          width="50">
      </el-table-column>
      <el-table-column
          prop="name"
          label="队列名">
      </el-table-column>
      <el-table-column
          prop="province"
          label="消费者数量"
          width="200">
      </el-table-column>
      <el-table-column
          prop="city"
          label="队列类型"
          width="120">
      </el-table-column>
      <el-table-column
          prop="address"
          label="消息数"
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
      tableData: [{
        date: '1',
        name: '王小虎',
        province: '上海',
        city: '普陀区',
        address: '上海市普陀区金沙江路 1518 弄',
        zip: 200333
      }, {
        date: '1',
        name: '王小虎',
        province: '上海',
        city: '普陀区',
        address: '上海市普陀区金沙江路 1517 弄',
        zip: 200333
      }, {
        date: '1',
        name: '王小虎',
        province: '上海',
        city: '普陀区',
        address: '上海市普陀区金沙江路 1519 弄',
        zip: 200333
      }, {
        date: '1',
        name: '王小虎',
        province: '上海',
        city: '普陀区',
        address: '上海市普陀区金沙江路 1516 弄',
        zip: 200333
      }]
    }
  },
  created() {

  },
  methods: {
    handleClick(row) {
      console.log(row);
    }
  },

}
</script>

<style>

</style>
