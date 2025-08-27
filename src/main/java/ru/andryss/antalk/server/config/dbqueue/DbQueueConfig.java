package ru.andryss.antalk.server.config.dbqueue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ClassUtils;
import ru.andryss.antalk.server.config.requestid.DbQueueTaskRequestIdAssignListener;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;
import ru.andryss.antalk.server.service.dbqueue.DbQueueProcessor;
import ru.andryss.antalk.server.service.dbqueue.DbQueueSettings;
import ru.andryss.antalk.server.service.dbqueue.QueuePayload;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.config.DatabaseDialect;
import ru.yoomoney.tech.dbqueue.config.QueueService;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.config.QueueShardId;
import ru.yoomoney.tech.dbqueue.config.QueueTableSchema;
import ru.yoomoney.tech.dbqueue.config.impl.CompositeTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingThreadLifecycleListener;
import ru.yoomoney.tech.dbqueue.settings.ExtSettings;
import ru.yoomoney.tech.dbqueue.settings.FailureSettings;
import ru.yoomoney.tech.dbqueue.settings.PollSettings;
import ru.yoomoney.tech.dbqueue.settings.ProcessingSettings;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.settings.QueueId;
import ru.yoomoney.tech.dbqueue.settings.QueueLocation;
import ru.yoomoney.tech.dbqueue.settings.QueueSettings;
import ru.yoomoney.tech.dbqueue.settings.ReenqueueSettings;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

@Slf4j
@Configuration
public class DbQueueConfig {

    @Bean(initMethod = "start")
    public QueueService queueService(
            QueueShard<SpringDatabaseAccessLayer> queueShard,
            ObjectMapperWrapper objectMapper,
            List<? extends QueueConsumerBase<?>> consumers
    ) {
        QueueService queueService = new QueueService(
                List.of(queueShard),
                new LoggingThreadLifecycleListener(),
                new CompositeTaskLifecycleListener(List.of(
                        new DbQueueTaskRequestIdAssignListener(objectMapper),
                        new LoggingTaskLifecycleListener()
                ))
        );

        consumers.forEach(consumer -> {
            log.info("Registering consumer {}", consumer);
            queueService.registerQueue(consumer);
        });

        return queueService;
    }

    @Bean
    public QueueShard<SpringDatabaseAccessLayer> queueShard(
            DataSource dataSource,
            TransactionTemplate transactionTemplate,
            DbQueueProperties properties
    ) {
        return new QueueShard<>(
                new QueueShardId(properties.getShardId()),
                new SpringDatabaseAccessLayer(
                        DatabaseDialect.POSTGRESQL,
                        QueueTableSchema.builder().build(),
                        new JdbcTemplate(dataSource),
                        transactionTemplate
                )
        );
    }

    @Bean
    @SuppressWarnings("unchecked")
    public List<? extends QueueConsumerBase<?>> consumers(
            List<? extends DbQueueProcessor<?>> processors,
            ObjectMapperWrapper objectMapper,
            DbQueueProperties properties
    ) {
        return processors.stream()
                .map(processor -> consumer(
                        (DbQueueProcessor<QueuePayload>) processor,
                        objectMapper,
                        properties
                ))
                .toList();
    }

    private <P extends QueuePayload> QueueConsumerBase<P> consumer(
            DbQueueProcessor<P> processor,
            ObjectMapperWrapper objectMapper,
            DbQueueProperties properties
    ) {
        DbQueueSettings settings = extractSettings(processor);
        return new QueueConsumerBase<>(
                queueConfig(settings, properties),
                createPayloadTransformer(objectMapper, processor),
                processor
        );
    }

    @Bean
    @SuppressWarnings("unchecked")
    public List<? extends QueueProducerBase<?>> producers(
            List<? extends DbQueueProcessor<?>> processors,
            ObjectMapperWrapper objectMapper,
            DbQueueProperties properties,
            QueueShard<SpringDatabaseAccessLayer> queueShard
    ) {
        return processors.stream()
                .map(processor -> producer(
                        (DbQueueProcessor<QueuePayload>) processor,
                        objectMapper,
                        properties,
                        queueShard
                ))
                .toList();
    }

