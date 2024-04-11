package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.exception.OrderNotFoundException;
import com.ecommerce.site.admin.model.entity.*;
import com.ecommerce.site.admin.helper.PagingAndSortingHelper;
import com.ecommerce.site.admin.annotation.PagingAndSortingParam;
import com.ecommerce.site.admin.model.enums.OrderStatus;
import com.ecommerce.site.admin.security.UserDetailsImpl;
import com.ecommerce.site.admin.service.OrderService;
import com.ecommerce.site.admin.service.SettingService;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/orders")
public class OrderController {
    
    private final String defaultRedirectUrl = "redirect:/orders/offset/10/page/1?sortField=orderTime&sortDir=desc";

    @Autowired
    private OrderService orderService;
    
    @Autowired 
    private SettingService settingService;

    @GetMapping("")
    public String listFirstPage() {
        return defaultRedirectUrl;
    }

    @GetMapping("/offset/{pageSize}/page/{pageNumber}")
    public String listByPage(@PagingAndSortingParam(listName = "listOrders", moduleUrl = "/orders") PagingAndSortingHelper helper,
                             @PathVariable("pageSize") int pageSize, @PathVariable("pageNumber") int pageNumber, HttpServletRequest request,
                             @AuthenticationPrincipal @NotNull UserDetailsImpl loggedUser) {
        orderService.listByPage(pageNumber, pageSize, helper);
        loadCurrencySetting(request);

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
            return "orders/order_shipper";
        }
        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/detail/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, @NotNull Model model,
                                   RedirectAttributes attributes, HttpServletRequest request,
                                   @AuthenticationPrincipal @NotNull UserDetailsImpl loggedUser) {
        try {
            Order order = orderService.get(id);
            loadCurrencySetting(request);
            boolean isVisibleForAdminOrSalesperson = loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson");
            model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
            model.addAttribute("order", order);

            return "orders/order_detail_modal";
        } catch (OrderNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id,
                              @NotNull RedirectAttributes attributes) {
        try {
            orderService.delete(id);
            attributes.addFlashAttribute("message", String.format("The order ID %s has been deleted", id));
        } catch (OrderNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectUrl;
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            Order order = orderService.get(id);
            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("pageTitle", String.format("Edit Order (ID %s)", id));
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";
        } catch (OrderNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @PostMapping("/save")
    public String saveOrder(@NotNull Order order, @NotNull HttpServletRequest request, @NotNull RedirectAttributes attributes) {
        String countryName = request.getParameter("countryName");
        order.setCountry(countryName);

        updateProductDetails(order, request);
        updateOrderTracks(order, request);

        orderService.save(order);

        attributes.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");
        return defaultRedirectUrl;
    }

    private void updateOrderTracks(@NotNull Order order, @NotNull HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        Set<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        for (int i = 0; i < trackIds.length; i++) {
            OrderTrack trackRecord = new OrderTrack();

            int trackId = Integer.parseInt(trackIds[i]);
            if (trackId > 0) {
                trackRecord.setId(trackId);
            }

            trackRecord.setOrder(order);
            trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            trackRecord.setNotes(trackNotes[i]);

            try {
                trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            orderTracks.add(trackRecord);
        }
    }

    private void updateProductDetails(@NotNull Order order, @NotNull HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (int i = 0; i < detailIds.length; i++) {
            OrderDetail orderDetail = new OrderDetail();
            int detailId = Integer.parseInt(detailIds[i]);
            if (detailId > 0) {
                orderDetail.setId(detailId);
            }

            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);
        }
    }
}
