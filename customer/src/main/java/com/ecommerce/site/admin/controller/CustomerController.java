package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.annotation.PagingAndSortingParam;
import com.ecommerce.site.admin.exception.CustomerNotFoundException;
import com.ecommerce.site.admin.helper.PagingAndSortingHelper;
import com.ecommerce.site.admin.model.entity.Country;
import com.ecommerce.site.admin.model.entity.Customer;
import com.ecommerce.site.admin.model.entity.State;
import com.ecommerce.site.admin.repository.StateRepository;
import com.ecommerce.site.admin.service.CustomerService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final String defaultRedirectUrl = "redirect:/customers/offset/10/page/1?sortField=firstName&sortDir=asc";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("")
    public String listFirstPage() {
        return defaultRedirectUrl;
    }

    @GetMapping("/offset/{pageSize}/page/{pageNumber}")
    public String listByPage(@PagingAndSortingParam(listName = "listCustomers", moduleUrl = "/customers") PagingAndSortingHelper helper,
                             @PathVariable("pageSize") int pageSize, @PathVariable("pageNumber") int pageNumber) {
        customerService.listByPage(pageNumber, pageSize, helper);
        return "customers/customers";
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                              @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        customerService.updateCustomerEnabledStatus(id, enabled);
        attributes.addFlashAttribute("message", String.format("The customer ID %s has been %s", id, enabled ? "enabled" : "disabled"));
        return String.format("redirect:%s", request.getHeader("Referer"));
    }

    @GetMapping("/detail/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            Customer customer = customerService.get(id);
            model.addAttribute("customer", customer);
            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            Customer customer = customerService.get(id);
            List<Country> listCountries = customerService.listCountries();
            List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(id));

            model.addAttribute("listCountries", listCountries);
            model.addAttribute("listStates", listStates);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));

            return "customers/customer_form";
        } catch (CustomerNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @PostMapping("/save")
    public String saveCustomer(Customer customer,
                               @NotNull RedirectAttributes attributes) {
        customerService.save(customer);
        attributes.addFlashAttribute("message", String.format("The customer ID %s has been updated successfully", customer.getId()));
        return defaultRedirectUrl;
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        try {
            customerService.delete(id);
            attributes.addFlashAttribute("message", String.format("The customer ID %s has been deleted successfully", id));
        } catch (CustomerNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return String.format("redirect:%s", request.getHeader("Referer"));
    }
}