    private <P extends QueuePayload> QueueProducerBase<P> producer(
            DbQueueProcessor<P> processor,
            ObjectMapperWrapper objectMapper,
            DbQueueProperties properties,
            QueueShard<SpringDatabaseAccessLayer> queueShard
    ) {
        DbQueueSettings settings = extractSettings(processor);
        return new QueueProducerBase<>(
                queueConfig(settings, properties),
                createPayloadTransformer(objectMapper, processor),
                processor,
                queueShard
        );
    }

    private QueueConfig queueConfig(DbQueueSettings settings, DbQueueProperties properties) {
        int threadCount = (
                Boolean.TRUE.equals(properties.isProcessingEnabled()) ? settings.threadCount() : 0
        );
        return new QueueConfig(
                QueueLocation.builder()
                        .withTableName(properties.getTableName())
                        .withQueueId(new QueueId(settings.value()))
                        .build(),
                QueueSettings.builder()
                        .withProcessingSettings(
                                ProcessingSettings.builder()
                                        .withProcessingMode(settings.processingMode())
                                        .withThreadCount(threadCount)
                                        .build()
                        )
                        .withPollSettings(
                                PollSettings.builder()
                                        .withBetweenTaskTimeout(Duration.ofSeconds(settings.betweenTasksTimeout()))
                                        .withNoTaskTimeout(Duration.ofSeconds(settings.noTaskTimeout()))
                                        .withFatalCrashTimeout(Duration.ofSeconds(settings.fatalCrashTimeout()))
                                        .build()
                        )
                        .withFailureSettings(
                                FailureSettings.builder()
                                        .withRetryType(settings.failRetryType())
                                        .withRetryInterval(Duration.ofSeconds(settings.failInitialDelay()))
                                        .build()
                        )
                        .withReenqueueSettings(
                                ReenqueueSettings.builder()
                                        .withRetryType(settings.retryType())
                                        .withSequentialPlan(
                                                Arrays.stream(settings.sequentialPlan())
                                                        .mapToObj(Duration::ofSeconds)
                                                        .toList())
                                        .withFixedDelay(Duration.ofSeconds(settings.fixedDelay()))
                                        .withInitialDelay(Duration.ofSeconds(settings.initialDelay()))
                                        .withArithmeticStep(Duration.ofSeconds(settings.arithmeticStep()))
                                        .withGeometricRatio(settings.geometricRatio())
                                        .build()
                        )
                        .withExtSettings(
                                ExtSettings.builder()
                                        .withSettings(Map.of())
                                        .build()
                        )
                        .build()
        );
    }

    @SuppressWarnings("unchecked")
    private <P extends QueuePayload> TaskPayloadTransformer<P> createPayloadTransformer(
            ObjectMapperWrapper objectMapper,
            DbQueueProcessor<P> processor
    ) {
        Class<P> payloadClass = getPayloadClass((Class<? extends DbQueueProcessor<P>>) processor.getClass());
        return new TaskPayloadTransformer<>() {
            @Override
            public P toObject(String s) {
                return objectMapper.readValue(s, payloadClass);
            }

            @Override
            public String fromObject(P p) {
                return objectMapper.writeValueAsString(p);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <P extends QueuePayload> Class<P> getPayloadClass(Class<? extends DbQueueProcessor<P>> processorClass) {
        Type[] genericInterfaces = ClassUtils.getUserClass(processorClass).getGenericInterfaces();
        try {
            return (Class<P>) this.getClass().getClassLoader().loadClass(
                    Arrays.stream(genericInterfaces)
                            .map(ParameterizedType.class::cast)
                            .filter(i -> i.getRawType().equals(DbQueueProcessor.class))
                            .findFirst()
                            .orElseThrow()
                            .getActualTypeArguments()[0]
                            .getTypeName()
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ConstantValue")
    private <P extends QueuePayload> DbQueueSettings extractSettings(DbQueueProcessor<P> processor) {
        DbQueueSettings settings = AnnotationUtils.findAnnotation(
                processor.getClass(),
                DbQueueSettings.class
        );
        if (settings == null) {
            throw new IllegalStateException(processor.getClass() + " must have DbQueueSettings annotation");
        }
        return settings;
    }
}
