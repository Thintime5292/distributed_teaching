layui.use(['element','layer'], function () {
    let element = layui.element;
    let layer = layui.layer;
    let $ = layui.jquery;
    $(".my-info").on("click",function () {
        layer.open({
            type: 2,
            title: "我的资料",
            area: ['680px', '510px'],
            content: 'http://www.teaching.com:9986/user/info'
        });
    });
    $(".logout").on("click",function () {
        layer.confirm("确认退出登录？",{
            title:'退出登录',
            btn:['确认','取消'],
        },function () {
            $.post("http://www.teaching.com:9986/user/logout",{},function (res) {
                layer.msg(res.msg);
                if (res.code == 0){
                    setTimeout(function () {
                        window.location.reload();
                    },1000);
                }
            });
        });
        $(this).parent().removeAttr("class");

    });
});