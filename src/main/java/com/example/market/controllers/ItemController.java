package com.example.market.controllers;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.market.entities.Item;
import com.example.market.entities.User;
import com.example.market.forms.ItemCreateForm;
import com.example.market.forms.ItemCreateValidator;
import com.example.market.forms.ItemEditForm;
import com.example.market.forms.ItemEditValidator;
import com.example.market.forms.ItemImageValidator;
import com.example.market.forms.PhotoEditForm;
import com.example.market.services.ItemService;
import com.example.market.services.UserService;

@RequestMapping("/items")
@Controller
public class ItemController { 
	
	// itemService を利用できるように
    private final ItemService itemService;
    private final UserService userService;
    private final ItemCreateValidator itemCreateValidator;
    private final ItemImageValidator itemImageValidator;
    private final ItemEditValidator itemEditValidator;
 
    public ItemController(
    	UserService userService,
    	ItemService itemService,
    	ItemCreateValidator itemCreateValidator,
    	ItemImageValidator itemImageValidator,
    	ItemEditValidator itemEditValidator
    	) {
    	this.userService = userService;
        this.itemService = itemService;
        this.itemCreateValidator = itemCreateValidator;
        this.itemImageValidator = itemImageValidator;
        this.itemEditValidator = itemEditValidator;
    }
    
    @PersistenceContext
    EntityManager entityManager;
    
    @InitBinder("itemCreateForm")
    public void initBinderCreateForm(WebDataBinder binder) {
        binder.addValidators(this.itemCreateValidator);
    }  
    
    @InitBinder("photoEditForm")
    public void initBinderPhotoCreateForm(WebDataBinder binder) {
        binder.addValidators(this.itemImageValidator);
    }  
    
    @InitBinder("itemEditForm")
    public void initBinderEditForm(WebDataBinder binder) {
        binder.addValidators(this.itemEditValidator);
    }  
    
    @GetMapping("/")    
    public String index(
    	@AuthenticationPrincipal(expression = "user") User user,
    	Model model
    ) {
    	// 最新のユーザー情報を取得
    	Optional<User> refreshedUser = userService.findById(user.getId());
        model.addAttribute("user", refreshedUser.orElseThrow());
        List<Item> items = itemService.getItemsExceptSelf(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("items", items);
        model.addAttribute("main", "items/index::main");
        return "layout/logged_in";    
    }
 
    @GetMapping("/create")    
    public String create(
        @AuthenticationPrincipal(expression = "user") User user,
        @ModelAttribute("itemCreateForm") ItemCreateForm itemCreateForm,
        Model model
    ) {
        model.addAttribute("title", "商品を出品");
        model.addAttribute("user", user);
        model.addAttribute("categories", itemService.getCategories());
        model.addAttribute("main", "items/create::main");
        return "layout/logged_in";    
    }
    
    @PostMapping("/create")    
    public String createProcess(
        @AuthenticationPrincipal(expression = "user") User user,
        @Valid ItemCreateForm itemCreateForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
        ) {
        if(bindingResult.hasErrors()){
            return create(user, itemCreateForm, model);
        }
        Item item = itemService.register(
        user,
        itemCreateForm.getName(),
        itemCreateForm.getDescription(),
        itemCreateForm.getCategory(),
        Integer.parseInt(itemCreateForm.getPrice()),
        itemCreateForm.getImage(),
        1);
        redirectAttributes.addFlashAttribute(
            "successMessage",
            "出品が完了しました");
        return "redirect:/items/" + item.getId();  
    }
 
    @GetMapping("/{id}")    
    public String detail(
        @AuthenticationPrincipal(expression = "user") User user,
        @PathVariable("id")  Long id,
        Model model
    ) {
        Optional<Item> item = itemService.findById(id);
        model.addAttribute("item", item.isEmpty() ? null : item.get());
        model.addAttribute("user", user);
        model.addAttribute("title", "商品詳細");
        model.addAttribute("main", "items/detail::main");
        return "layout/logged_in";    
    }
 
    @GetMapping("/{id}/update")
    public String edit(
        @AuthenticationPrincipal(expression = "user") User user,
        @ModelAttribute ItemEditForm itemEditForm,
        BindingResult bindingResult,
        @PathVariable("id")  Long id,
        Model model
    ) {
        Item item = itemService.findById(id).orElseThrow();
        if (!bindingResult.hasErrors()) {
            itemEditForm.setName(item.getName());
            itemEditForm.setDescription(item.getDescription());
            itemEditForm.setPrice(String.valueOf(item.getPrice()));
            itemEditForm.setCategory(item.getCategory());
        }
        model.addAttribute("categories", itemService.getCategories());
        model.addAttribute("item", item);
        model.addAttribute("user", user);
        model.addAttribute("title", "商品情報の編集");
        model.addAttribute("main", "items/edit::main");
        return "layout/logged_in";
    }
    
    @PostMapping("/{id}/update")
    public String update(
        @AuthenticationPrincipal(expression = "user") User user,
        @PathVariable("id")  Long id,
        @Valid @ModelAttribute ItemEditForm itemEditForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if(bindingResult.hasErrors()){
            return edit(user, itemEditForm, bindingResult, id, model);
        }
        itemService.update(id, 
                itemEditForm.getName(), 
                itemEditForm.getDescription(), 
                itemEditForm.getCategory(), 
                Integer.parseInt(itemEditForm.getPrice())
        );
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "商品情報の更新が完了しました");
            return "redirect:/items/{id}";
    }
    
