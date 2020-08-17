package com.capg.pbms.passbook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.capg.pbms.passbook.model.Transaction;
@Service
public class PassbookServiceImpl implements IPassbookService {
@Autowired
private RestTemplate restTemplate;
	@Override
	public List<Transaction> getAllTransactions() {
		ResponseEntity<List<Transaction>> passbook=restTemplate.exchange("http://localhost:8888/transaction/", HttpMethod.GET,null,new ParameterizedTypeReference<List<Transaction>>() {
		});
		return passbook.getBody();
	}
	@Override
	public Transaction getTransactionByTransactionId(int transactionId) {
		// TODO Auto-generated method stub
		Transaction transaction=restTemplate.getForObject("http://localhost:8888/trans/"+transactionId,Transaction.class);
		
		return transaction;
	}

}
