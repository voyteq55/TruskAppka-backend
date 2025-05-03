package com.truskappka.truskappka_backend.user.entity;

import com.truskappka.truskappka_backend.opinion.entity.Opinion;
import com.truskappka.truskappka_backend.stand.entity.Stand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    public User() {
        this.uuid = UUID.randomUUID();
    }

    public User(String email) {
        this.uuid = UUID.randomUUID();
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean isVendor;

    @OneToMany(mappedBy = "user")
    private Set<Stand> stands;

    @OneToMany(mappedBy = "user")
    private Set<Opinion> opinions;


}
