package com.example.accounts.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.accounts.constants.AccountsConstants;
import com.example.accounts.dto.AccountsContactInfoDto;
import com.example.accounts.dto.CustomerDto;
import com.example.accounts.dto.ErrorResponseDto;
import com.example.accounts.dto.ResponseDto;
import com.example.accounts.service.AccountsService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Tag(name = "CRUD REST APIs for Accounts",
     description = "Extra description text about REST Controller")
@RestController
@RequestMapping(path = "/api/v1/accounts",
                produces = { MediaType.APPLICATION_JSON_VALUE })
@Validated
public class AccountsController {

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    private final AccountsService accountsService;

    @Value("${build.version}")
    private String buildVersion;

    private final Environment environment;

    private final AccountsContactInfoDto accountsContactInfoDto;

    public AccountsController(final AccountsService accountsService, final Environment environment, final AccountsContactInfoDto accountsContactInfoDto) {
        this.accountsService = accountsService;
        this.environment = environment;
        this.accountsContactInfoDto = accountsContactInfoDto;
    }

    @Operation(summary = "Create Account",
               description = "POST API to create a new Account")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @ApiResponse(responseCode = "500",
                 description = "HTTP Status Internal Server Error",
                 content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody final CustomerDto customerDto) {
        this.accountsService.createAccount(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    @Operation(summary = "Fetch Account",
               description = "GET API to fetch an Account")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @ApiResponse(responseCode = "500",
                 description = "HTTP Status Internal Server Error",
                 content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam @Pattern(regexp = "(^$|[0-9]{10})",
                                                                                  message = "Mobile number must be 10 digits") final String mobileNumber) {
        final CustomerDto customerDto = this.accountsService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(customerDto);
    }

    @Operation(summary = "Update Account",
               description = "PUT API to update an Account")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @ApiResponse(responseCode = "417",
                 description = "Expectation Failed")
    @ApiResponse(responseCode = "500",
                 description = "HTTP Status Internal Server Error",
                 content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody final CustomerDto customerDto) {
        final boolean isUpdated = this.accountsService.updateAccount(customerDto);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                             .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
    }

    @Operation(summary = "Delete Account",
               description = "DELETE API to delete an Account")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @ApiResponse(responseCode = "417",
                 description = "Expectation Failed")
    @ApiResponse(responseCode = "500",
                 description = "HTTP Status Internal Server Error",
                 content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam @Pattern(regexp = "(^$|[0-9]{10})",
                                                                                   message = "Mobile number must be 10 digits") final String mobileNumber) {
        final boolean isDeleted = this.accountsService.deleteAccount(mobileNumber);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                             .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
    }

    @Operation(summary = "Get Build Information",
               description = "GET API to Get Build Information")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @Retry(name = "getBuildInfo", fallbackMethod = "getBuildInfoFallback")
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        logger.debug("getBuildInfo invoked");
        return ResponseEntity.status(HttpStatus.OK).body(this.buildVersion);
    }

    public ResponseEntity<String> getBuildInfoFallback(final Throwable throwable) {
        logger.debug("getBuildInfoFallback invoked: {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body("v1.0");
    }

    @Operation(summary = "Get Java Version",
               description = "GET API to Get Java Version")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @RateLimiter(name = "getJavaVersion", fallbackMethod = "getJavaVersionFallback")
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(this.environment.getProperty("JAVA_HOME"));
    }

    public ResponseEntity<String> getJavaVersionFallback(final Throwable throwable) {
        logger.debug("getJavaVersionFallback invoked: {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                             .body("Java 17");
    }

    @Operation(summary = "Get Contact Info",
               description = "GET API to Get Contact Info")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(this.accountsContactInfoDto);
    }

}
