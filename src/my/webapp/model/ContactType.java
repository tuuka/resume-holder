package my.webapp.model;

public enum ContactType {
    PHONE("Phone"),
    MOBILE("Mobile"),
    SKYPE("Skype"),
    MAIL("e-mail"),
    GITHUB("GitHub") {
        @Override
        public String toHtml(String value) {
            return toLink(value, getTitle());
        }
    },
    HOME_PAGE("Home page") {
        @Override
        public String toHtml(String value) {
            return toLink(value, getTitle());
        }
    };
    private final String title;

    ContactType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String toHtml(String contact) {
        return contact==null? "":
                String.format("<span class=\"title\">%s: </span>%s",
                        this.getTitle(), contact);
    }

    private static String toLink(String contact, String title) {
        return contact==null? "":
                String.format("<span class=\"title\">%1$s: </span><a href=\"%2$s\">%2$s</a>",
                title, contact);
    }
}
