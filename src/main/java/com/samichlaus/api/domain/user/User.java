package com.samichlaus.api.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.samichlaus.api.domain.token.Token;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonIgnoreProperties({
  "id",
  "password",
  "last_login_date",
  "created_at",
  "isActive",
  "isNotLocked",
  "failed_login_attempts",
  "tokens",
  "authorities",
  "role"
})
public class User implements UserDetails {

  @Id @GeneratedValue private Integer id;

  @Builder.Default
  @Column(name = "user_id", updatable = false)
  private UUID userid = UUID.randomUUID();

  @NotBlank(message = "First name is required")
  private String first_name;

  @NotBlank(message = "Last name is required")
  private String last_name;

  @Email(message = "Email is not valid")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  private Date last_login_date;

  @Builder.Default
  @Column(nullable = false)
  private Date created_at = new Date();

  @Enumerated(EnumType.STRING)
  private Role role;

  @Enumerated(EnumType.STRING)
  private List<Authoritie> authorities;

  @Column(columnDefinition = "boolean default true")
  private boolean isActive;

  @Column(columnDefinition = "boolean default true")
  private boolean isNotLocked;

  @Column(columnDefinition = "integer default 0")
  private Integer failed_login_attempts;

  @JsonManagedReference
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Token> tokens;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return isNotLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isActive;
  }
}
