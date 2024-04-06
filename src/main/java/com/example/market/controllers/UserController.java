package com.example.market.controllers;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
import com.example.market.forms.ProfileCreateValidator;
import com.example.market.forms.ProfileEditForm;
import com.example.market.forms.ProfileImageEditForm;
import com.example.market.forms.SignUpForm;
import com.example.market.forms.SignUpValidator;
import com.example.market.services.ItemService;
import com.example.market.services.UserService;

@RequestMapping("/users")
@Controller
public class UserController {
	
	// UserController 内で UserService を利用できるようにする
    private final UserService userService;
    private final ItemService itemService;
    private final SignUpValidator signUpValidator;     
    private final ProfileCreateValidator profileCreateValidator; 
 
    public UserController(UserService userService,
    	ItemService itemService,
    	SignUpValidator signUpValidator,
    	ProfileCreateValidator profileCreateValidator
    	) {
        this.userService = userService;
        this.itemService = itemService;
        this.signUpValidator = signUpValidator;
        this.profileCreateValidator = profileCreateValidator;
    }
    
    @InitBinder("signUpForm")
    public void initBinderSignUpForm(WebDataBinder binder) {
        binder.addValidators(signUpValidator);
    }
    
    @InitBinder("profileImageEditForm")
    public void initBinderCreateForm(WebDataBinder binder) {
        binder.addValidators(profileCreateValidator);
    } 
    
    @GetMapping("/sign_up")    
    public String signUp(
        @ModelAttribute("sign_up") SignUpForm signUpForm,
        Model model) {
        model.addAttribute("signUpForm", signUpForm);
        model.addAttribute("title", "ユーザー登録");
        model.addAttribute("main", "users/sign_up::main");
        return "layout/not_logged_in";    
    }
    
    // サインアップフォーム投稿時の処理を追記
    @PostMapping("/sign_up")
    public String signUpProcess(
        @ModelAttribute @Validated SignUpForm signUpForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model,
        HttpServletRequest request
    	){
        
        if (bindingResult.hasErrors()) {
            return signUp(signUpForm, model);
        }
        
        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};
        userService.register(
            signUpForm.getName(),
            signUpForm.getEmail(),
            signUpForm.getPassword(),
            roles);
        redirectAttributes.addFlashAttribute(
            "successMessage",
            "アカウントの登録が完了しました");
        try {
			request.login(signUpForm.getEmail(), signUpForm.getPassword());
		} catch (ServletException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return "redirect:/users/login";
		}
        return "redirect:/items/";
    }
 
    @GetMapping("/login")    
    public String login(
    	Model model) {
        model.addAttribute("title", "ログイン");
        model.addAttribute("main", "users/login::main");
        return "layout/not_logged_in";    
    }
 
    @GetMapping("/{id}")    
    public String detail(
    	@AuthenticationPrincipal(expression = "user") User loginUser,
        @PathVariable("id") Integer id,
        Item item, Model model) {
    	User user = userService.findById(id).orElseThrow();
    	model.addAttribute("user", user);
        model.addAttribute("title", "プロフィール");
        model.addAttribute("main", "users/detail::main");
        return "layout/logged_in";    
    }
 
    @GetMapping("/{id}/update")    
    public String edit(
    	@AuthenticationPrincipal(expression = "user") User loginUser,
    	@ModelAttribute("profileEditForm") ProfileEditForm profileEditForm,
        @PathVariable("id")  Integer id,
        Model model) {
    	User user = userService.findById(id).orElseThrow();
    	profileEditForm.setName(user.getName());
    	profileEditForm.setProfile(user.getProfile());
    	model.addAttribute("user", user);
        model.addAttribute("title", "プロフィール編集");
        model.addAttribute("main", "users/update::main");
        return "layout/logged_in";    
    }
    
    @PostMapping("/updateProfile/{id}")
    public String update(
    	@AuthenticationPrincipal(expression = "user") User loginUser,
        @PathVariable("id")  Integer id,
        @Valid ProfileEditForm profileEditForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model) {
    	if(bindingResult.hasErrors()){
            return edit(loginUser, profileEditForm, id, model);
        }
    	userService.updateProfile(
    		id,
    		profileEditForm.getName(),
    		profileEditForm.getProfile()
    	);
    	redirectAttributes.addFlashAttribute(
    			"successMessage",
                "プロフィールの更新が完了しました");
    	return "redirect:/users/{id}";
    }
    
    @GetMapping("/{id}/imageUpdate")
    public String image(
        	@AuthenticationPrincipal(expression = "user") User loginUser,
        	@ModelAttribute("profileImageEditForm") ProfileImageEditForm profileImageEditForm,
            @PathVariable("id")  Integer id,
            Model model) {
        	User user = userService.findById(id).orElseThrow();
        	profileImageEditForm.getImage();
        	model.addAttribute("user", user);
            model.addAttribute("title", "プロフィール画像編集");
            model.addAttribute("main", "users/image::main");
            return "layout/logged_in";    
        }
        
    @PostMapping("/update/{id}")
    public String update(
    	@AuthenticationPrincipal(expression = "user") User loginUser,
        @PathVariable("id")  Integer id,
        @Valid ProfileImageEditForm profileImageEditForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model) {
    	if(bindingResult.hasErrors()){
            return image(loginUser, profileImageEditForm, id, model);
        }
    	userService.updateImage(
    		id,
    		profileImageEditForm.getImage()
    	);
    	redirectAttributes.addFlashAttribute(
    			"successMessage",
                "プロフィール画像の更新が完了しました");
    	return "redirect:/users/{id}";
    }
    
    @GetMapping("/{id}/exhibitions")    
    public String exhibitions(
    	@AuthenticationPrincipal(expression = "user") User loginUser,
        @PathVariable("id")  Integer id,
    	Model model) {
    	User user = userService.findById(id).orElseThrow();
    	List<Item> items = itemService.getExhivitions(id);
    	model.addAttribute("user", user);
    	model.addAttribute("items", items);
        model.addAttribute("title", "出品商品一覧");
        model.addAttribute("main", "users/exhibitions::main");
        return "layout/logged_in";    
    }
    
    @GetMapping("/detail/{id}")    
    public String detail(
    	@AuthenticationPrincipal(expression = "user") User loginUser,
        @PathVariable("id")  Integer id,
        Model model) {
    	User user = userService.findById(id).orElseThrow();
    	List<Item> items = itemService.getLikedItems(id);
    	model.addAttribute("items", items);
        model.addAttribute("user", user);
        model.addAttribute("title", "お気に入り一覧");
        model.addAttribute("main", "users/likes::main");
        return "layout/logged_in";    
    }
}