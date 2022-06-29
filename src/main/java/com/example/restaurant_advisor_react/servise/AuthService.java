package com.example.restaurant_advisor_react.servise;

import com.example.restaurant_advisor_react.AuthUser;
import com.example.restaurant_advisor_react.model.Role;
import com.example.restaurant_advisor_react.model.User;
import com.example.restaurant_advisor_react.model.dto.AuthRequest;
import com.example.restaurant_advisor_react.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Set;
import java.util.UUID;

import static com.example.restaurant_advisor_react.util.UserUtil.prepareToSave;
import static com.example.restaurant_advisor_react.util.validation.ValidationUtil.checkNew;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;


    @Autowired
    MailSender mailSender;

    @Value("${myhostname}")
    private String myHostName;

    public AuthUser getAuthUser(AuthRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(), request.getPassword()
                        )
                );
        return (AuthUser) authentication.getPrincipal();
    }

    public User registerUser(User user) {
        checkNew(user);
        Assert.notNull(user, "user must not be null");

        user.setEnabled(false);
        user.setRoles(Set.of(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(prepareToSave(user));
        sendMessage(user);

        return user;
    }

    private void sendMessage(User user) {
        if (!ObjectUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Restaurant Advisor. Please, visit next link: http://%s/api/v1/auth/activate/%s",
                    user.getFirstName(),
                    myHostName,
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }
}