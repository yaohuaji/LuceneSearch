<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="false" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>百度一下，你就知道</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
</head>

<body>
<form action="search.do" method="post">
    <input type="hidden" name="num" value="1"/>
    <input type="text" name="keywords" maxlength="30"/>
    <input type="submit" value="百度一下">
</form>
<br>
<c:if test="${! empty page.list}">
    百度为您找到相关结果约${page.rowCount}个
    <br>
    <c:forEach items="${page.list}" var="htmlbean">
        <a href="${htmlbean.url}">${htmlbean.title}</a>
        <p>${htmlbean.context}</p>
        ${htmlbean.url}
        <br>
    </c:forEach>
    <br>
    <c:if test="${page.hasPrevious}">
        <a href="search.do?num=${page.previousPageNum}&keywords=${keywords}">上一页</a>
    </c:if>
    <c:forEach begin="${page.everyPageStart}" end="${page.everyPageEnd}" var="current">
        <c:choose>
            <c:when test="${page.pageNum eq current}">
                <a><font color="#a52a2a">${current}</font></a>
            </c:when>
            <c:otherwise>
                <a href="search.do?num=${current}&keywords=${keywords}">${current} </a>
            </c:otherwise>
        </c:choose>
        &nbsp;&nbsp;&nbsp;
    </c:forEach>
    <c:if test="${page.hasNext}">
        <a href="search.do?num=${page.nextPageNum}&keywords=${keywords}">下一页</a>
    </c:if>
</c:if>
</body>
</html>