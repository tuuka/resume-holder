<%@ page import="my.webapp.model.ContactType" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/style.css">
    <title>Список всех резюме</title>
</head>
<body>
    <main>
        <section class="container">
            <ul class="resume-list">
                <li class="resume-list__item">
                    <div class="resume-list__content">
                        <div class="resume-list__item_name resume-list_column-title">Full name</div>
                        <div class="resume-list__item_uuid resume-list_column-title">UUID</div>
                    </div>
                    <div class="resume-list__item_actions resume-list_column-title">
                        Actions
                    </div>
                </li>
                <c:forEach items="${resumes}" var="resume">
                    <jsp:useBean id="resume" type="my.webapp.model.Resume"/>

                    <li class="resume-list__item">
                        <div class="resume-list__content">
                            <a class="resume-list__item_name"
                               href="resume?uuid=${resume.uuid}&action=view">
                                    ${resume.fullName}
                            </a>
                            <a class="resume-list__item_uuid"
                               href="resume?uuid=${resume.uuid}&action=view">
                                    ${resume.uuid}</a>
                        </div>
                        <div class="resume-list__item_actions">
                            <a href="resume?uuid=${resume.uuid}&action=edit">
                                <img src="${pageContext.request.contextPath}/img/pencil.png" alt="Edit">
                            </a>
                            <a href="resume?uuid=${resume.uuid}&action=delete">
                                <img src="${pageContext.request.contextPath}/img/delete.png" alt="Delete">
                            </a>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </section>
    </main>
</body>
</html>
