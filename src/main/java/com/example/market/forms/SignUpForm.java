package com.example.market.forms;


import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {
    @NotBlank(message="メールアドレスが入力されてません。")
    @Size(max=255, message="メールアドレスは255文字以内で入力してください")
    private String email;
    
    @NotBlank(message="パスワードが入力されてません。")
    @Size(min=8, max=100, message="パスワードは8文字以上、100文字以内で入力してください")
    private String password;
    
    @NotBlank(message="名前が入力されてません。")
    @Size(min=1, max=255, message="名前は255文字以内で入力してください")
    private String name;
    
    @NotBlank(message="パスワード(確認用)が入力されてません。")
    @Size(min=8, max=100)
    private String confirmPassword;
    
    @AssertTrue(message = "パスワードとパスワード（確認用）が一致しません。")
    public boolean isPasswordMatched() {
        return password.equals(confirmPassword);
    }
}
