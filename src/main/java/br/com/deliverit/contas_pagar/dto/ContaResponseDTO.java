package br.com.deliverit.contas_pagar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaResponseDTO {

    private Long id;
    private String nome;
    private BigDecimal valorOriginal;
    private BigDecimal valorCorrigido;
    private Integer diasAtraso;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPagamento;

    private BigDecimal percentualMulta;
    private BigDecimal percentualJurosDia;
}