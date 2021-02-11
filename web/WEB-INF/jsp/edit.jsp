<%@ page import="my.webapp.util.DateUtil" %>
<%@ page import="my.webapp.model.*" %>
<%@ page import="java.util.UUID" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<%--    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">--%>
<%--    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">--%>
    <jsp:useBean id="resume" type="my.webapp.model.Resume" scope="request"/>
    <title>Resume ${resume.fullName}</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<main>
    <section class="container resume-edit">
        <form class="resume-edit__form" method="post"
              name="resumeServlet" id="edit_form" action=""
              enctype="application/x-www-form-urlencoded">
            <div class="resume-edit__name">
                <label><span class="title">Full name:</span>
                    <input type="text" class="resume-edit__input"
                       name="full_name" required value="${resume.fullName}">
                </label>
            </div>
            <div class="resume-edit__uuid">
                <label><span class="title">uuid:</span>
                    <input type="text" disabled class="resume-edit__input"
                       name="edit_uuid" required value="${resume.uuid}">
                </label>
                <a class="resume-button"
                   href="javascript:{}"
                   onclick="saveResume();">Save resume</a>
                <a class="resume-button"
                   href="javascript:{}"
                   onclick="saveResume('<%=UUID.randomUUID().toString()%>')">Copy resume</a>
<%--                <button class="resume-button" type="submit">Save resume</button>--%>
            </div>

            <ul class="resume-edit__contacts">
                <c:forEach var="contactType" items="<%=ContactType.values()%>">
                    <c:set var="contact" value="${resume.getContact(contactType)}"/>
                    <li>
                        <label> <span class="title">${contactType.toString().toLowerCase()}:</span>
                            <input type="text" class="resume-edit__input"
                                   name="${contactType.toString()}"
                                   value="${empty contact? "": contact}">
                        </label>
                    </li>
                </c:forEach>
            </ul>

            <ul class="resume-edit__sections">
                <c:forEach var="sectionType" items="<%=SectionType.values()%>">
                    <c:set var="section" value="${
                        resume.getSection(sectionType)==null?
                        sectionType.emptySection :
                        resume.getSection(sectionType) }"/>
                    <jsp:useBean id="section" type="my.webapp.model.Section"/>
                    <li class="resume-edit__sections-item">
                        <div class="resume-edit__section_title" data-section-type="${sectionType}">
                            <span class="resume-edit__add-icon ${
                                (sectionType=='OBJECTIVE' ||
                                 sectionType=='PERSONAL') ? "hidden" : ""}">
                                <img src="${pageContext.request.contextPath}/img/add.svg" alt="">
                            </span>${sectionType.title}</div>
                        <c:choose>
                            <c:when test="${sectionType=='PERSONAL' || sectionType=='OBJECTIVE'}">
                                <label class="resume-edit__text">
                                    <input type="text" class="resume-edit__input"
                                           name="${sectionType.toString()}"
                                           value="<%=((TextSection)section).getContent()%>">
                                </label>
                            </c:when>
                            <c:when test="${sectionType=='QUALIFICATIONS' || sectionType=='ACHIEVEMENT'}">
                                <ul class="resume-edit__text-list">
                                    <c:if test="${not empty section}">
                                        <c:forEach var="item" items="<%=((ListSection)section).getItems()%>">
                                            <li class="resume-edit__text">
                                                <span class="resume-edit__delete-icon">
                                                    <img src="${pageContext.request.contextPath}/img/delete.svg" alt="">
                                                </span>
                                                <label>
                                                    <input type="text" class="resume-edit__input"
                                                           name="${sectionType.toString()}" value="${item}">
                                                </label>
                                            </li>
                                        </c:forEach>
                                    </c:if>
                                </ul>
                            </c:when>
                            <c:when test="${sectionType=='EDUCATION' || sectionType=='EXPERIENCE'}">
                                <ul class="resume-edit__org-list">
                                    <c:if test="${not empty section}">
                                        <c:forEach var="org" items="<%=((OrganizationSection)section)
                                                .getOrganizations()%>" varStatus="orgIndex">
                                            <li class="resume-edit__org">
                                                <span class="resume-edit__add-icon">
                                                    <img src="${pageContext.request.contextPath}/img/add.svg" alt="">
                                                </span>
                                                <span class="resume-edit__delete-icon">
                                                    <img src="${pageContext.request.contextPath}/img/delete.svg" alt="">
                                                </span>
                                                <label class="resume-edit__org-name">Organization: <input
                                                        type="text" class="resume-edit__input"
                                                           name="${sectionType.toString()}" value="${org.homePage.name}">
                                                </label>
                                                <label class="resume-edit__org-link">URL: <input
                                                        type="text" class="resume-edit__input"
                                                           name="${sectionType.toString()}_url"
                                                           value="${empty org.homePage.url? '': org.homePage.url}">
                                                </label>
                                                <ul class="resume-edit__positions-list">
                                                    <c:forEach var="position" items="${org.positions}">
                                                        <jsp:useBean id="position"
                                                                     type="my.webapp.model.Organization.Position"/>
                                                        <li class="resume-edit__position">
                                                            <span class="resume-edit__delete-icon">
                                                                <img src="${pageContext.request.contextPath}/img/delete.svg" alt="">
                                                            </span>
                                                            <label>From: <input type="text" class="resume-edit__input"
                                                                       name="${sectionType.toString()}_${orgIndex.index}_posstart"
                                                                       value="<%=DateUtil.format(position.getStartDate())%>">
                                                            </label>
                                                            <label>to: <input type="text" class="resume-edit__input"
                                                                       name="${sectionType.toString()}_${orgIndex.index}_posend"
                                                                       value="<%=DateUtil.format(position.getEndDate())%>">
                                                            </label>
                                                            <label>Title: <input type="text" class="resume-edit__input"
                                                                       name="${sectionType.toString()}_${orgIndex.index}_postitle"
                                                                       value="${position.title}">
                                                            </label>
                                                            <label>Description: <input type="text" class="resume-edit__input"
                                                                       name="${sectionType.toString()}_${orgIndex.index}_posdescr"
                                                                       value="${position.description}">
                                                            </label>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                            </li>
                                        </c:forEach>
                                    </c:if>
                                </ul>
                            </c:when>
                        </c:choose>
                    </li>
                </c:forEach>
            </ul>
        </form>
    </section>
</main>


<jsp:include page="footer.jsp"/>
</body>
</html>
