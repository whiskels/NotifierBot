package com.whiskels.notifier.telegram.security;

import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.telegram.domain.Role.UNAUTHORIZED;

@Slf4j
@Aspect
@Configuration
@EnableAspectJAutoProxy
public class SecuredAspect {
    private final SendMessageCreationEventPublisher publisher;
    private final String adminChatId;

    public SecuredAspect(SendMessageCreationEventPublisher publisher,
                         @Value("${telegram.bot.admin}") String adminChatId) {
        this.publisher = publisher;
        this.adminChatId = adminChatId;
    }

    @Pointcut(value = "@annotation(secured) "
            + "&& args(user,..)"
            + "&& execution(* *(..))")
    private void securedCalledBy(User user, Secured secured) {
    }

    @Around(value = "securedCalledBy(user, secured))", argNames = "joinPoint, user, secured")
    private Object authorizeUser(ProceedingJoinPoint joinPoint, User user, Secured secured) throws Throwable {
        if (!isAuthorized(user, List.of(secured.value()))) {
            handleUnauthorized(user, joinPoint.getArgs());
            return null;
        }
        return joinPoint.proceed();
    }

    private boolean isAuthorized(User user, List<Role> requiredRoles) {
        return requiredRoles.contains(UNAUTHORIZED)
                || !Collections.disjoint(user.getRoles(), requiredRoles);
    }

    private void handleUnauthorized(User user, Object[] args) {
        log.warn("Unauthorized access: {} {}", user, args);
        String userChatId = String.valueOf(user.getChatId());
        publisher.publish(MessageBuilder.builder(userChatId)
                .line("Your token is *%s*", userChatId)
                .line("Please contact your supervisor to gain access")
                .build());
        publisher.publish(MessageBuilder.builder(adminChatId)
                .line("*Unauthorized access:*")
                .line("%s", Arrays.stream(args).map(Object::toString).collect(COLLECTOR_COMMA_SEPARATED))
                .build());
    }
}
