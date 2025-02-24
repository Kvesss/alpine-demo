package com.example.message.dto;

public record AccountsMessageDto(Long accountNumber, String name, String email, String mobileNumber) {

    @Override
    public String toString() {
        return "AccountsMessageDto{"
               + "accountNumber="
               + this.accountNumber
               + ", name='" + this.name + '\''
               + ", email='" + this.email + '\''
               + ", mobileNumber='"
               + this.mobileNumber + '\'' + '}';
    }

}
