package br.com.mvc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "pedido")
public class Pedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "produto_id")
  private Product product;

  @Column(nullable = false)
  private Integer quantidade;

  @Column(nullable = false)
  private BigDecimal valorTotal;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private StatusPedido status = StatusPedido.ABERTO;

  @Column(name = "fatura_id")
  private String faturaId;

  @Column(nullable = false)
  private LocalDateTime criadoEm;
}
