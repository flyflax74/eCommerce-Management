package com.ecommerce.site.admin.rest;

import com.ecommerce.site.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@Param("id") Integer id,
                                      @Param("email") String email) {
        return userService.checkEmailUnique(id, email) ? "OK" : "Duplicated";
    }

}
