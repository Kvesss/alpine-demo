package com.example.accounts.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.accounts.service.AccountsService;

import java.util.function.Consumer;

@Configuration
public class AccountsFunctions {

    private static final Logger log = LoggerFactory.getLogger(AccountsFunctions.class);

    @Bean
    public Consumer<Long> updateCommunication(final AccountsService accountsService) {
        return accountNumber -> {
            log.info("Updating Communication status for the account number : {}", accountNumber);
            accountsService.updateCommunicationStatus(accountNumber);
        };
    }
}
