package ru.andryss.antalk.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class TransactionConfig {

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager, transactionDefinitionBase());
    }

    @Bean
    public TransactionTemplate readOnlyTransactionTemplate(PlatformTransactionManager transactionManager) {
        DefaultTransactionDefinition transactionDefinition = transactionDefinitionBase();
        transactionDefinition.setReadOnly(true);

        return new TransactionTemplate(transactionManager, transactionDefinition);
    }

    private static DefaultTransactionDefinition transactionDefinitionBase() {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionDefinition;
    }
}
