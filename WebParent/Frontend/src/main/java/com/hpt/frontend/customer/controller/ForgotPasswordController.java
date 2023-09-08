package com.hpt.frontend.customer.controller;

import com.hpt.common.entity.Customer;
import com.hpt.common.exception.CustomerNotFoundException;
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

@Controller
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = MailUtils.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(link, email);

            model.addAttribute("message", "Chúng tôi đã gửi một liên kết đặt lại mật khẩu đến email của bạn."
                    + " Vui lòng kiểm tra.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Could not send email");
        }

        return "customer/forgot_password_form";
    }

    private void sendEmail(String link, String email)
            throws UnsupportedEncodingException, MessagingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = MailUtils.prepareMailSender(emailSettings);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(email);
        helper.setSubject(subject);

        helper.setText(content, true);
        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        Customer customer = customerService.getCustomerByResetPasswordToken(token);
        if (customer != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", "Invalid Token");

            return "message";
        }

        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        try {
            customerService.updatePassword(token, password);

            model.addAttribute("pageTitle", "Reset Password");
            model.addAttribute("title", "Reset Password");
            model.addAttribute("message", "Bạn đã thay đổi mật khẩu thành công.");

        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", e.getMessage());
        }

        return "message";
    }
}
