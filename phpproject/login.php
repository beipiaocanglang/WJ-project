<?php
    header("Content-type: text/html; charset=utf-8");

    $username = $_POST['username'];
    $password = $_POST['password'];


    if (!preg_match('/^[\w\x80-\xff]{3,15}$/', $username) || $username == "") {
        exit('<script>alert("用户名不符合规定或为空。");history.back(-1)</script>');
    }
    if (strlen($password) < 6 || strlen($password) == "") {
        exit('<script>alert("密码长度不符合规定或为空。");history.back(-1)</script>');
    }

    include('conn.php');
    mysqli_select_db($conn, $mysql_database);


    $sql = "select username,password from PHP_user where username='$username' and password = '$password'";

    $retval = mysqli_query($conn, $sql);

    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        // 输出数据
        while($row = $result->fetch_assoc()) {
            //echo $row["username"];
            if($row["username"] == $username && $row["password"] == $password){
                exit('<script>alert("登录成功");location.href = "http://www.xiaohuokj.cn"</script>');
            }else {
                exit('<script>alert("用户名或者密码错误")</script>');
            }
        }
    } else {
        exit('<script>alert("用户名或密码不正确");history.back(-1)</script>');
    }
?>