package ru.nsu.ccfit.ooad.nsumediabot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.toggle.BareboneToggle;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.ccfit.ooad.nsumediabot.auth.service.AuthService;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.UserService;
import ru.nsu.ccfit.ooad.nsumediabot.bot.ability.*;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.MaterialService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SpecializationService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SubjectService;

import java.util.concurrent.Executor;

@Component
public class NsumediaBot
        extends AbilityBot {

    @Value("${telegram.bot.creator-id}")
    private Long creatorId;

    private final Executor threadPool;

    @Autowired
    public NsumediaBot(@Value("${telegram.bot.token}") String token,
            @Value("${telegram.bot.name}") String name,
            @Value("${telegram.bot.thread-pool.size}") Integer numThread,
            UserService userService,
            AuthService authService,
            SpecializationService specializationService,
            SubjectService subjectService,
            MaterialService materialService) {
        super(token, name, new BareboneToggle());

        addExtensions(
                new AllUserRoleAbility(this, specializationService, subjectService, materialService),
                new UnregisteredUserRoleAbility(this, userService, authService),
                new UnactivatedUserRoleAbility(this, userService, authService),
                new RegisteredUserRoleAbility(this, userService, specializationService, subjectService),
                new AdminRoleAbility(this, userService, specializationService, subjectService, materialService)
        );

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(numThread);
        threadPoolTaskExecutor.setCorePoolSize(numThread);
        threadPoolTaskExecutor.setThreadNamePrefix("telegram-");
        threadPoolTaskExecutor.initialize();
        this.threadPool = threadPoolTaskExecutor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        threadPool.execute(() -> super.onUpdateReceived(update));
    }

    @Override
    public long creatorId() {
        return creatorId;
    }
}
