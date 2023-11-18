package ru.nsu.ccfit.ooad.nsumediabot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import ru.nsu.ccfit.ooad.nsumediabot.auth.activation.service.ActivationTokenService;

import java.time.Duration;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class Scheduler
        implements SchedulingConfigurer {

    private final ActivationTokenService activationTokenService;

    @Value("${activation.token.time-to-live}")
    private Long fixedDelay;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
        taskRegistrar.addFixedDelayTask(
                () -> {
                    log.info("Start deleting expired tokens");
                    activationTokenService.deleteExpiredTokens();
                },
                Duration.ofMillis(fixedDelay)
        );
    }
}
