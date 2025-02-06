package com.war11.domain.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String keyword;

  private Long count;

  private LocalDateTime cacheSaveTime;

  public Keyword(String keyword, long count, LocalDateTime cacheSaveTime) {
    this.keyword = keyword;
    this.count = count;
    this.cacheSaveTime = cacheSaveTime;
  }
}
