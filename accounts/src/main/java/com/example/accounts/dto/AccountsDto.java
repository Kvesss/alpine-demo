package com.example.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
  name = "Accounts",
  description = "Schema to hold Customer and Account info"
)
public class AccountsDto {

    @Schema(
      description = "Account number", example = "3453453423"
    )
    @NotEmpty(message = "Name cannot be null or empty!")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private Long accountNumber;

    @Schema(
      description = "account type", example = "savings"
    )
    @NotEmpty(message = "E-mail cannot be null or empty!")
    @Email(message = "E-mail should be valid value.")
    private String accountType;

    @Schema(
      description = "Branch Address", example = "Jersey City"
    )
    @NotEmpty(message = "BranchAddress can not be a null or empty")
    private String branchAddress;
}
