package br.com.deliverit.contas_pagar.service;

import br.com.deliverit.contas_pagar.dto.ContaRequestDTO;
import br.com.deliverit.contas_pagar.dto.ContaResponseDTO;
import br.com.deliverit.contas_pagar.entity.Conta;
import br.com.deliverit.contas_pagar.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository repository;

    @Transactional
    public ContaResponseDTO criar(ContaRequestDTO dto) {
        Conta conta = new Conta();
        conta.setNome(dto.getNome());
        conta.setValorOriginal(dto.getValorOriginal());
        conta.setDataVencimento(dto.getDataVencimento());
        conta.setDataPagamento(dto.getDataPagamento());

        calcularAtraso(conta);

        Conta contaSalva = repository.save(conta);
        return toResponseDTO(contaSalva);
    }

    @Transactional(readOnly = true)
    public List<ContaResponseDTO> listar() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void calcularAtraso(Conta conta) {
        long dias = ChronoUnit.DAYS.between(
                conta.getDataVencimento(),
                conta.getDataPagamento());

        conta.setDiasAtraso((int) Math.max(0, dias));

        BigDecimal multa;
        BigDecimal jurosDia;

        if (dias <= 0) {
            // Sem atraso
            multa = new BigDecimal("0.00");
            jurosDia = new BigDecimal("0.000");
        } else if (dias <= 3) {
            // Até 3 dias: 2% multa + 0,1% juros/dia
            multa = new BigDecimal("2.00");
            jurosDia = new BigDecimal("0.100");
        } else if (dias <= 5) {
            // 4-5 dias: 3% multa + 0,2% juros/dia
            multa = new BigDecimal("3.00");
            jurosDia = new BigDecimal("0.200");
        } else {
            // Superior a 5 dias: 5% multa + 0,3% juros/dia
            multa = new BigDecimal("5.00");
            jurosDia = new BigDecimal("0.300");
        }

        conta.setPercentualMulta(multa);
        conta.setPercentualJurosDia(jurosDia);

        // Cálculo do valor corrigido
        BigDecimal valorMulta = conta.getValorOriginal()
                .multiply(multa)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        BigDecimal valorJuros = conta.getValorOriginal()
                .multiply(jurosDia)
                .divide(new BigDecimal("100"), 3, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(Math.max(0, dias)))
                .setScale(2, RoundingMode.HALF_UP);

        conta.setValorCorrigido(
                conta.getValorOriginal()
                        .add(valorMulta)
                        .add(valorJuros));
    }

    private ContaResponseDTO toResponseDTO(Conta conta) {
        return ContaResponseDTO.builder()
                .id(conta.getId())
                .nome(conta.getNome())
                .valorOriginal(conta.getValorOriginal())
                .valorCorrigido(conta.getValorCorrigido())
                .diasAtraso(conta.getDiasAtraso())
                .dataPagamento(conta.getDataPagamento())
                .percentualMulta(conta.getPercentualMulta())
                .percentualJurosDia(conta.getPercentualJurosDia())
                .build();
    }
}