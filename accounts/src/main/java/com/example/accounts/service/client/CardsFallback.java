package com.example.accounts.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.accounts.dto.CardsDto;

@Component
public class CardsFallback implements CardsFeignClient {

    @Override
    public ResponseEntity<CardsDto> fetchCardDetails(final String mobileNumber) {
        return ResponseEntity.ofNullable(new CardsDto());
    }

}
