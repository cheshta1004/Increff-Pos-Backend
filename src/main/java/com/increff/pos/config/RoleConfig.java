package com.increff.pos.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
@Component
public class RoleConfig {

    private final Set<String> supervisorEmails;
    public RoleConfig(@Value("${supervisor.emails}") String emails) {
        this.supervisorEmails = new HashSet<>(Arrays.asList(emails.toLowerCase().split(",")));
    }
    public boolean isSupervisor(String email) {
        if (Objects.isNull(email)) {
            return false;
        }
        return supervisorEmails.contains(email.toLowerCase().trim());
    }
}
