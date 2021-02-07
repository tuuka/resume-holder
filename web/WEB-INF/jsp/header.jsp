<%@ page contentType="text/html;charset=UTF-8" %>

<header>
    <menu>
        <ul class="menu">
            <li><a class="menu__item" href="${pageContext.request.contextPath}">List</a></li>
            <li><a class="menu__item" href="${pageContext.request.contextPath}?action=add">Add</a></li>
            <li><a class="menu__item" href="${pageContext.request.contextPath}?action=generate">Generate Fake</a></li>
            <li><a class="menu__item filter-link disabled" href="#" onclick="filter_click()">Filter</a></li>
        </ul>
    </menu>
</header>
