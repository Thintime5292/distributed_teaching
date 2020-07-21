layui.use(['layer'], function () {
    let $ = layui.jquery;
    let layer = layui.layer;
    $(".body-login").css("height", $(window).height());
    $('#username').focus();
    $("#repassword").keyup(function () {
        checkPwd($(this));
    });
    $("#repassword").blur(function () {
        checkPwd($(this));
    });

    function checkPwd($this) {
        let pwd = $("#password").val();
        let repwd = $this.val();
        pwd = $.trim(pwd);
        repwd = $.trim(repwd);
        if (pwd != repwd) {
            $this.addClass("red-border");
            $("#password").addClass("red-border");
        } else if (pwd == repwd) {
            $this.removeClass("red-border");
            $("#password").removeClass("red-border");
            $this.addClass("green-border");
            $("#password").addClass("green-border");
        }
    }

    $('input').keydown(function (e) {
        if (e.keyCode == 13) {
            dologin();
        }
    });
    $(window).resize(function () {
        let winHeight = $(this).height();
        $(".body-login").css("height", winHeight);
    });
    $("#username").blur(function () {
        var val = $(this).val();
        var $this = $(this);
        if (val != "") {
            $.post("/user/isExist", {"username": val}, function (res) {
                if (res.code > 0) {
                    $this.addClass("red-border");
                    $this.removeClass("green-border");
                } else {
                    $this.removeClass("red-border");
                    $this.addClass("green-border");
                }
            });
        }
    });

    $("#registerBtn").on("click", function () {
        register();
    });


    function register() {
        var username = $.trim($('#username').val());
        var pwd = $.trim($('#password').val());
        if (username == '') {
            layer.alert('请输入用户名', {icon: 2});
            return;
        }
        if (pwd == '') {
            layer.alert('请输入密码', {icon: 2});
            return;
        }
        $.post('/user/register', {'username': username, 'password': pwd}, function (res) {
            if (res.code > 0) {
                layer.alert(res.msg, {icon: 2});
            } else {
                layer.msg(res.msg, {icon: 1});
                let url = "http://www.teaching.com:9986/home/index";
                setTimeout(function () {
                    window.location.href = url;
                }, 1000);
            }
        });
    }
});