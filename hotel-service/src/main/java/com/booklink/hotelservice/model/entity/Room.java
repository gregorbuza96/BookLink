package com.booklink.hotelservice.model.entity;

import com.booklink.hotelservice.model.enums.ComfortLevel;
import com.booklink.hotelservice.model.enums.RoomStatus;
import com.booklink.hotelservice.model.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "room")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) private Integer roomNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RoomType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ComfortLevel comfort;
    @Column(nullable = false) private Double pricePerNight;
    @Column(nullable = false) private Integer capacity;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RoomStatus status;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id") private Hotel hotel;

    @ManyToMany
    @JoinTable(name = "room_amenity",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private List<Amenity> amenities;
}
