<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
    </head>
    <body>
        <h1>${hello}</h1>

        <h2>null值的第一种处理方式</h2>
        <h5>${hello!}</h5>
        <h5>${hello!""}</h5>
        <h5>${hello!"空值时给的默认值"}</h5>

        <h2>null值的第二种处理方式</h2>
        <#if hello??>
            ${hello}
        </#if>
    </body>
</html>