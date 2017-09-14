<?php
    $mysql_server_name='127.0.0.1'; //改成自己的mysql数据库服务器

    $mysql_username='root'; //改成自己的mysql数据库用户名

    $mysql_password='root'; //改成自己的mysql数据库密码

    $mysql_database='php'; //改成自己的mysql数据库名

    $conn=mysqli_connect($mysql_server_name, $mysql_username, $mysql_password) or die("error connecting"); //连接数据库

    echo '连接成功<br/>';

    mysqli_query($conn, "set names 'utf8'"); //数据库输出编码 应该与你的数据库编码保持一致.南昌网站建设公司百恒网络PHP工程师建议用UTF-8 国际标准编码.

?>