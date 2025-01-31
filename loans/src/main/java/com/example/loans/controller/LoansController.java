package com.example.loans.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.loans.constants.LoansConstants;
import com.example.loans.dto.ErrorResponseDto;
import com.example.loans.dto.LoansContactInfoDto;
import com.example.loans.dto.LoansDto;
import com.example.loans.dto.ResponseDto;
import com.example.loans.service.LoansService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Tag(
  name = "CRUD REST APIs for Loans in the Bank",
  description = "CRUD REST APIs in the Bank to CREATE, UPDATE, FETCH AND DELETE loan details"
)
@RestController
@RequestMapping(path = "/api/v1/loans", produces = { MediaType.APPLICATION_JSON_VALUE})
@Validated
public class LoansController {

    private static final Logger logger = LoggerFactory.getLogger(LoansController.class);

    private final LoansService loansService;

    @Value("${build.version}")
    private String buildVersion;

    private final Environment environment;

    private final LoansContactInfoDto cardsContactInfoDto;

    public LoansController(final Environment environment, final LoansContactInfoDto cardsContactInfoDto, final LoansService loansService) {
        this.environment = environment;
        this.cardsContactInfoDto = cardsContactInfoDto;
        this.loansService = loansService;
    }

    @Operation(
      summary = "Create Loan REST API",
      description = "REST API to create new loan inside the Bank"
    )
      @ApiResponse(
        responseCode = "201",
        description = "HTTP Status CREATED"
      )
      @ApiResponse(
        responseCode = "500",
        description = "HTTP Status Internal Server Error",
        content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
        )
      )
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLoan(@RequestParam
    @Pattern(regexp="(^$|[0-9]{10})", message = "Mobile number must be 10 digits") final String mobileNumber) {
        this.loansService.createLoan(mobileNumber);
        return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(new ResponseDto(LoansConstants.STATUS_201, LoansConstants.MESSAGE_201));
    }

    @Operation(
      summary = "Fetch Loan Details REST API",
      description = "REST API to fetch loan details based on a mobile number"
    )
      @ApiResponse(
        responseCode = "200",
        description = "HTTP Status OK"
      )
      @ApiResponse(
        responseCode = "500",
        description = "HTTP Status Internal Server Error",
        content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
        )
      )
    @GetMapping("/fetch")
    public ResponseEntity<LoansDto> fetchLoanDetails(@RequestHeader("alpine-correlation-id") final String correlationId, @RequestParam
    @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits") final String mobileNumber) {
        logger.debug("alpine correlation id: {}", correlationId);
        final LoansDto loansDto = this.loansService.fetchLoan(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(loansDto);
    }

    @Operation(
      summary = "Update Loan Details REST API",
      description = "REST API to update loan details based on a loan number"
    )
      @ApiResponse(
        responseCode = "200",
        description = "HTTP Status OK"
      )
      @ApiResponse(
        responseCode = "417",
        description = "Expectation Failed"
      )
      @ApiResponse(
        responseCode = "500",
        description = "HTTP Status Internal Server Error",
        content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
        )
      )
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLoanDetails(@Valid @RequestBody final LoansDto loansDto) {
        final boolean isUpdated = this.loansService.updateLoan(loansDto);
        if(isUpdated) {
            return ResponseEntity
                     .status(HttpStatus.OK)
                     .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                     .status(HttpStatus.EXPECTATION_FAILED)
                     .body(new ResponseDto(LoansConstants.STATUS_417, LoansConstants.MESSAGE_417_UPDATE));
        }
    }

    @Operation(
      summary = "Delete Loan Details REST API",
      description = "REST API to delete Loan details based on a mobile number"
    )
      @ApiResponse(
        responseCode = "200",
        description = "HTTP Status OK"
      )
      @ApiResponse(
        responseCode = "417",
        description = "Expectation Failed"
      )
      @ApiResponse(
        responseCode = "500",
        description = "HTTP Status Internal Server Error",
        content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
        )
      )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteLoanDetails(@RequestParam
    @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits") final String mobileNumber) {
        final boolean isDeleted = this.loansService.deleteLoan(mobileNumber);
        if(isDeleted) {
            return ResponseEntity
                     .status(HttpStatus.OK)
                     .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                     .status(HttpStatus.EXPECTATION_FAILED)
                     .body(new ResponseDto(LoansConstants.STATUS_417, LoansConstants.MESSAGE_417_DELETE));
        }
    }

    @Operation(summary = "Get Build Information",
               description = "GET API to Get Build Information")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(this.buildVersion);
    }

    @Operation(summary = "Get Java Version",
               description = "GET API to Get Java Version")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        final String javaVersion = this.environment.getProperty("JAVA_HOME");
        return ResponseEntity.status(HttpStatus.OK).body(javaVersion);
    }

    @Operation(summary = "Get Contact Info",
               description = "GET API to Get Contact Info")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @GetMapping("/contact-info")
    public ResponseEntity<LoansContactInfoDto> getContactInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(this.cardsContactInfoDto);
    }

}