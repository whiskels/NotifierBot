package com.whiskels.notifier.telegram.util;

import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.TelegramLabeled;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.security.Secured;
import lombok.experimental.UtilityClass;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.*;

import static com.whiskels.notifier.telegram.domain.Role.UNAUTHORIZED;

@UtilityClass
public class TelegramUtil {
    public static <T> String getTelegramLabel(T object) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(object);
        if (TelegramLabeled.class.isAssignableFrom(clazz) || TelegramLabeled.class.isAssignableFrom(object.getClass())) {
            TelegramLabeled converted = (TelegramLabeled) object;
            return converted.getLabel();
        }
        return clazz.getSimpleName();
    }

    public static <T extends CommandHandler> Map<Role, Set<T>> groupByAllowedRoles(Collection<T> handlers) {
        Map<Role, Set<T>> map = new HashMap<>();
        EnumSet.allOf(Role.class).forEach(r -> map.put(r, new HashSet<>()));
        for (T handler : handlers) {
            Optional<Role[]> requiredRoles = Arrays.stream(AopProxyUtils.ultimateTargetClass(handler)
                            .getMethods())
                    .filter(method -> method.isAnnotationPresent(Secured.class))
                    .map(method -> method.getAnnotation(Secured.class).value())
                    .findAny();
            requiredRoles.ifPresentOrElse(roles -> Arrays.stream(roles).forEach(role -> map.get(role).add(handler)),
                    () -> map.get(UNAUTHORIZED).add(handler));
        }
        return map;
    }
}
