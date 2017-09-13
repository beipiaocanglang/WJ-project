<?php
header("Content-type: text/html; charset=utf-8");
if(isset($_POST['submit'])){
    $username = $_POST['username'];
    $password = $_POST['password'];
    $email = $_POST['email'];
    $mobile = $_POST['mobile'];


//注册信息判断
    if(!preg_match('/^[\w\x80-\xff]{3,15}$/', $username)){
        exit('<script>alert("用户名不符合规定。");history.back(-1)</script>');
    }
    if(strlen($password) < 6){
        exit('<script>alert("密码长度不符合规定。");history.back(-1)</script>');
    }
    if(!preg_match("/^1[34578]\d{9}$/",$mobile)){
        exit('<script>alert("手机号格式错误。");history.back(-1)</script>');
    }
    if(!preg_match('/^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,})$/', $email)){
        exit('<script>alert("电子邮箱格式错误。");history.back(-1)</script>');
    }

    include('conn.php');

    $sql = "INSERT INTO users(username,password,email,mobile)VALUES('$username','$password','$email','$mobile')";


    mysqli_select_db($conn, 'r1yc477ai2' );

    $retval = mysqli_query( $conn, $sql );



    if(!$retval )
    {
        //die('无法插入数据: ' . mysqli_error($conn));
        die('<script>alert("用户注册失败");history.back(-1)</script>'. mysqli_error($conn));
    }
//echo "数据插入成功\n";
    echo '<script>alert("用户注册成功");location.href = "login.html"</script>';

    mysqli_close($conn);
}else {
    exit('<script>location.href=\'login.html\'</script>');
}

?>