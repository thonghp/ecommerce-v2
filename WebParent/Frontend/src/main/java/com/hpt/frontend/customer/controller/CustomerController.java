package com.hpt.frontend.customer.controller;

import com.hpt.common.entity.Category;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.Customer;
import com.hpt.frontend.category.CategoryService;
import com.hpt.frontend.customer.CustomerService;
import com.hpt.frontend.oauth.CustomerOAuth2User;
import com.hpt.frontend.security.CustomerUserDetails;
import com.hpt.frontend.setting.SettingService;
import com.hpt.frontend.setting.email.EmailSettingBag;
import com.hpt.frontend.setting.email.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService service;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SettingService settingService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> listCountries = service.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());

        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        service.registerCustomer(customer);
        sendVerificationEmail(request, customer);

        model.addAttribute("pageTitle", "Đăng Ký Thành Công!");

        return "register/register_success";
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer)
            throws UnsupportedEncodingException, MessagingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = MailUtils.prepareMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", customer.getFullName());

        String verifyURL = MailUtils.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {
        boolean verified = service.verify(code);

        return "register/" + (verified ? "verify_success" : "verify_fail");
    }

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request) {
        String email = getEmailOfAuthenticatedCustomer(request);
        Customer customer = service.getCustomerByEmail(email);
        List<Country> listCountries = service.listAllCountries();
        List<Category> listCategories = categoryService.listRootCategories();

        model.addAttribute("customer", customer);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("listCategories", listCategories);

        return "customer/account_form";
    }

    private String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        String customerEmail = null;

        if (principal instanceof UsernamePasswordAuthenticationToken
                || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            customerEmail = oauth2User.getEmail();
        }

        return customerEmail;
    }

    @PostMapping("/update_account_details")
    public String updateAccountDetails(Model model, Customer customer, RedirectAttributes ra,
                                       HttpServletRequest request) {
        service.update(customer);
        ra.addFlashAttribute("message", "Đã cập nhật thành công.");

        updateNameForAuthenticatedCustomer(customer, request);

        return "redirect:/account_details";
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Object principal = request.getUserPrincipal();

        if (principal instanceof UsernamePasswordAuthenticationToken
                || principal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
            Customer authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());

        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            String fullName = customer.getLastName() + " " + customer.getFirstName();
            oauth2User.setFullName(fullName);
        }
    }

    private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
        CustomerUserDetails userDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        }

        return userDetails;
    }
}
