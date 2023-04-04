package com.heb.eg.interview.imagedetectorapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "IMAGE")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String location;

    @Column
    private char enableDetection;

    @Column
    private String label;

    @Column(nullable = false)
    private String objectsDetected;

}
