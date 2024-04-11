package com.ecommerce.site.admin.security;

import com.ecommerce.site.admin.model.entity.User;
import com.ecommerce.site.admin.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        @NotNull Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl userDetails =  (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.user();

        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
