package my.webapp.model;

public enum SectionType {
    OBJECTIVE("Objective") {
        @Override
        public Section getEmptySection(){
            return TextSection.EMPTY;
        }
    },
    PERSONAL("Personal") {
        @Override
        public Section getEmptySection(){
            return TextSection.EMPTY;
        }
    },
    QUALIFICATIONS("Qualifications") {
        @Override
        public Section getEmptySection(){
            return ListSection.EMPTY;
        }
    },
    ACHIEVEMENT("Achievement") {
        @Override
        public Section getEmptySection(){
            return ListSection.EMPTY;
        }
    },
    EXPERIENCE("Experience") {
        @Override
        public Section getEmptySection(){
            return OrganizationSection.EMPTY;
        }
    },
    EDUCATION("Education") {
        @Override
        public Section getEmptySection(){
            return OrganizationSection.EMPTY;
        }
    };

    private final String title;

    SectionType(String title) {
        this.title = title;
    }

    public String getTitle() { return title; }

    public abstract Section getEmptySection();

}
