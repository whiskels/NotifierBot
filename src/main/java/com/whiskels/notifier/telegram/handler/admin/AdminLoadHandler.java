package com.whiskels.notifier.telegram.handler.admin;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.telegram.Command;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import static com.whiskels.notifier.telegram.Command.ADMIN_RELOAD_DATA;

@Service
@ConditionalOnBean(Loader.class)
class AdminLoadHandler extends BeanCallingHandler<Loader> {
    public AdminLoadHandler() {
        super(Loader::load, null, "Reload data");
    }

    @Override
    public Command getCommand() {
        return ADMIN_RELOAD_DATA;
    }
}
