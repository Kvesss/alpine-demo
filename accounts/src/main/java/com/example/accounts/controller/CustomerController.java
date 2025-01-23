package com.example.accounts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.accounts.dto.CustomerDetailsDto;
import com.example.accounts.dto.ErrorResponseDto;
import com.example.accounts.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;

@Tag(name = "CRUD REST APIs for Customers",
     description = "Extra description text about REST Controller")
@RestController
@RequestMapping(path = "/api/v1/customers",
                produces = { MediaType.APPLICATION_JSON_VALUE })
@Validated
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Fetch Account",
               description = "GET API to fetch an Account")
    @ApiResponse(responseCode = "200",
                 description = "HTTP Status OK")
    @ApiResponse(responseCode = "500",
                 description = "HTTP Status Internal Server Error",
                 content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(@RequestParam @Pattern(regexp = "(^$|[0-9]{10})",
                                                                                  message = "Mobile number must be 10 digits") final String mobileNumber) {
        final CustomerDetailsDto customerDetailsDto = this.customerService.fetchCustomerDetails(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(customerDetailsDto);
    }
}
