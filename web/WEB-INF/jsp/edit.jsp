<%@ page import="my.webapp.util.DateUtil" %>
<%@ page import="my.webapp.model.*" %>
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
                       name="new_uuid" required value="${resume.uuid}">
                </label>
                <a class="resume-button"
                   href="javascript:{}"
                   onclick="document.getElementById('edit_form').submit();">Save resume
                </a>
<%--                <button class="resume-button" type="submit">Save resume</button>--%>
            </div>

            <ul class="resume-edit__contacts">
                <c:forEach var="contactType" items="<%=ContactType.values()%>">
                    <jsp:useBean id="contactType" type="my.webapp.model.ContactType"/>
                    <c:set var="contact" value="<%=resume.getContacts().get(contactType)%>"/>
                    <jsp:useBean id="contact" type="java.lang.String"/>
                    <li>
                        <label> <span class="title"><%=contactType.toString().toLowerCase()%>:</span>
                            <input type="text" class="resume-edit__input"
                                   name="${contactType.toString()}" value="${empty contact? '': resume.contacts.get(contactType)}">
                        </label>
                    </li>
                </c:forEach>
            </ul>

            <ul class="resume-edit__sections">
                <c:forEach var="sectionType" items="<%=SectionType.values()%>">
                    <jsp:useBean id="sectionType" type="my.webapp.model.SectionType"/>
                    <c:set var="section" value="<%=resume.getSection(sectionType)%>"/>
                    <jsp:useBean id="section" type="my.webapp.model.Section"/>
                    <li class="resume-edit__sections-item">
                        <div class="resume-edit__section_title"><%=sectionType.getTitle()%>
                            <span class="resume-edit__add-icon <%=
                                (sectionType.equals(SectionType.OBJECTIVE) ||
                                sectionType.equals(SectionType.PERSONAL)) ? "hidden" : ""%>">
                                <img src="${pageContext.request.contextPath}/img/add.png" alt="">
                            </span>
                        </div>
                        <c:choose>
                            <c:when test="<%=sectionType.equals(SectionType.PERSONAL) || sectionType.equals(SectionType.OBJECTIVE)%>">
                                <label class="resume-edit__section_text">
                                    <input type="text" class="resume-edit__input"
                                           name="${sectionType.toString()}" value="${empty section? '': resume.sections.get(sectionType)}">
                                </label>
                            </c:when>
                            <c:when test="<%=sectionType.equals(SectionType.QUALIFICATIONS) || sectionType.equals(SectionType.ACHIEVEMENT)%>">
                                <ul class="resume-edit__section_list">
                                    <c:if test="${not empty section}">
                                        <c:forEach var="item" items="<%=((ListSection)section).getItems()%>">
                                            <li class="resume-edit__section_text">
                                                <label>
                                                    <input type="text" class="resume-edit__input"
                                                           name="${sectionType.toString()}" value="${item}">
                                                </label>
                                                <span class="resume-edit__delete-icon"><img src="${pageContext.request.contextPath}/img/delete.png" alt=""></span>
                                            </li>
                                        </c:forEach>
                                    </c:if>
                                </ul>
                            </c:when>
                            <c:when test="<%=sectionType.equals(SectionType.EDUCATION) || sectionType.equals(SectionType.EXPERIENCE)%>">
                                <ul class="resume-edit__section_org-list">
                                    <c:if test="${not empty section}">
                                        <c:forEach var="org" items="<%=((OrganizationSection)section).getOrganizations()%>" varStatus="orgIndex">
                                            <li class="resume-edit__section_org">
                                                <label class="resume-edit__org-name">Organization:
                                                    <input type="text" class="resume-edit__input"
                                                           name="${sectionType.toString()}" value="${org.homePage.name}">
                                                </label>
                                                <label class="resume-edit__org-link">URL:
                                                    <input type="text" class="resume-edit__input"
                                                           name="${sectionType.toString()}_${orgIndex.index}_url"
                                                           value="${empty org.homePage.url? '': org.homePage.url}">
                                                </label>
                                                <ul class="resume-edit__positions-list">
                                                    <c:forEach var="position" items="${org.positions}">
                                                        <jsp:useBean id="position"
                                                                     type="my.webapp.model.Organization.Position"/>
                                                        <li class="resume-edit__position">
                                                            <label>From
                                                                <input type="text" class="resume-edit__input"
                                                                       name="${sectionType.toString()}_${orgIndex.index}_posstart"
                                                                       value="<%=DateUtil.format(position.getStartDate())%>">
                                                            </label>
                                                            <label>to
                                                                <input type="text" class="resume-edit__input"
                                                                       name="${sectionType.toString()}_${orgIndex.index}_posend"
                                                                       value="<%=DateUtil.format(position.getEndDate())%>">
                                                            </label>
                                                            <label>Title:
                                                                <input type="text" class="resume-edit__input"
                                                                       name="${sectionType.toString()}_${orgIndex.index}_postitle"
                                                                       value="${position.title}">
                                                            </label>
                                                            <label>Description:
                                                                <input type="text" class="resume-edit__input"
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

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
