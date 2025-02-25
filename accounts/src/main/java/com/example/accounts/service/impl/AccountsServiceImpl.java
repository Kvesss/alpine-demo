package com.example.accounts.service.impl;

import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.example.accounts.constants.AccountsConstants;
import com.example.accounts.dto.AccountsDto;
import com.example.accounts.dto.AccountsMessageDto;
import com.example.accounts.dto.CustomerDto;
import com.example.accounts.entity.Accounts;
import com.example.accounts.entity.Customer;
import com.example.accounts.exception.CustomerAlreadyExistsException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.mapper.AccountsMapper;
import com.example.accounts.mapper.CustomerMapper;
import com.example.accounts.repository.AccountsRepository;
import com.example.accounts.repository.CustomerRepository;
import com.example.accounts.service.AccountsService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements AccountsService {

    private static final Logger log = LoggerFactory.getLogger(AccountsServiceImpl.class);

    private AccountsRepository accountsRepository;

    private CustomerRepository customerRepository;

    private final StreamBridge streamBridge;

    @Override
    public void createAccount(final CustomerDto customerDto) {
        final Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        final Optional<Customer> duplicateCustomer = this.customerRepository.findByMobileNumber(customer.getMobileNumber());
        if (duplicateCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given number: " + duplicateCustomer.get().getMobileNumber());
        }
        final Customer savedCustomer = this.customerRepository.save(customer);
        final Accounts savedAccount = this.accountsRepository.save(this.createNewAccount(savedCustomer));
        this.sendCommunication(savedAccount, savedCustomer);
    }

    @Override
    public CustomerDto fetchAccount(final String mobileNumber) {
        final Customer customer = this.customerRepository.findByMobileNumber(mobileNumber)
                                                         .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        final Accounts accounts = this.accountsRepository.findByCustomerId(customer.getCustomerId())
                                                                   .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));
        final CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(final CustomerDto customerDto) {
        boolean isUpdated = false;
        final AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto !=null ){
            Accounts accounts = this.accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
              () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = this.accountsRepository.save(accounts);

            final Long customerId = accounts.getCustomerId();
            final Customer customer = this.customerRepository.findById(customerId).orElseThrow(
              () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            this.customerRepository.save(customer);
            isUpdated = true;
        }
        return  isUpdated;
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(final Customer customer) {
        final Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    private void sendCommunication(final Accounts account, final Customer customer) {
        final var accountsMsgDto = new AccountsMessageDto(account.getAccountNumber(), customer.getName(),
                                                          customer.getEmail(), customer.getMobileNumber());
        log.info("Sending Communication request for the details: {}", accountsMsgDto);
        final var result = this.streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
    }

    /**
     *
     * @param mobileNumber
     * @return
     */
    @Override
    @Transactional
    @Modifying
    public boolean deleteAccount(final String mobileNumber) {
        final Customer customer = this.customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
          () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        this.accountsRepository.deleteByCustomerId(customer.getCustomerId());
        this.customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    /**
     * @param accountNumber - Long
     * @return boolean indicating if the update of communication status is successful or not
     */
    @Override
    public boolean updateCommunicationStatus(final Long accountNumber) {
        boolean isUpdated = false;
        if(accountNumber !=null ){
            final Accounts accounts = this.accountsRepository.findById(accountNumber).orElseThrow(
              () -> new ResourceNotFoundException("Account", "AccountNumber", accountNumber.toString())
            );
            accounts.setCommunicationSw(true);
            this.accountsRepository.save(accounts);
            isUpdated = true;
        }
        return  isUpdated;
    }

}
