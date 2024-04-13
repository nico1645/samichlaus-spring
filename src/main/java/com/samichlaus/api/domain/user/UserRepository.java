package com.samichlaus.api.domain.user;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  Boolean existsByEmail(String email);
  @Query(value = "Select * FROM users where users.user_id = :user_id", nativeQuery = true)
  Optional<User> findByUserid(@Param("user_id") UUID userId);
  @NotNull
  public Optional<User> findById(@NotNull Integer id);

}