    @GetMapping("/{id}/imageUpdate")
    public String image(
    	@AuthenticationPrincipal(expression = "user") User user,
        @ModelAttribute("photoEditForm") PhotoEditForm photoEditForm,
        @PathVariable("id")  Long id,
        Model model
    ) {
    	Item item = itemService.findById(id).orElseThrow();
    	photoEditForm.getImage();
    	model.addAttribute("item", item);
    	model.addAttribute("user", user);
    	model.addAttribute("title", "商品画像の変更");
        model.addAttribute("main", "items/image::main");
        return "layout/logged_in";    
    }
    
    @PostMapping("/{id}/imageUpdate")
    public String updateImage(
    	@AuthenticationPrincipal(expression = "user") User user,
        @PathVariable("id")  Long id,
        @Valid PhotoEditForm photoEditForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model) {
    	if(bindingResult.hasErrors()){
            return image(user, photoEditForm, id, model);
        }
    	itemService.updateImage(
    		id,
    		photoEditForm.getImage()
    	);
    	redirectAttributes.addFlashAttribute(
                "successMessage",
                "商品画像の更新が完了しました");
            return "redirect:/items/{id}";
    }
    
    @PostMapping("/delete/{id}")    
    public String delete(
        @PathVariable("id")  Long id,
        RedirectAttributes redirectAttributes,
        Model model) {
        itemService.delete(id);
        redirectAttributes.addFlashAttribute(
            "successMessage",
            "商品の削除が完了しました");
        return "redirect:/items/";  
    }
    
    @GetMapping("/{id}/confirm")    
    public String confirm(
    	@AuthenticationPrincipal(expression = "user") User user,
        @PathVariable("id")  Long id,
    	Model model
    ) {
    	Item item = itemService.findById(id).orElseThrow();
        model.addAttribute("item", item);
    	model.addAttribute("user", user);
        model.addAttribute("title", "購入確認");
        model.addAttribute("main", "items/confirm::main");
        return "layout/logged_in";    
    }
    
    @GetMapping("/{id}/finish")    
    public String finish(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable("id")  Long id,
            RedirectAttributes redirectAttributes,
            Model model
        ) {
            Item item = itemService.findById(id).orElseThrow();
            model.addAttribute("item", item);
            model.addAttribute("user", user);
            model.addAttribute("title", "購入確定");
            model.addAttribute("main", "items/finish::main");
            return "layout/logged_in";    
        }
    
    // お気に入り機能
    @PostMapping("/toggleLike/{id}")    
    public String toggleLike(
        @PathVariable("id")  Integer id,
        @AuthenticationPrincipal(expression = "user") User user,
        Model model) {
        itemService.toggleLike(
            user,
            id
        );
        return "redirect:/items/";  
    }
    
    // カート機能
    @PostMapping("/addcart/{id}")
    public String addcart(
        @PathVariable("id") Integer id,
        @AuthenticationPrincipal(expression = "user") User user,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        Item item = itemService.findById(id).orElseThrow();
        if (item.isSoldOut()) {                                                       
            redirectAttributes.addFlashAttribute("message", "申し訳ありません。ちょっと前に売り切れました。");
            return "redirect:/items/" + id;
        }
        itemService.getOrderItems(
        		user,
        		id
        		);
        return "redirect:/items/{id}/finish";

    }
}
