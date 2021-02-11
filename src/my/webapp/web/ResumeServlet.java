package my.webapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.webapp.model.*;
import my.webapp.storage.Storage;
import my.webapp.util.DateUtil;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@WebServlet(name = "ResumeServlet", urlPatterns = {"/resumes"})
public class ResumeServlet extends HttpServlet {
    private Storage storage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        storage = my.webapp.Config.get().getStorage();
    }

    protected void doPost(@NotNull HttpServletRequest request,
                          @NotNull HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
//        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String uuid = request.getParameter("uuid");
        String editUuid = request.getParameter("edit_uuid");
        String fullName = request.getParameter("full_name");
//        Map<String, String[]> m = request.getParameterMap();

        Resume r;
        //check if 'add resume' or 'save copied resume' was chosen
        final boolean isCreate = (uuid == null || uuid.length() == 0 ||
                (editUuid != null && !editUuid.equals(uuid)));
        if (isCreate) {
            if (editUuid.length() > 0) {
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

        for (SectionType st : SectionType.values()) {
            String[] values = request.getParameterValues(st.toString());
            if (values == null || values.length == 0 || values[0].equals("")) {
                r.getSections().remove(st);
                continue;
            }
            switch (st) {
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
                        if (posTitle != null && posDescr != null &&
                                posStartDateStr != null && posEndDateStr != null) {
                            for (int j = 0; j < posTitle.length; j++) {
                                if (posTitle[j].equals("") || posStartDateStr[j].equals("")) continue;
                                positions.add(new Organization.Position(
                                        posStartDateStr[j],
                                        (posEndDateStr[j].equals("") ? DateUtil.format(DateUtil.NOW) : posEndDateStr[j]),
                                        posTitle[j],
                                        posDescr[j]));
                            }
                        }
                        orgs.add(new Organization(new Link(values[i], orgUrls[i]), positions));
                    }
                    r.setSection(st, new OrganizationSection(orgs));
                    break;
            }
        }
        if (isCreate) storage.save(r);
        else storage.update(r);
        response.sendRedirect(String.format("%s/resumes?uuid=%s&action=view",
                request.getContextPath(), r.getUuid()));
    }

    protected void doGet(@NotNull HttpServletRequest request,
                         @NotNull HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
//        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");

        if (action == null) {
            String[] searchTypes = request.getParameterValues("search-type");
            String[] searchContents = request.getParameterValues("search-content");
            List<Resume> resumes = storage.getAllSorted();

            Map<String, String> searchFiltersMap = new HashMap<>();
            String searchMapJson = null;

            // create filtered resumes and legend for filtered mode
            if (searchTypes != null && searchContents != null) {
                resumes = resumes.stream().filter(resume ->
                        checkFilter(resume, searchTypes, searchContents))
                        .collect(Collectors.toList());
                searchFiltersMap =
                        IntStream.range(0, searchTypes.length).boxed().collect(
                                Collectors.toMap(i -> searchTypes[i], i -> searchContents[i])
                        );
            } else { //create Json-map to fill 'select' and 'input' fields to filter resumes
                ObjectMapper om = new ObjectMapper();
                searchMapJson = om.writeValueAsString(getSearchMapForFilters(resumes));
            }

            request.setAttribute("searchFiltersMap", searchFiltersMap);
            request.setAttribute("searchMapJson", searchMapJson);
            request.setAttribute("resumes", resumes);
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
                response.sendRedirect(request.getContextPath() + "/resumes");
                return;
            case "add":
                r = new Resume("Empty");
                break;
            case "generate":
                storage.save(Resume.generateNFakeResumes(1).get(0));
                response.sendRedirect(request.getContextPath() + "/resumes");
                return;
            default:
                throw new IllegalArgumentException("Action " + action + " is illegal!");
        }

        request.setAttribute("resume", r);
        request.getRequestDispatcher(
                "view".equals(action) ? "/WEB-INF/jsp/view.jsp" : "/WEB-INF/jsp/edit.jsp"
        ).forward(request, response);

    }

    private boolean checkFilter(Resume resume, String[] searchTypes, String[] searchContents) {
        int i = 0;
        for (String s : searchTypes) {
            // check if searchType is contact
            if (s.equals("fullname") && resume.getFullName().contains(searchContents[i])) return true;
            ContactType ct = Arrays.stream(ContactType.values())
                    .filter(item -> item.getTitle().equalsIgnoreCase(s)).findFirst().orElse(null);
            if (ct != null && resume.getContact(ct).contains(searchContents[i])) return true;
            //check if searchType is section
            SectionType st = Arrays.stream(SectionType.values())
                    .filter(item -> item.getTitle().equalsIgnoreCase(s))
                    .findFirst().orElse(null);
            if (st != null) {
                switch (st) {
                    case PERSONAL:
                    case OBJECTIVE:
                        if (((TextSection) resume.getSection(st)).getContent().contains(searchContents[i])
                        ) return true;
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        int j = i;
                        if (((ListSection) resume.getSection(st)).getItems()
                                .stream().anyMatch(item -> item.contains(searchContents[j]))
                        ) return true;
                        break;
                    case EDUCATION:
                    case EXPERIENCE:
                        int k = i;
                        if (((OrganizationSection) resume.getSection(st))
                                .getOrganizations().stream().anyMatch(item ->
                                        item.getHomePage().getName().contains(searchContents[k]))
                        ) return true;
                        break;
                }
            }
            i++;
        }
        return false;
    }

    Map<String, List<String>> getSearchMapForFilters(List<Resume> resumes) {
        Map<String, List<String>> searchMap = new HashMap<>();
        searchMap.put("fullname", new ArrayList<>());
        for (ContactType ct : ContactType.values()) {
            searchMap.put(ct.getTitle(), new ArrayList<>(Collections.emptyList()));
        }
        for (SectionType st : SectionType.values()) {
            searchMap.put(st.getTitle(), new ArrayList<>(Collections.emptyList()));
        }
        resumes.forEach(r -> {
            searchMap.get("fullname").add(r.getFullName());
            for (Map.Entry<ContactType, String> entry : r.getContacts().entrySet()) {
                if (!searchMap.get(entry.getKey().getTitle()).contains(entry.getValue()))
                    searchMap.get(entry.getKey().getTitle()).add(entry.getValue());
            }
            for (Map.Entry<SectionType, Section> entry : r.getSections().entrySet()) {
                switch (entry.getKey()) {
                    case OBJECTIVE:
                    case PERSONAL:
                        String text = ((TextSection) entry.getValue()).getContent();
                        searchMap.get(entry.getKey().getTitle())
                                .add(text.length() > 50 ? text.substring(0, 49) : text);
                        break;
                    case QUALIFICATIONS:
                    case ACHIEVEMENT:
                        for (String s : ((ListSection) entry.getValue()).getItems()) {
                            searchMap.get(entry.getKey().getTitle())
                                    .add(s.length() > 50 ? s.substring(0, 49) : s);
                        }
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        for (Organization o : ((OrganizationSection) entry
                                .getValue()).getOrganizations()) {
                            searchMap.get(entry.getKey().getTitle())
                                    .add(o.getHomePage().getName());
                        }
                }
            }
        });
        searchMap.replaceAll((k, v) -> new ArrayList<>(new TreeSet<>(v)));
        return searchMap;
    }


}
