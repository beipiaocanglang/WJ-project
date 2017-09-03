<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Freemarker获取集合</title>
    </head>
    <body>
        <table border="1">
            <tr>
                <td>用户姓名</td>
                <td>用户地址</td>
                <td>用户年龄</td>
            </tr>

            <#list pList as p>
            <#if p_index%2=0>
            <tr bgcolor="red">
            <#else>
            <tr>
            </#if>

                <td>${p.username}</td>
                <td>${p.address}</td>
                <td>${p.age}</td>
            </tr>
            </#list>
        </table>
    </body>
</html>