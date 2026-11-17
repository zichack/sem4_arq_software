package br.com.mvc.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produto")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false)
  private BigDecimal preco;

  @Column(nullable = false)
  @Builder.Default
  private Integer quantidadeEstoque = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private StatusProduto status = StatusProduto.INDISPONIVEL;
}
