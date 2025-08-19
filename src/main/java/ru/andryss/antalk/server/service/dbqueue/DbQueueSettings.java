package ru.andryss.antalk.server.service.dbqueue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.yoomoney.tech.dbqueue.settings.FailRetryType;
import ru.yoomoney.tech.dbqueue.settings.FailureSettings;
import ru.yoomoney.tech.dbqueue.settings.PollSettings;
import ru.yoomoney.tech.dbqueue.settings.ProcessingMode;
import ru.yoomoney.tech.dbqueue.settings.ReenqueueRetryType;
import ru.yoomoney.tech.dbqueue.settings.ReenqueueSettings;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbQueueSettings {
    /**
     * Название очереди (UPPER_CASE_EXAMPLE)
     */
    String value();

    // Processing settings

    /**
     * Режим обработки задач в рамках одного потока
     *
     * @see ProcessingMode
     */
    ProcessingMode processingMode() default ProcessingMode.SEPARATE_TRANSACTIONS;

    /**
     * Количество потоков обработки очереди в одной jvm
     */
    int threadCount() default 1;

    // Poll settings

    /**
     * Время задержки (в секундах) после выполнения задачи перед попыткой найти и обработать следующую
     *
     * @see PollSettings#getBetweenTaskTimeout()
     */
    int betweenTasksTimeout() default 0;

    /**
     * Время задержки (в секундах) перед новой попыткой найти задачу после ненахождения задачи
     *
     * @see PollSettings#getNoTaskTimeout()
     */
    int noTaskTimeout() default 10;

    /**
     * Время задержки выполнения (в секундах) после получения неожиданной ошибки во время исполнения
     *
     * @see PollSettings#getFatalCrashTimeout()
     */
    int fatalCrashTimeout() default 1;

    // Failure settings

    /**
     * Способ расчета времени следующей попытки выполнения задачи
     *
     * @see FailRetryType
     * @see FailureSettings#getRetryType()
     */
    FailRetryType failRetryType() default FailRetryType.GEOMETRIC_BACKOFF;

    /**
     * Начальное время (в секундах), на которое откладывается задача на случай, если она будет выполнена неуспешно
     *
     * @see FailureSettings#getRetryInterval()
     */
    int failInitialDelay() default 60;

    // Reenqueue settings

    /**
     * Стратегия расчета времени следующей попытки выполнения задачи
     *
     * @see ReenqueueRetryType
     * @see ReenqueueSettings#getRetryType()
     */
    ReenqueueRetryType retryType() default ReenqueueRetryType.MANUAL;

    /**
     * Для стратегии ReenqueueRetryType.SEQUENTIAL.
     * Последовательность задержек (в секундах) между попытками выполнения задачи
     *
     * @see ReenqueueSettings#getSequentialPlanOrThrow()
     */
    int[] sequentialPlan() default {60};

    /**
     * Для стратегии ReenqueueRetryType.FIXED.
     * Постоянный интервал (в секундах) между попытками выполнения задачи
     *
     * @see ReenqueueSettings#getFixedDelayOrThrow()
     */
    int fixedDelay() default 60;

    /**
     * Для стратегии ReenqueueRetryType.ARITHMETIC или ReenqueueRetryType.GEOMETRIC.
     * Задержка (в секундах) после первой попытки выполнения задачи
     *
     * @see ReenqueueSettings#getInitialDelayOrThrow()
     */
    int initialDelay() default 60;

    /**
     * Для стратегии ReenqueueRetryType.ARITHMETIC.
     * Шаг арифметической прогрессии задержек (в секундах)
     *
     * @see ReenqueueSettings#getArithmeticStepOrThrow()
     */
    int arithmeticStep() default 60;

    /**
     * Для стратегии ReenqueueRetryType.GEOMETRIC.
     * Множитель геометрической прогрессии задержек
     *
     * @see ReenqueueSettings#getGeometricRatioOrThrow()
     */
    long geometricRatio() default 2;
}
