package com.serjlemast.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;

@Data
@Schema(description = "Request payload containing one or more URLs to add")
public class UrlRequest {

  @NotEmpty(message = "URL list must not be empty")
  @Size(min = 1, max = 100, message = "You must provide between 1 and 100 URLs")
  @Schema(
      description = "List of URL addresses to be saved",
      example = "[\"http://localhost:5001/sensor/data\"]")

  /*
   * todo:
   *   1. Investigate: Is there a better type for URL than 'string' in Java?
   */
  private Set<
          @Pattern(
              regexp = "^http://\\d{1,3}(\\.\\d{1,3}){3}:(50\\d\\d|5100)/sensor/data$",
              message =
                  "Each URL must match http://localhost:PORT/sensor/data where PORT is between 5000 and 5100")
          String>
      urls;
}
