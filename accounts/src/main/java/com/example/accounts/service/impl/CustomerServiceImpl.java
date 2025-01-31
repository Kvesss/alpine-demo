package com.example.accounts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.accounts.dto.AccountsDto;
import com.example.accounts.dto.CustomerDetailsDto;
import com.example.accounts.entity.Accounts;
import com.example.accounts.entity.Customer;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.mapper.AccountsMapper;
import com.example.accounts.mapper.CustomerMapper;
import com.example.accounts.repository.AccountsRepository;
import com.example.accounts.repository.CustomerRepository;
import com.example.accounts.service.CustomerService;
import com.example.accounts.service.client.CardsFeignClient;
import com.example.accounts.service.client.LoansFeignClient;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private AccountsRepository accountsRepository;

    private CustomerRepository customerRepository;

    private CardsFeignClient cardsFeignClient;

    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(@RequestHeader("alpine-correlation-id") final String correlationId, final String mobileNumber) {
        final Customer customer = this.customerRepository.findByMobileNumber(mobileNumber)
                                                         .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        final Accounts accounts = this.accountsRepository.findByCustomerId(customer.getCustomerId())
                                                         .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));
        final CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        customerDetailsDto.setCardsDto(this.cardsFeignClient.fetchCardDetails(mobileNumber).getBody());
        customerDetailsDto.setLoansDto(this.loansFeignClient.fetchLoanDetails(mobileNumber).getBody());
        return customerDetailsDto;
    }

}
