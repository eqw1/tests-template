package modelRest.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Builder
@XmlRootElement(name = "ROOT")
public class ApiToken {

    @Getter
    @XmlElement(name = "SESSION_ID")
    @JsonProperty("authtoken")
    private String authtoken;

}
