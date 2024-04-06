package com.example.market.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.example.market.entities.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEditForm {
	
	@NotBlank(message="商品名は必ず入力してください。")
    @Size(min=1, max=255, message="商品名は255文字以内で入力してください。")
    private String name; // 商品名
    
    @NotNull(message="カテゴリーは必ず選択してください。")
    private Category category;
    
    @NotBlank(message="商品説明は必ず入力してください。")
    @Size(min=1, max=1000, message="商品説明は1000文字以内で入力してください。")
    private String description; // 商品説明
    
    @NotBlank(message="価格は必ず入力してください。")
    private String price; // 価格
}
