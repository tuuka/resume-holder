package my.webapp.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextSection.class, name = "Text"),
        @JsonSubTypes.Type(value = ListSection.class, name = "List"),
        @JsonSubTypes.Type(value = OrganizationSection.class, name = "OrganizationList")
})
public abstract class Section implements Serializable {

}
