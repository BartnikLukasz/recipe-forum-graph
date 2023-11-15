package bartnik.master.app.graph.recipeforum.util;

import bartnik.master.app.graph.recipeforum.config.CustomUserDetails;
import bartnik.master.app.graph.recipeforum.model.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static CustomUserDetails getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
    }

    public static boolean isCurrentUserAdmin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        var user =  (CustomUserDetails) securityContext.getAuthentication().getPrincipal();
        return user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()));
    }

}
