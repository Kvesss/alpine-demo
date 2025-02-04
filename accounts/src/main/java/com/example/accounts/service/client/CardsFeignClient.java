package com.example.accounts.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.accounts.dto.CardsDto;

@FeignClient(value = "cards",
             fallback = CardsFallback.class)
public interface CardsFeignClient {

    @GetMapping(value = "/api/v1/cards/fetch", consumes = "application/json")
    ResponseEntity<CardsDto> fetchCardDetails(@RequestParam String mobileNumber);
}
