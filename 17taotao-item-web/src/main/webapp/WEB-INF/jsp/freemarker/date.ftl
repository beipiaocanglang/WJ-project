<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Freemarker处理时间类型</title>
    </head>
    <body>
        现在日期：${today?date}<br>
        现在时间：${today?time}<br>
        现在日期和时间：${today?datetime}<br>
        格式化：${today?string('yyyy-MM-dd')}<br>
    </body>
</html>