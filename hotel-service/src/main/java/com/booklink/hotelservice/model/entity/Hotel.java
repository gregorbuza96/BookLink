package com.booklink.hotelservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "hotel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hotel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    private String address;
    @Column(nullable = false) private String city;
    @Column(nullable = false) private String country;
    private Integer starRating;
    private String phone;
    private String email;
    private String description;
    @Column(name = "image_url", length = 1000) private String imageUrl;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms;
}
