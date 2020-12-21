package modelRest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class ErrorResponse {
    @Getter
    @JsonProperty("serviceName")
    private String serviceName;
    @Getter
    @JsonProperty("errorCode")
    private String errorCode;
    @Getter
    @JsonProperty("userMessage")
    private String userMessage;
}
