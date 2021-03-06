package com.capg.pbms.transaction.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.capg.pbms.transaction.exception.ChequeBounceException;
import com.capg.pbms.transaction.exception.InsufficienBalanceException;
import com.capg.pbms.transaction.exception.InvaildAccountException;

import com.capg.pbms.transaction.model.BankAccountDetails;
import com.capg.pbms.transaction.model.Transaction;
import com.capg.pbms.transaction.repo.TransactionRepo;

@Service
public class TransactionService implements ITransactionService {

	@Autowired
	TransactionRepo transactionRepo;
	@Autowired
	RestTemplate rest;

	@Override
	public Transaction debitUsingSlip(long accNumber, double amount, Transaction transaction)
			throws InvaildAccountException, InsufficienBalanceException {
		
		/*
		 * if (!transactionRepo.existsById(accNumber)) { throw new
		 * InvaildAccountException("accountNumber doesn't exists"); }
		 */
		 
		BankAccountDetails bankDetails = rest.getForObject("http://localhost:8100/pecuniabank/get/accNum/" + accNumber,
				BankAccountDetails.class);

		transaction.setTransAccountNumber(bankDetails.getAccNumber());

		if (amount < 100 || amount > transaction.getCurrentBalance()) {
			throw new InsufficienBalanceException("Amount should be more than 100 and less than current balance");
		}

		transaction.setTransClosingBalance(transaction.getCurrentBalance() - amount);

		transaction.setTransactionAmount(transaction.getCurrentBalance() - transaction.getTransClosingBalance());

		transaction.setTransactionDate(LocalDateTime.now());
		return transactionRepo.save(transaction);
	}

	@Override
	public Transaction creditUsingSlip(long accNumber, double amount, Transaction transaction)
			throws InvaildAccountException, InsufficienBalanceException {
		if (!transactionRepo.existsById(accNumber)) {
			throw new InvaildAccountException("accountNumber doesn't exists");
		}

		BankAccountDetails bankDetails = rest.getForObject("http://localhost:8100/pecuniabank/get/accNum/" + accNumber,
				BankAccountDetails.class);

		transaction.setTransAccountNumber(bankDetails.getAccNumber());
		
		if (amount > 100000) {
			throw new InsufficienBalanceException("amount should be less than 1 lakh");
		}

		transaction.setTransClosingBalance(transaction.getCurrentBalance() + amount);
		transaction.setTransactionAmount(transaction.getTransClosingBalance() - transaction.getCurrentBalance());
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setTransAccountNumber(transaction.getTransAccountNumber());
		return transactionRepo.save(transaction);
	}

	@Override
	public Transaction creditUsingCheque(long accNumber, double amount, Transaction transaction)
			throws InvaildAccountException {
		
		  if (!transactionRepo.existsById(accNumber)) { throw new
		 InvaildAccountException("accountNumber doesn't exists"); }
		 

		BankAccountDetails bankDetails = rest.getForObject("http://localhost:8100/pecuniabank/get/accNum/" + accNumber,
				BankAccountDetails.class);

		transaction.setTransAccountNumber(bankDetails.getAccNumber());

		// transaction.setTransAccountNumber(bank.getAccNumber());
		transaction.getChequeDetails().setDebitAccNum(Long.parseLong((String.valueOf(Math.abs(new Random().nextLong())).substring(0, 12))));
		transaction.getChequeDetails().setChequeIssueDate(LocalDateTime.now());
		transaction.getChequeDetails().setChequeAmount(amount);
		transaction.getChequeDetails().setChequeClosingBalance(
				transaction.getChequeDetails().getCurrentBalance() + transaction.getChequeDetails().getChequeAmount());
		return transactionRepo.save(transaction);

	}

	@Override
	public List<Transaction> getAllTransaction() {

		return transactionRepo.findAll();
	}

	@Override
	public Transaction debitUsingCheque(long accNumber, double amount, Transaction transaction)
			throws InvaildAccountException, ChequeBounceException {
		if (!transactionRepo.existsById(accNumber)) {
			throw new InvaildAccountException("accountNumber doesn't exists");
		}
		BankAccountDetails bankDetails = rest.getForObject("http://localhost:8100/pecuniabank/get/accNum/" + accNumber,
				BankAccountDetails.class);

		//transaction.setTransAccountNumber(bankDetails.getAccNumber());
		transaction.getChequeDetails().setChequeIssueDate(LocalDateTime.now());
		if (amount > 100000) {
			throw new ChequeBounceException("Amount should be less than 1 lakh");
		}
		//transaction.getChequeDetails().setDebitAccNum(Long.parseLong((String.valueOf(Math.abs(new Random().nextLong())).substring(0, 12))));
		
		transaction.getChequeDetails().setChequeAmount(amount);
		transaction.getChequeDetails().setChequeClosingBalance(
				transaction.getChequeDetails().getCurrentBalance() - transaction.getChequeDetails().getChequeAmount());
		return transactionRepo.save(transaction);

	}

	public Transaction findByTransactionId(int transactionId) throws InvaildAccountException {
		Transaction transaction=transactionRepo.findByTransactionId(transactionId);
		if(transaction==null) {
			throw new InvaildAccountException("transactionId not found");
		}
		return transaction;
		
	}
	
	
}
