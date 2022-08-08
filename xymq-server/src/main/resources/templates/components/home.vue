<template>
  <div class="container">
    <div class="box-queue">
      <div id="queue-linechar" ref="qLinechar"></div>
      <div id="queue-piechar" ref="qPiechar"></div>
    </div>
    <div class="box-topic">
      <div id="topic-linechar" ref="tLinechar"></div>
      <div id="topic-piechar" ref="tPiechar"></div>
    </div>
  </div>
</template>

<script>

export default {
  name: "home",
  data(){
    return {
      // 队列消费成功
      successQueueCount:[],
      // 队列堆积情况
      accQueueCount:[],
      // 消息总数
      queueTotal:[],
      // 时间列表
      time:[]
    }
  },
  mounted(){
    this.initWebsocket();
    this.initCharts();
  },
  methods:{
    initWebsocket(){
      var socket;
      var _this = this;
      if(window.WebSocket){
        // go on
        socket = new WebSocket("ws://localhost:8687/connect");

        socket.onmessage = function (ev){
          var res = JSON.parse(ev.data)
          _this.setCharts(res)
        }
      }
    },
    initCharts(){
      axios.get("/xy/data/queue").then(Response=>{
        this.setCharts(Response.data)
      })
    },
    setCharts(data){
      let qLinechar = this.$refs.qLinechar
      let qPiechar = this.$refs.qPiechar
      let tLinechar = this.$refs.tLinechar
      let tPiechar = this.$refs.tPiechar

      var myChart = echarts.init(qLinechar);
      var myChart2 = echarts.init(qPiechar);
      var myChart3 = echarts.init(tLinechar);
      var myChart4 = echarts.init(tPiechar);

      var queueAccDetail = [];
      Object.keys(data.queueAccDetail).forEach(function (item){
        queueAccDetail.push({name:item,value:data.queueAccDetail[item]})
      })
      var topicAccDetail = [];
      Object.keys(data.topicAccDetail).forEach(function (item){
        topicAccDetail.push({name:item,value:data.topicAccDetail[item]})
      })

      var option = {
        title: {
          text: '队列容器'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['消息堆积','消费成功','消息总数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.time
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '消息堆积',
            type: 'line',
            stack: '消息堆积',
            data: data.accQueueCount
          },
          {
            name: '消费成功',
            type: 'line',
            stack: '消费成功',
            data: data.successQueueCount
          },
          {
            name: '消息总数',
            type: 'line',
            stack: '消息总数',
            data: data.queueTotal
          }
        ]
      };
      var option2 = {
        title: {
          text: '堆积详情',
          // subtext: 'Fake Data',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '暂无数据',
            type: 'pie',
            radius: '50%',
            data: queueAccDetail,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      var option3 = {
        title: {
          text: '主题容器'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['离线消息', '推送失败', '推送成功', '消息总数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.time
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '离线消息',
            type: 'line',
            stack: '离线消息',
            data: data.offLineCount
          },
          {
            name: '推送失败',
            type: 'line',
            stack: '推送失败',
            data: data.accTopicCount
          },
          {
            name: '推送成功',
            type: 'line',
            stack: '推送成功',
            data: data.successTopicCount
          },
          {
            name: '消息总数',
            type: 'line',
            stack: '消息总数',
            data: data.topicTotal
          }
        ]
      };
      var option4 = {
        title: {
          text: '堆积详情',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '暂无数据',
            type: 'pie',
            radius: '50%',
            data: topicAccDetail,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };

      myChart.setOption(option,true);
      myChart2.setOption(option2)
      myChart3.setOption(option3);
      myChart4.setOption(option4)
    }
  }
}
</script>

<style scoped>
.container{
  height: 100%;
  background-color: rgb(243, 244, 250);
}
.box-queue{
  display: flex;
  align-items: center;
  height: 49%;
  width: 100%;
}
.box-topic{
  height: 49%;
  width: 100%;
  display: flex;
  align-items: center;
}
#queue-linechar{
  border-radius: 15px;
  display: inline;
  margin: 20px;
  background-color: white;
  width: 60%;
  height: 95%;
}
#queue-piechar{
  border-radius: 15px;
  display: inline;
  margin: 20px;
  background-color: white;
  width: 35%;
  height: 95%;
}
#topic-linechar{
  border-radius: 15px;
  display: inline;
  margin: 20px;
  background-color: white;
  width: 60%;
  height: 95%;
}
#topic-piechar{
  border-radius: 15px;
  display: inline;
  margin: 20px;
  background-color: white;
  width: 35%;
  height: 95%;
}
</style>