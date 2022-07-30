// 1.定义路由所需的组件
const home = { template: "<div>首页 纵览天下大事</div>" };
const news = { template: "<div>新闻 用事实说话</div>" };

// 2.定义路由，每个路由有两部分，path(路径)，component(组件)
const routes = [
    { path: "/home", component: home },
    { path: "/news", component : () => loadModule('./component/queue.vue',options) }
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
    routes:routes
})
