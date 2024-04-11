package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.repository.CurrencyRepository;
import com.ecommerce.site.admin.service.SettingBagService;
import com.ecommerce.site.admin.service.SettingService;
import com.ecommerce.site.admin.util.AmazonS3Util;
import com.ecommerce.site.admin.util.FileUploadUtil;
import com.ecommerce.site.admin.model.entity.Currency;
import com.ecommerce.site.admin.model.entity.Setting;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_BASE_URI;
import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_SERVICE_ACTIVE;


@Controller
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("")
    public String listAll(@NotNull Model model) {
        List<Setting> listSettings = settingService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);
        if (S3_SERVICE_ACTIVE) {
            model.addAttribute("S3_BASE_URI", S3_BASE_URI);
        } else {
            model.addAttribute("S3_BASE_URI", null);
        }
        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return "settings/settings";
    }

    @PostMapping("/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
                                      HttpServletRequest request, @NotNull RedirectAttributes attributes) throws IOException {
        SettingBagService settingBag = settingService.getGeneralSettings();
        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);
        updateSettingValuesFromForm(request, settingBag.list());

        attributes.addFlashAttribute("message", "General settings have been saved");

        return "redirect:/settings";
    }

    private void saveSiteLogo(@NotNull MultipartFile multipartFile, SettingBagService settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

            if (S3_SERVICE_ACTIVE) {
                AmazonS3Util.removeFolder("site-logo");
                AmazonS3Util.uploadFile("site-logo", fileName, multipartFile.getInputStream());
            } else {
                FileUploadUtil.cleanDir("../site-logo");
                FileUploadUtil.saveFile("../site-logo", fileName, multipartFile);
            }
        }
    }

    private void saveCurrencySymbol(@NotNull HttpServletRequest request, SettingBagService settingBag) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findById = currencyRepository.findById(currencyId);
        if (findById.isPresent()) {
            Currency currency = findById.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request, @NotNull List<Setting> listSettings) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }
        settingService.saveAll(listSettings);
    }

    @PostMapping("/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, @NotNull RedirectAttributes attributes) {
        List<Setting> mailServerSettings = settingService.getMailServerSettings();
        updateSettingValuesFromForm(request, mailServerSettings);
        attributes.addFlashAttribute("message", "Mail server settings have been saved");
        return "redirect:/settings#mailServer";
    }

    @PostMapping("/save_mail_templates")
    public String saveMailTemplateSettings(HttpServletRequest request, @NotNull RedirectAttributes attributes) {
        List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
        updateSettingValuesFromForm(request, mailTemplateSettings);
        attributes.addFlashAttribute("message", "Mail template settings have been saved");
        return "redirect:/settings#mailTemplates";
    }

    @PostMapping("/save_payment")
    public String savePaymentSettings(HttpServletRequest request, @NotNull RedirectAttributes attributes) {
        List<Setting> paymentSettings = settingService.getPaymentSettings();
        updateSettingValuesFromForm(request, paymentSettings);
        attributes.addFlashAttribute("message", "Payment settings have been saved");
        return "redirect:/settings#payment";
    }
}

