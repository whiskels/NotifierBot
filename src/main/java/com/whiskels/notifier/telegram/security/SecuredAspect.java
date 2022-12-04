package com.whiskels.notifier.telegram.security;

import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.UNAUTHORIZED;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class SecuredAspect {
    private final SendMessageCreationEventPublisher publisher;
    private final String adminChatId;

    public SecuredAspect(@Value("${telegram.bot.admin:}") String adminChatId,
                         SendMessageCreationEventPublisher publisher) {
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
            unauthorized(user, joinPoint.getArgs());
            return null;
        }
        return joinPoint.proceed();
    }

    private boolean isAuthorized(User user, List<Role> requiredRoles) {
        return requiredRoles.contains(UNAUTHORIZED)
                || !Collections.disjoint(user.getRoles(), requiredRoles);
    }

    private void unauthorized(User user, Object[] args) {
        log.warn("Unauthorized access: {} {}", user, args);
        String userChatId = String.valueOf(user.getChatId());
        publisher.publish(builder(userChatId)
                .line("Your token is *%s*", userChatId)
                .line("Please contact your supervisor to gain access")
                .build());
        if (isNotEmpty(adminChatId)) {
            publisher.publish(builder(adminChatId)
                    .line("*Unauthorized access:*")
                    .line("%s", Arrays.stream(args).map(Object::toString).collect(COLLECTOR_COMMA_SEPARATED))
                    .build());
        }
    }
}
