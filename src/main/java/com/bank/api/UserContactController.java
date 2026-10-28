package com.bank.api;

import com.bank.dto.ContactResponse;
import com.bank.dto.EmailRequest;
import com.bank.dto.PhoneRequest;
import com.bank.security.CurrentUser;
import com.bank.service.UserContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserContactController {

    private final UserContactService userContactService;

    @PostMapping("/emails")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse addEmail(@Valid @RequestBody EmailRequest request) {
        return userContactService.addEmail(CurrentUser.id(), request);
    }

    @PutMapping("/emails/{id}")
    public ContactResponse updateEmail(@PathVariable Long id, @Valid @RequestBody EmailRequest request) {
        return userContactService.updateEmail(CurrentUser.id(), id, request);
    }

    @DeleteMapping("/emails/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmail(@PathVariable Long id) {
        userContactService.deleteEmail(CurrentUser.id(), id);
    }

    @PostMapping("/phones")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse addPhone(@Valid @RequestBody PhoneRequest request) {
        return userContactService.addPhone(CurrentUser.id(), request);
    }

    @PutMapping("/phones/{id}")
    public ContactResponse updatePhone(@PathVariable Long id, @Valid @RequestBody PhoneRequest request) {
        return userContactService.updatePhone(CurrentUser.id(), id, request);
    }

    @DeleteMapping("/phones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhone(@PathVariable Long id) {
        userContactService.deletePhone(CurrentUser.id(), id);
    }
}
