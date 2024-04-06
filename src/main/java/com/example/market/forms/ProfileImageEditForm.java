package com.example.market.forms;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageEditForm {
	@NotNull(message="画像は必ず選択してください。")
	private MultipartFile image;  // プロフィール画像のファイル名

}
