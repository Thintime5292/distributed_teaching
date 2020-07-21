layui.use(['layer'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    $(".body-login").css("height",$(window).height());
    $('#username').focus();
    $('input').keydown(function (e) {
        if (e.keyCode == 13) {
            dologin();
        }
    });
    $(window).resize(function () {
        let winHeight = $(this).height();
        $(".body-login").css("height",winHeight);
    });
});

//登录
function dologin() {

    var username = $.trim($('#username').val());
    var pwd = $.trim($('#password').val());
    var verifycode = $.trim($('#verifycode').val());
    if (username == '') {
        layer.alert('请输入用户名', {icon: 2});
        return;
    }
    if (pwd == '') {
        layer.alert('请输入密码', {icon: 2});
        return;
    }
    $.post('login', {'username': username, 'password': pwd}, function (res) {
        if (res.code > 0) {
            // reloadImg();
            layer.alert(res.msg, {icon: 2});
        } else {
            layer.msg(res.msg, {icon: 1});
            let token = res.token;
            let url = $("input[name='returnUrl']").val() + "?token=" + token;
            console.log(url);
            setTimeout(function () {
                window.location.href = url;
            }, 1000);
        }
    });
}