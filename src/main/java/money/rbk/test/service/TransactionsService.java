package money.rbk.test.service;

import money.rbk.test.entity.TransactionEntity;
import money.rbk.test.repository.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionsService {

    @Autowired
    private TransactionsRepository transactionsRepository;

    public TransactionEntity getWithId(Long id) {
        return transactionsRepository.findById(id).orElse(null);
    }
}
