package com.wenziyue.security;

import com.wenziyue.security.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author wenziyue
 */
public class LoginUser implements UserDetails {

    private UserEntity userEntity;

    public LoginUser(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 注意：角色必须带前缀 "ROLE_"
        return AuthorityUtils.commaSeparatedStringToAuthorityList(userEntity.getRole()==1?"ROLE_ADMIN":"ROLE_USER");
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.getStatus()==0;
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
