package my.webapp.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
/* Jackson 2.12 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextSection.class, name = "Text"),
        @JsonSubTypes.Type(value = ListSection.class, name = "List"),
        @JsonSubTypes.Type(value = OrganizationSection.class, name = "OrganizationList")
})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Section implements Serializable {

}
