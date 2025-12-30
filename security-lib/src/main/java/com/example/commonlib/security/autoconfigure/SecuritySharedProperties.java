package com.example.commonlib.security.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "security.shared")
public class SecuritySharedProperties {

    private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:8080"));

    private List<String> permitAll = new ArrayList<>(); // default: none


    private String rolesClaim = "roles";
    private String rolePrefix = "ROLE_";

    public List<String> getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }

    public List<String> getPermitAll() { return permitAll; }
    public void setPermitAll(List<String> permitAll) { this.permitAll = permitAll; }

    public String getRolesClaim() { return rolesClaim; }
    public void setRolesClaim(String rolesClaim) { this.rolesClaim = rolesClaim; }

    public String getRolePrefix() { return rolePrefix; }
    public void setRolePrefix(String rolePrefix) { this.rolePrefix = rolePrefix; }
}
