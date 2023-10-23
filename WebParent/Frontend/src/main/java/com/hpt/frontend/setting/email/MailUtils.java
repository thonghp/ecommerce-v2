package com.hpt.frontend.setting.email;

import com.hpt.frontend.oauth.CustomerOAuth2User;
import com.hpt.frontend.setting.currency.CurrencySettingBag;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

public class MailUtils {
    /**
     * Get the site URL from the request removed the servlet path.
     *
     * @param request the current request
     * @return the site URL does not contain servlet path
     */
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        System.out.println("siteURL: " + siteURL);
        System.out.println("request.getServletPath(): " + request.getServletPath());

        return siteURL.replace(request.getServletPath(), "");
    }

    /**
     * Setting up the mail sender.
     *
     * @param settings EmailSettingBag object
     * @return JavaMailSenderImpl object
     */
    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(settings.getHost());
        mailSender.setPort(settings.getPort());
        mailSender.setUsername(settings.getUsername());
        mailSender.setPassword(settings.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    /**
     * Get the email of the authenticated customer.
     *
     * @param request the current request
     * @return the email of the authenticated customer
     */
    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if (principal == null) return null;

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

    /**
     * Format the currency.
     *
     * @param amount   the amount of money
     * @param settings the currency settings object to get setting values
     * @return the formatted currency
     */
    public static String formatCurrency(float amount, CurrencySettingBag settings) {
        String symbol = settings.getSymbol();
        String symbolPosition = settings.getSymbolPosition();
        String decimalPointType = settings.getDecimalPointType();
        String thousandPointType = settings.getThousandPointType();
        int decimalDigits = settings.getDecimalDigits();

        String pattern = symbolPosition.equals("Before price") ? symbol : "";
        pattern += "###,###";

        if (decimalDigits > 0) {
            pattern += ".";
            for (int count = 1; count <= decimalDigits; count++) pattern += "#";
        }

        pattern += symbolPosition.equals("After price") ? symbol : "";

        char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
        char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandSeparator);

        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);

        return formatter.format(amount);
    }
}
