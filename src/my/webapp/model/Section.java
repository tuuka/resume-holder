package my.webapp.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "Resume_contacts")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextSection.class, name = "TextType"),
        @JsonSubTypes.Type(value = ListSection.class, name = "ListType"),
        @JsonSubTypes.Type(value = OrganizationSection.class, name = "OrgListType")
})
public abstract class Section implements Serializable {

}
