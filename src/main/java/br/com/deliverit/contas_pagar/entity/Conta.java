package br.com.deliverit.contas_pagar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(name = "valor_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorOriginal;
    
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;
    
    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;
    
    @Column(name = "dias_atraso", nullable = false)
    private Integer diasAtraso;
    
    @Column(name = "percentual_multa", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualMulta;
    
    @Column(name = "percentual_juros_dia", nullable = false, precision = 5, scale = 3)
    private BigDecimal percentualJurosDia;
    
    @Column(name = "valor_corrigido", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCorrigido;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}