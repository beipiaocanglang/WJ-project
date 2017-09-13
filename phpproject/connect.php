<?php
$server = "rm-bp1s9co7ux0293175.mysql.rds.aliyuncs.com";
$db_username = " r1yc477ai2";
$db_password = "xhJSzc@1";

// 创建连接
$con = mysql_connect($server,$db_username,$db_password);

// 检测连接
if ($conn->connect_error) {
    die("连接失败: " . $conn->connect_error);
}else {
    echo "连接成功";
}
mysql_select_db('user',$con);//选择数据库（我的是test）
?>