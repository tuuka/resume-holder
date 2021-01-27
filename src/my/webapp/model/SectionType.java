package my.webapp.model;

public enum SectionType {
    OBJECTIVE("OBJECTIVE") {
        @Override
        public Section getEmptySection(){
            return TextSection.EMPTY;
        }
    },
    PERSONAL("PERSONAL") {
        @Override
        public Section getEmptySection(){
            return TextSection.EMPTY;
        }
    },
    QUALIFICATIONS("QUALIFICATIONS") {
        @Override
        public Section getEmptySection(){
            return ListSection.EMPTY;
        }
    },
    ACHIEVEMENT("ACHIEVEMENT") {
        @Override
        public Section getEmptySection(){
            return ListSection.EMPTY;
        }
    },
    EXPERIENCE("EXPERIENCE") {
        @Override
        public Section getEmptySection(){
            return OrganizationSection.EMPTY;
        }
    },
    EDUCATION("EDUCATION") {
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
