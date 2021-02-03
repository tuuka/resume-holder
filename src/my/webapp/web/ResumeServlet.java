package my.webapp.web;

import my.webapp.model.*;
import my.webapp.storage.Storage;
import my.webapp.util.DateUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@WebServlet(name="ResumeServlet", urlPatterns = {"/"})
public class ResumeServlet extends HttpServlet {
    private Storage storage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        storage = my.webapp.Config.get().getStorage();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
//        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String uuid = request.getParameter("uuid");
        String editUuid = request.getParameter("edit_uuid");
        String fullName = request.getParameter("full_name");
        Map<String, String[]> m = request.getParameterMap();

        Resume r;
        //check if 'add resume' or 'save copied resume' was chosen
        final boolean isCreate = (uuid == null || uuid.length() == 0 ||
                (editUuid != null && !editUuid.equals(uuid)));
        if (isCreate) {
            if (editUuid.length() > 0){
                r = new Resume(editUuid, fullName);
            } else r = new Resume(fullName);
        } else {
            r = storage.get(uuid);
            r.setFullName(fullName);
        }

        for (ContactType ct : ContactType.values()) {
            String value = request.getParameter(ct.toString());
            if (value == null || value.length() == 0) {
                r.getContacts().remove(ct);
            } else {
                r.setContact(ct, value);
            }
        }

        for (SectionType st : SectionType.values()){
            String[] values = request.getParameterValues(st.toString());
            if (values == null || values.length == 0) {
                r.getSections().remove(st);
                continue;
            }
            switch (st){
                case OBJECTIVE:
                case PERSONAL:
                    r.setSection(st, new TextSection(values[0]));
                    break;
                case QUALIFICATIONS:
                case ACHIEVEMENT:
                    r.setSection(st, new ListSection(values));
                    break;
                case EDUCATION:
                case EXPERIENCE:
                    List<Organization> orgs = new ArrayList<>();
                    String[] orgUrls = request.getParameterValues(st + "_url");
                    for (int i = 0; i < values.length; i++) {
                        List<Organization.Position> positions = new ArrayList<>();
                        String[] posTitle = request.getParameterValues(st + "_" + i + "_postitle");
                        String[] posDescr = request.getParameterValues(st + "_" + i + "_posdescr");
                        String[] posStartDateStr = request.getParameterValues(st + "_" + i + "_posstart");
                        String[] posEndDateStr = request.getParameterValues(st + "_" + i + "_posend");
                        for(int j = 0; j < posTitle.length; j++){
                            if (posTitle[j].equals("") || posStartDateStr[j].equals("")) continue;
                            positions.add(new Organization.Position(
                                    posStartDateStr[j],
                                    (posEndDateStr[j].equals("") ? DateUtil.format(DateUtil.NOW) : posEndDateStr[j]),
                                    posTitle[j],
                                    posDescr[j]));
                        }
                        orgs.add(new Organization(new Link(values[i], orgUrls[i]), positions));
                    }
                    r.setSection(st, new OrganizationSection(orgs));
                    break;
            }
        }
        if (isCreate) storage.save(r);
            else storage.update(r);
        response.sendRedirect(String.format("%s?uuid=%s&action=view",
                request.getContextPath(), r.getUuid()));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
//        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("resumes", storage.getAllSorted());
            request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
            return;
        }

        Resume r;
        switch (action) {
            case "view":
            case "edit":
                r = storage.get(uuid);
                break;
            case "delete":
                storage.delete(uuid);
                response.sendRedirect(request.getContextPath() + "");
                return;
            case "add":
                r = new Resume("Empty");
                break;
            case "generate":
                storage.save(Resume.generateNFakeResumes(1).get(0));
                response.sendRedirect(request.getContextPath() + "");
                return;
            default:
                throw new IllegalArgumentException("Action " + action + " is illegal!");
        }

        request.setAttribute("resume", r);
        request.getRequestDispatcher(
                "view".equals(action) ? "/WEB-INF/jsp/view.jsp" : "/WEB-INF/jsp/edit.jsp"
        ).forward(request, response);

    }
}
