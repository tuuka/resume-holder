<%@ page contentType="text/html;charset=UTF-8" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
</head>

<header>
    <menu>
        <ul class="menu">
            <li><a class="menu__item" href="${pageContext.request.contextPath}/resumes">List</a></li>
            <li><a class="menu__item" href="${pageContext.request.contextPath}/resumes?action=add">Add</a></li>
            <li><a class="menu__item" href="${pageContext.request.contextPath}/resumes?action=generate">Generate Fake</a></li>
            <li><a class="menu__item filter-link disabled" href="#" onclick="filter_click()">Filter</a></li>
        </ul>
    </menu>
</header>
