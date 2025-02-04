package com.example.accounts.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.accounts.dto.LoansDto;

@Component
public class LoansFallback implements LoansFeignClient{

    @Override
    public ResponseEntity<LoansDto> fetchLoanDetails(final String mobileNumber) {
        return ResponseEntity.ofNullable(new LoansDto());
    }

}
