<?php
$dbhost = 'rm-bp1s9co7ux0293175.mysql.rds.aliyuncs.com';  // mysql服务器主机地址
$dbuser = 'r1yc477ai2';            // mysql用户名
$dbpass = 'xhJSzc@1';          // mysql用户名密码

$conn = mysqli_connect($dbhost, $dbuser, $dbpass);

if(! $conn )
{
    die('连接失败: ' . mysqli_error($conn));
}
/*echo '连接成功<br />';*/
// 设置编码，防止中文乱码
mysqli_query($conn , "set names utf8");
?>