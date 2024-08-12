package com.example.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(
  name = "Response",
  description = "Schema to hold Response info"
)
public class ResponseDto {

    @Schema(
      description = "description of the response code", example = "200"
    )
    private String statusCode;

    @Schema(
      description = "Message status", example = "OK"
    )
    private String statusMessage;
}
