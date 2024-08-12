package com.example.loans.service.impl;

import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.loans.constants.LoansConstants;
import com.example.loans.dto.LoansDto;
import com.example.loans.entity.Loans;
import com.example.loans.exception.LoanAlreadyExistsException;
import com.example.loans.exception.ResourceNotFoundException;
import com.example.loans.mapper.LoansMapper;
import com.example.loans.repository.LoansRepository;
import com.example.loans.service.LoansService;

import jakarta.transaction.Transactional;

@Service
public class LoansServiceImpl implements LoansService {

    private final LoansRepository loansRepository;

    public LoansServiceImpl(final LoansRepository loansRepository) {
        this.loansRepository = loansRepository;
    }

    @Transactional
    @Override
    public void createLoan(final String mobileNumber) {
        final Optional<Loans> loan = this.loansRepository.findByMobileNumber(mobileNumber);
        if(loan.isPresent()) {
            throw new LoanAlreadyExistsException("Loan already registered with given number " + mobileNumber);
        }
        this.loansRepository.save(this.createNewLoan(mobileNumber));
    }

    private Loans createNewLoan(final String mobileNumber) {
        final Loans newLoan = new Loans();
        final long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(LoansConstants.HOME_LOAN);
        newLoan.setTotalLoan(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        return newLoan;
    }

    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
          () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        return LoansMapper.mapToLoansDto(loans, new LoansDto());
    }

    @Override
    public boolean updateLoan(final LoansDto loansDto) {
        return false;
    }

    @Override
    public boolean deleteLoan(final String mobileNumber) {
        return false;
    }

}
