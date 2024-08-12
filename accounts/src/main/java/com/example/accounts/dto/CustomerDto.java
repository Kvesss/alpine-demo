package com.example.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
  name = "Customer",
  description = "Schema to hold Customer and Account info"
)
public class CustomerDto {

    @Schema(
      description = "Name of the customer", example = "John Doe"
    )
    @NotEmpty(message = "Name cannot be null or empty!")
    @Size(min = 5, max = 30, message = "Length can be between 5 and 30.")
    private String name;

    @Schema(
      description = "Name of the customer", example = "e.mail@gmail.com"
    )
    @NotEmpty(message = "E-mail cannot be null or empty!")
    @Email(message = "E-mail should be valid value.")
    private String email;

    @Schema(
      description = "Name of the customer", example = "1234567891"
    )
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    private AccountsDto accountsDto;
}
