package com.truskappka.truskappka_backend.stand.entity;

import com.truskappka.truskappka_backend.opinion.entity.Opinion;
import com.truskappka.truskappka_backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "stands")
public class Stand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;

    private LocalTime mondayHoursOpen;
    private LocalTime mondayHoursClose;
    private LocalTime tuesdayHoursOpen;
    private LocalTime tuesdayHoursClose;
    private LocalTime wednesdayHoursOpen;
    private LocalTime wednesdayHoursClose;
    private LocalTime thursdayHoursOpen;
    private LocalTime thursdayHoursClose;
    private LocalTime fridayHoursOpen;
    private LocalTime fridayHoursClose;
    private LocalTime saturdayHoursOpen;
    private LocalTime saturdayHoursClose;
    private LocalTime sundayHoursOpen;
    private LocalTime sundayHoursClose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "stand")
    private Set<Opinion> opinions;
}
