package com.springboot.spring3.LoginSpringBoot3.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.naming.Name;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = " user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstname;

    private String lastname;

    private String email;

    private String pasword;

    @Enumerated(EnumType.STRING) //tomara el valor de la cadena interna
    private Role role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        //1. en nuestro programa solo devolveremos un rol,los usuarios no tendran mas que 1 rol, por eso ponemos asi
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override//7
    public String getPassword() {
        return pasword;
    }

    @Override //2. retornameremos el eamil
    public String getUsername() {
        return email;
    }

    @Override //3.cambiamos a true
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override//4. cambiamos atrue
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override// 5
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override//6
    public boolean isEnabled() {
        return true;
    }
}
