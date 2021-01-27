package my.webapp.web;

import my.webapp.model.Resume;
import my.webapp.storage.Storage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResumeServlet extends HttpServlet {
    private Storage storage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        storage = my.webapp.Config.get().getStorage();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
                response.sendRedirect("");
                return;
            case "add":
                r = new Resume();
                break;
            default:
                throw new IllegalArgumentException("Action " + action + " is illegal!");
        }

        request.setAttribute("resume", r);
        request.getRequestDispatcher(
                "view".equals(action) ? "/WEB-INF/jsp/view.jsp" : "/WEB-INF/jsp/edit.jsp"
        ).forward(request, response);

    }
}
