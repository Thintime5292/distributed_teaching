layui.use(['element', 'upload','form'], function () {
    var upload = layui.upload;
    var $ = layui.jquery;
    var layer = layui.layer;
    var form = layui.form;
    form.render();
    upload.render({
        elem: '#uploadIcon' //绑定元素
        , url: 'http://www.teaching.com:9986/file/upload' //上传接口
        , done: function (res) {
            //上传完毕回调
            // console.log(res)
            if (res.code > 0) {
                layer.msg(res.msg);
            } else {
                $("#iconPre").attr("src", res.url);
                $("#iconImg").val(res.url);
            }

        }
        , error: function () {
            //请求异常回调
        }
    });
    form.on('submit(userInfo)', function(data){
        $.ajax({
            url: "http://www.teaching.com:9986/user/save",
            type:'post',
            data:data.field,
            success:function (res) {
                console.log(res);
                if(res.code > 0){
                    layer.msg('更新失败！',{icon:2});
                } else {
                    layer.msg('更新成功！',{icon:1});
                    setTimeout(function () {
                        window.parent.location.reload();
                    },1000);
                }
            }
        });
        return false;
    });
    $(".cancerBtn").on("click",function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });
});