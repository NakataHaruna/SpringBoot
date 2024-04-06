package com.example.market.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.market.services.UserService;

@Component
public class SignUpValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        // 対象クラスがSignUpFormクラスかどうか
        return SignUpForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm form = (SignUpForm)target;
        
        if (userService.findByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", null, "該当のメールアドレスは登録済みです。");
        }
    }
}