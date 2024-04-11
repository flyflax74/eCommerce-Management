package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.model.DashboardInfo;
import com.ecommerce.site.admin.service.DashboardService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @Autowired
    private DashboardService service;

    @GetMapping("/")
    public String viewHomePage(@NotNull Model model) {
        DashboardInfo summary = service.loadSummary();
        model.addAttribute("summary", summary);
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }
}
