package com.example.market.forms;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemEditValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ItemEditForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	ItemEditForm form = (ItemEditForm)target;
        
    	try {
            int price = Integer.parseInt(form.getPrice());
            if(price < 1 || price > 1000000) {
            	errors.rejectValue("price", null,"価格は1~1000000の範囲で入力してください。");
            }
        } catch(NumberFormatException e) {
        	errors.rejectValue("price", null, "価格は数値で入力してください");
        }
    }
}
