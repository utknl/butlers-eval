package com.utknl.butlers.eval.features.question;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "questions")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class Question {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String model;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    private int complexityScore;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
