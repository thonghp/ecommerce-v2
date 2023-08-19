package com.hpt.frontend.oauth;

import com.hpt.common.entity.AuthenticationType;
import com.hpt.common.entity.Customer;
import com.hpt.frontend.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oauth2User = (CustomerOAuth2User) authentication.getPrincipal();

        String name = oauth2User.getName();
        String email = oauth2User.getEmail();
        String countryCode = request.getLocale().getCountry();
        String clientName = oauth2User.getClientName();

        AuthenticationType authenticationType = getAuthenticationType(clientName);

        Customer customer = customerService.getCustomerByEmail(email);
        if (customer == null) {
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
        } else {
            customerService.updateAuthenticationType(customer, authenticationType);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        if (clientName.equals("Google")) {
            return AuthenticationType.GOOGLE;
        } else if (clientName.equals("Facebook")) {
            return AuthenticationType.FACEBOOK;
        } else {
            return AuthenticationType.DATABASE;
        }
    }
}
