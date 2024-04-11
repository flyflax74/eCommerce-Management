package com.ecommerce.site.admin.security;

import com.ecommerce.site.admin.model.entity.User;
import com.ecommerce.site.admin.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ecommerce.site.admin.constant.ApplicationConstant.LOCK_TIME_DURATION;
import static com.ecommerce.site.admin.constant.ApplicationConstant.MAX_FAILED_ATTEMPT;


@Service
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(@NotNull HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        User user = userService.findByEmail(email);

        if (user != null) {
            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < MAX_FAILED_ATTEMPT - 1) {
                    userService.updateFailedAttempts(user);
                    switch (user.getFailedAttempt()) {
                        case 0 -> exception = new LockedException("Wrong password. Your account will be locked after 5 failed attempts."
                                + " You only have " + (MAX_FAILED_ATTEMPT - 1 - user.getFailedAttempt()) + " login attempts left");
                        case 3 -> exception = new LockedException("Wrong password. Please try to login again carefully. You only have the last attempt left");
                        default -> exception = new LockedException("Wrong password. Please try to login again carefully. You only have "
                                + (MAX_FAILED_ATTEMPT - 1 - user.getFailedAttempt()) + " login attempts left");
                    }
                } else {
                    userService.lock(user);
                    exception = new LockedException("Your account has been locked due to 5 failed attempts. It will be unlocked after 24 hours");
                }
            } else if (!user.isAccountNonLocked()) {
                if (userService.unlockWhenTimeExpired(user, LOCK_TIME_DURATION)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again");
                }
            } else {
                exception = new LockedException("Your account has been unlocked. Please contact to admin for unlock");
            }
        } else {
            exception = new LockedException("Your username or email does not exist. Please contact to admin for register");
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
