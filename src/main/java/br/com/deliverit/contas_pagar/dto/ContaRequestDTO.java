package br.com.deliverit.contas_pagar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Valor original é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor original deve ser maior que zero")
    private BigDecimal valorOriginal;

    @NotNull(message = "Data de vencimento é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimento;

    @NotNull(message = "Data de pagamento é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPagamento;
}