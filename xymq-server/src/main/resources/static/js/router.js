// 2.定义路由，每个路由有两部分，path(路径)，component(组件)
const routes = [
    { path: "/", component : () => loadModule('./component/home.vue',options),name:'home'  },
    { path: "/news", component : () => loadModule('./component/queue.vue',options),name:'news' }
];

//vue3-sfc-loader v0.7.3
const options = {
    moduleCache: {
        vue: Vue
    },
    async getFile(url) {
        const res = await fetch(url);
        if ( !res.ok )
            throw Object.assign(new Error(res.statusText + ' ' + url), { res });
        return await res.text();
    },
    addStyle(textContent) {
        const style = Object.assign(document.createElement('style'), { textContent });
        const ref = document.head.getElementsByTagName('style')[0] || null;
        document.head.insertBefore(style, ref);
    },
}
const { loadModule } = window['vue2-sfc-loader'];
var router = new VueRouter({
    // html5模式 去掉锚点
    routes:routes
})
