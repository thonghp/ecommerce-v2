package com.hpt.frontend.customer.controller;

import com.hpt.common.entity.Country;
import com.hpt.common.entity.Customer;
import com.hpt.frontend.customer.CustomerService;
import com.hpt.frontend.setting.SettingService;
import com.hpt.frontend.setting.email.EmailSettingBag;
import com.hpt.frontend.setting.email.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
//        service.registerCustomer(customer);
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

        System.out.println("to Address: " + toAddress);
        System.out.println("Verify URL: " + verifyURL);
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {
        boolean verified = service.verify(code);

        return "register/" + (verified ? "verify_success" : "verify_fail");
    }
}
