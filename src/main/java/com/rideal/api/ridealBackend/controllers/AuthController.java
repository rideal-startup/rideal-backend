package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.User;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@BasePathAwareController
public class AuthController {
    @RequestMapping("/identity")
    public @ResponseBody
    User getAuthenticatedUserIdentity(
            PersistentEntityResourceAssembler resourceAssembler) {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
