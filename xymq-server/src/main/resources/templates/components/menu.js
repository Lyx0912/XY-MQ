var menu = Vue.extend({
    props:{
        index:['index']
    },
    template: '    <el-container style="height: 100%;height: 100%">\n' +
        '        <el-aside width="14%" height="100%">\n' +
        '            <el-menu :default-active="index"\n' +
        '                     class="el-menu-container"\n' +
        '                     @open="handleOpen"\n' +
        '                     @close="handleClose"\n' +
        '                     background-color="#304156"\n' +
        '                     text-color="#fff"\n' +
        '                     active-text-color="#1890ff">\n' +
        '                <div class="aside-header"></div>\n' +
        '                <el-menu-item index="0">\n' +
        '                    <i class="el-icon-document"></i>\n' +
        '                    <span slot="title">容器概览</span>\n' +
        '                </el-menu-item>\n' +
        '                <el-menu-item index="1">\n' +
        '                    <i class="el-icon-document"></i>\n' +
        '                    <span slot="title">队列详情</span>\n' +
        '                </el-menu-item>\n' +
        '                <el-menu-item index="2">\n' +
        '                    <i class="el-icon-setting"></i>\n' +
        '                    <span slot="title">主题详情</span>\n' +
        '                </el-menu-item>\n' +
        '                <el-menu-item index="3">\n' +
        '                    <i class="el-icon-setting"></i>\n' +
        '                    <span slot="title">使用Demo</span>\n' +
        '                </el-menu-item>\n' +
        '            </el-menu>\n' +
        '        </el-aside>\n' +
        '        <el-container>\n' +
        '            <el-header>\n' +
        '                <a style="float: right">\n' +
        '                    <el-image style="width: 40px; height: 40px" alt="源码地址" src="../static/img/github.svg" :fit="fit"></el-image>\n' +
        '                </a>\n' +
        '            </el-header>\n' +
        '            \t\t\t    \t<!-- 内容区 -->\n' +
        '\t\t\t    \t<el-main>\n' +
        '\t\t\t\t\t    <router-view v-slot="{ Component }" >\n' +
        '\t\t\t\t\t        <keep-alive :include="cacheComponents" :max="200"><!-- :max 缓存组件最大数量 -->\n' +
        '\t\t\t\t\t        \t<component :is="Component" :key="$store.getters.cacheKey" v-if="isRouterAlive"/>\n' +
        '\t\t\t\t\t        \t\n' +
        '\t\t\t\t\t        </keep-alive>\n' +
        '\t\t\t\t\t    </router-view>\n' +
        '\t\t\t    \t</el-main>\n' +
        '        </el-container>\n' +
        '    </el-container>'
});