package io.ermdev.cshop.business.register;

import io.ermdev.cshop.data.entity.Token;
import io.ermdev.cshop.data.entity.User;
import io.ermdev.cshop.data.service.TokenService;
import io.ermdev.cshop.data.service.UserService;
import io.ermdev.cshop.exception.EntityException;
import io.ermdev.cshop.exception.ResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class ConfirmListener implements ApplicationListener<ConfirmEvent> {

    private OnConfirmCompleted onConfirmCompleted;
    private UserService userService;
    private TokenService tokenService;

    @Autowired
    public ConfirmListener(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public void onApplicationEvent(ConfirmEvent confirmEvent) {
        onConfirmCompleted = confirmEvent.getOnConfirmCompleted();
    }

    public void confirmUser(String key) {
        try {
            final Token token = tokenService.findByKey(key);
            final Calendar calendar = Calendar.getInstance();
            final long remainingTime = token.getExpiryDate().getTime() - calendar.getTime().getTime();

            if (remainingTime > 0) {
                User user = token.getUser();
                user.setEnabled(true);
                userService.save(user);
                tokenService.delete(token.getId());
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            onConfirmCompleted.onComplete(true);
        }
    }
}
