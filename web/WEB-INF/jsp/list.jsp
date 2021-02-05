<%@ page import="my.webapp.model.ContactType" %>
<%@ page import="my.webapp.model.SectionType" %><%--<%@ page import="my.webapp.model.ContactType" %>--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/style.css" type="text/css">
    <title>Список всех резюме</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<main>
    <section class="container">
        <ul class="resume-list">
            <li class="resume-list__item">
                <div class="resume-list__content">
                    <div class="resume-list__item-name resume-list_column-title">Full name</div>
                    <div class="resume-list__item-mail resume-list_column-title">e-mail</div>
                    <div class="resume-list__item-obj resume-list_column-title"
                        ><%=SectionType.OBJECTIVE.getTitle()%></div>
                </div>
                <div class="resume-list__item-actions resume-list_column-title">
                    Actions
                </div>
            </li>
            <c:forEach items="${resumes}" var="resume">
                <jsp:useBean id="resume" type="my.webapp.model.Resume"/>
                <li class="resume-list__item">
                    <a class="resume-list__content" href="?uuid=${resume.uuid}&action=view">
                        <div class="resume-list__item-name">${resume.fullName}</div>
                        <div class="resume-list__item-mail"><%=
                            resume.getContact(ContactType.MAIL) == null ? "" :
                            resume.getContact(ContactType.MAIL)%>
                        </div>
                        <div class="resume-list__item-obj"><%=
                            resume.getSection(SectionType.OBJECTIVE) == null ? "" :
                            resume.getSection(SectionType.OBJECTIVE)%>
                        </div>
                    </a>
                    <div class="resume-list__item-actions">
                        <a href="?uuid=${resume.uuid}&action=edit"><img
                                src="${pageContext.request.contextPath}/img/edit.svg" alt="Edit"></a>
                        <a href="?uuid=${resume.uuid}&action=delete"><img
                                src="${pageContext.request.contextPath}/img/delete.svg" alt="Delete"></a>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </section>
</main>
</body>
</html>
