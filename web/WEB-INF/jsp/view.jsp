<%@ page import="my.webapp.model.ListSection" %>
<%@ page import="my.webapp.model.SectionType" %>
<%@ page import="my.webapp.model.TextSection" %>
<%@ page import="my.webapp.model.OrganizationSection" %>
<%@ page import="my.webapp.util.DateUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/style.css">
    <jsp:useBean id="resume" type="my.webapp.model.Resume" scope="request"/>
    <title>Resume ${resume.fullName}</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<main>
    <section class="container resume-view">
        <div class="resume-view__name"><span class="title">Full name: </span>${resume.fullName}</div>
        <div class="resume-view__uuid"><span class="title">uuid: </span>${resume.uuid}
            <a class="resume-button" href="?uuid=${resume.uuid}&action=edit">Edit resume</a>
        </div>
        <div class="resume-view__contacts">
            <c:forEach var="contactEntry" items="${resume.contacts}">
                <jsp:useBean id="contactEntry"
                             type="java.util.Map.Entry<my.webapp.model.ContactType, java.lang.String>"/>
                <%--                <p>${contactEntry.key.toHtml(contactEntry.value)}</p>--%>
                <p><%=contactEntry.getKey().toHtml(contactEntry.getValue())%>
                </p>
            </c:forEach>
        </div>

        <ul class="resume-view__sections">
            <c:forEach var="type" items="<%=SectionType.values()%>">
                <jsp:useBean id="type" type="my.webapp.model.SectionType"/>
                <c:set var="section" value="<%=resume.getSections().get(type)==null? type.getEmptySection(): resume.getSections().get(type)%>"/>
                <jsp:useBean id="section" type="my.webapp.model.Section"/>
                <li class="resume-view__sections-item">
                    <div class="resume-view__section_title"><%=type.getTitle()%></div>
                    <c:choose>
                        <c:when test="<%=type.equals(SectionType.PERSONAL) || type.equals(SectionType.OBJECTIVE)%>">
                            <div class="resume-view__section_text <%=type.equals(SectionType.OBJECTIVE) ? "objective" : ""%>"><%=((TextSection) section).getContent()%></div>
                        </c:when>
                        <c:when test="<%=type.equals(SectionType.QUALIFICATIONS) || type.equals(SectionType.ACHIEVEMENT)%>">
                            <ul class="resume-view__section_list">
                                <c:forEach var="item" items="<%=((ListSection)section).getItems()%>">
                                    <li class="resume-view__section_text">${item}</li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:when test="<%=type.equals(SectionType.EXPERIENCE) || type.equals(SectionType.EDUCATION)%>">
                            <ul class="resume-view__section_org-list">
                                <c:forEach var="org" items="<%=((OrganizationSection)section).getOrganizations()%>">
                                    <li class="resume-view__section_org">
                                        <c:choose>
                                            <c:when test="${empty org.homePage.url}">
                                                <div class="resume-view__org-name">${org.homePage.name}</div>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="resume-view__org-name"
                                                   href="${org.homePage.url}">${org.homePage.name}</a>
                                            </c:otherwise>
                                        </c:choose>
                                        <ul class="resume-view__positions-list">
                                            <c:forEach var="position" items="${org.positions}">
                                                <jsp:useBean id="position"
                                                             type="my.webapp.model.Organization.Position"/>
                                                <li class="resume-view__position">From <span
                                                        class="position-date"><%=DateUtil.format(position.getStartDate())%></span> to
                                                    <span class="position-date"><%=DateUtil.format(position.getEndDate())%></span> <%=position.getTitle()%> (<%=position.getDescription()%>)
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                    </c:choose>
                </li>
            </c:forEach>
        </ul>
    </section>
</main>
</body>
</html>
