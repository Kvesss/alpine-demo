package com.example.accounts.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(
  name = "Error Response",
  description = "Schema to hold Error Response info"
)
public class ErrorResponseDto {

    @Schema(
      description = "Api path"
    )
    private String apiPath;

    @Schema(
      description = "error code"
    )
    private HttpStatus errorCode;

    @Schema(
      description = "error message"
    )
    private String errorMessage;

    @Schema(
      description = "Time of the error"
    )
    private LocalDateTime errorTime;
}
