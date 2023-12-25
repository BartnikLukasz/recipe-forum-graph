package bartnik.master.app.graph.recipeforum.config;

import bartnik.master.app.graph.recipeforum.model.CustomUser;
import bartnik.master.app.graph.recipeforum.model.projections.CustomUserGet;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ToString
public class CustomUserDetails implements UserDetails {


    private final String name;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(CustomUser customUser) {
        name=customUser.getUsername();
        password=customUser.getPassword();
        authorities= Arrays.stream(customUser.getAuthorities().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public CustomUserDetails(CustomUserGet customUserGet) {
        name = customUserGet.getUsername();
        password = customUserGet.getPassword();
        authorities = Arrays.stream(customUserGet.getAuthorities().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
