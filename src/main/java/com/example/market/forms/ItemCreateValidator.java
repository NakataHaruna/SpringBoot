package com.example.market.forms;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ItemCreateValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ItemCreateForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ItemCreateForm form = (ItemCreateForm)target;
        // 画像ファイルチェック
        MultipartFile imageFile = form.getImage();
        try {
            if (imageFile.getOriginalFilename().isEmpty()) {
                errors.rejectValue("image", null, "ファイルが選択されていません。");
                return;
            }
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            if(image == null) {
                errors.rejectValue("image", null, "ファイルの形式が正しくありません。");
                return;
            }
            if(image.getWidth() < 50 || image.getWidth() > 1000) {
                errors.rejectValue("image", null, "ファイルの横幅は50〜1000pxにしてください。");
            }
            if(image.getHeight() < 50 || image.getHeight() > 1000) {
                errors.rejectValue("image", null, "ファイルの高さは50〜1000pxにしてください。");
            }
            String mimeType = imageFile.getContentType().toLowerCase();
            if(!mimeType.endsWith("jpeg") && !mimeType.endsWith("png")) {
                errors.rejectValue("image", null, "画像はjpegまたはpng形式のみ有効です。");
            }
        } catch (IOException e) {
            e.printStackTrace();
            errors.rejectValue("image", null, "ファイル情報の取得に失敗しました。");
        }
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