package com.example.market.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditForm {
	@NotBlank
    @Size(min=1, max=255, message="名前は255文字以内で入力してください")
    private String name;
	
	@NotNull
    @Size(max=1000, message="プロフィールは1001文字以内で入力してください。")
    private String profile;


}
