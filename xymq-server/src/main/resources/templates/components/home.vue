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

    }
  },
  mounted(){
    this.initCharts();
  },
  methods:{
    initCharts(){
      var socket;
      if(window.WebSocket){
        // go on
        socket = new WebSocket("ws://localhost:8687/connect");

        socket.onmessage = function (ev){

          console.log(ev.data)
        }

      }
      let qLinechar = this.$refs.qLinechar
      let qPiechar = this.$refs.qPiechar
      let tLinechar = this.$refs.tLinechar
      let tPiechar = this.$refs.tPiechar

      var myChart = echarts.init(qLinechar);
      var myChart2 = echarts.init(qPiechar);
      var myChart3 = echarts.init(tLinechar);
      var myChart4 = echarts.init(tPiechar);

      var option = {
        title: {
          text: 'Stacked Line'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['Email', 'Union Ads', 'Video Ads', 'Direct', 'Search Engine']
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
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: 'Email',
            type: 'line',
            stack: 'Total',
            data: [120, 132, 101, 134, 90, 230, 210]
          },
          {
            name: 'Union Ads',
            type: 'line',
            stack: 'Total',
            data: [220, 182, 191, 234, 290, 330, 310]
          },
          {
            name: 'Video Ads',
            type: 'line',
            stack: 'Total',
            data: [150, 232, 201, 154, 190, 330, 410]
          },
          {
            name: 'Direct',
            type: 'line',
            stack: 'Total',
            data: [320, 332, 301, 334, 390, 330, 320]
          },
          {
            name: 'Search Engine',
            type: 'line',
            stack: 'Total',
            data: [820, 932, 901, 934, 1290, 1330, 1320]
          }
        ]
      };
      var option2 = {
        title: {
          text: 'Referer of a Website',
          subtext: 'Fake Data',
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
            name: 'Access From',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 1048, name: 'Search Engine' },
              { value: 735, name: 'Direct' },
              { value: 580, name: 'Email' },
              { value: 484, name: 'Union Ads' },
              { value: 300, name: 'Video Ads' }
            ],
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
          text: 'Stacked Line'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['Email', 'Union Ads', 'Video Ads', 'Direct', 'Search Engine']
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
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: 'Email',
            type: 'line',
            stack: 'Total',
            data: [120, 132, 101, 134, 90, 230, 210]
          },
          {
            name: 'Union Ads',
            type: 'line',
            stack: 'Total',
            data: [220, 182, 191, 234, 290, 330, 310]
          },
          {
            name: 'Video Ads',
            type: 'line',
            stack: 'Total',
            data: [150, 232, 201, 154, 190, 330, 410]
          },
          {
            name: 'Direct',
            type: 'line',
            stack: 'Total',
            data: [320, 332, 301, 334, 390, 330, 320]
          },
          {
            name: 'Search Engine',
            type: 'line',
            stack: 'Total',
            data: [820, 932, 901, 934, 1290, 1330, 1320]
          }
        ]
      };
      var option4 = {
        title: {
          text: 'Referer of a Website',
          subtext: 'Fake Data',
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
            name: 'Access From',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 1048, name: 'Search Engine' },
              { value: 735, name: 'Direct' },
              { value: 580, name: 'Email' },
              { value: 484, name: 'Union Ads' },
              { value: 300, name: 'Video Ads' }
            ],
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

      myChart.setOption(option);
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