package com.example.demodatn.service;

import com.example.demodatn.domain.DeleteTransactionDomain;
import com.example.demodatn.entity.TransactionEntity;
import com.example.demodatn.entity.TransactionItemEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.FoodRepository;
import com.example.demodatn.repository.TransactionItemRepository;
import com.example.demodatn.repository.TransactionRepository;
import com.example.demodatn.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Transactional
    public void deleteUserTransaction(DeleteTransactionDomain domain){
        Long transactionId = StringUtils.convertObjectToLongOrNull(domain.getTransactionId());
        if (transactionId == null){
            throw new CustomException("Transaction id bi sai", "Transaction id bi sai", HttpStatus.BAD_REQUEST);
        }
        TransactionEntity transactionEntity = transactionRepository.findById(transactionId).or(() -> {throw new CustomException("tim transaction bi sai", "tim transaction bi sai", HttpStatus.BAD_REQUEST);}).get();
        List<TransactionItemEntity> listTransactionItem = transactionItemRepository.findAllByTransactionId(transactionId);

        if (CollectionUtils.isEmpty(listTransactionItem)){
            throw new CustomException("ko tim thay list transaciton", "ko tim thay list transaction", HttpStatus.BAD_REQUEST);
        }
        List<TransactionItemEntity> listDeleteTransaction = new ArrayList<>();

        for (TransactionItemEntity transactionItemEntity :listTransactionItem){
            transactionItemEntity.setIsDeleted(1);
            listDeleteTransaction.add(transactionItemEntity);
        }

        transactionItemRepository.saveAll(listDeleteTransaction);
        transactionEntity.setIsDeleted(1);

        transactionRepository.save(transactionEntity);

    }
}
