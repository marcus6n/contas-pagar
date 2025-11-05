package br.com.deliverit.contas_pagar.service;

import br.com.deliverit.contas_pagar.dto.ContaRequestDTO;
import br.com.deliverit.contas_pagar.dto.ContaResponseDTO;
import br.com.deliverit.contas_pagar.entity.Conta;
import br.com.deliverit.contas_pagar.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ContaServiceTest {
    
    @Autowired
    private ContaService service;
    
    @Autowired
    private ContaRepository repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    @DisplayName("Deve calcular corretamente conta SEM atraso")
    void deveCriarContaSemAtraso() {
        // Arrange
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Conta Teste");
        dto.setValorOriginal(new BigDecimal("1000.00"));
        dto.setDataVencimento(LocalDate.now());
        dto.setDataPagamento(LocalDate.now());
        
        // Act
        ContaResponseDTO response = service.criar(dto);
        
        // Assert
        assertNotNull(response.getId());
        assertEquals("Conta Teste", response.getNome());
        assertEquals(new BigDecimal("1000.00"), response.getValorOriginal());
        assertEquals(new BigDecimal("1000.00"), response.getValorCorrigido());
        assertEquals(0, response.getDiasAtraso());
        assertEquals(new BigDecimal("0.00"), response.getPercentualMulta());
        assertEquals(new BigDecimal("0.000"), response.getPercentualJurosDia());
    }
    
    @Test
    @DisplayName("Deve calcular multa 2% e juros 0.1% para até 3 dias de atraso")
    void deveCalcularMulta2PorcentoAte3Dias() {
        // Arrange
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Conta Teste");
        dto.setValorOriginal(new BigDecimal("1000.00"));
        dto.setDataVencimento(LocalDate.now().minusDays(2));
        dto.setDataPagamento(LocalDate.now());
        
        // Act
        ContaResponseDTO response = service.criar(dto);
        
        // Assert
        assertEquals(2, response.getDiasAtraso());
        assertEquals(new BigDecimal("2.00"), response.getPercentualMulta());
        assertEquals(new BigDecimal("0.100"), response.getPercentualJurosDia());
        // 1000 + (1000 * 2%) + (1000 * 0.1% * 2) = 1000 + 20 + 2 = 1022
        assertEquals(new BigDecimal("1022.00"), response.getValorCorrigido());
    }
    
    @Test
    @DisplayName("Deve calcular multa 2% e juros 0.1% para exatamente 3 dias de atraso")
    void deveCalcularMulta2PorcentoPara3Dias() {
        // Arrange
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Conta Teste");
        dto.setValorOriginal(new BigDecimal("1000.00"));
        dto.setDataVencimento(LocalDate.now().minusDays(3));
        dto.setDataPagamento(LocalDate.now());
        
        // Act
        ContaResponseDTO response = service.criar(dto);
        
        // Assert
        assertEquals(3, response.getDiasAtraso());
        assertEquals(new BigDecimal("2.00"), response.getPercentualMulta());
        assertEquals(new BigDecimal("0.100"), response.getPercentualJurosDia());
        // 1000 + (1000 * 2%) + (1000 * 0.1% * 3) = 1000 + 20 + 3 = 1023
        assertEquals(new BigDecimal("1023.00"), response.getValorCorrigido());
    }
    
    @Test
    @DisplayName("Deve calcular multa 3% e juros 0.2% para 4 dias de atraso")
    void deveCalcularMulta3PorcentoPara4Dias() {
        // Arrange
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Conta Teste");
        dto.setValorOriginal(new BigDecimal("1000.00"));
        dto.setDataVencimento(LocalDate.now().minusDays(4));
        dto.setDataPagamento(LocalDate.now());
        
        // Act
        ContaResponseDTO response = service.criar(dto);
        
        // Assert
        assertEquals(4, response.getDiasAtraso());
        assertEquals(new BigDecimal("3.00"), response.getPercentualMulta());
        assertEquals(new BigDecimal("0.200"), response.getPercentualJurosDia());
        // 1000 + (1000 * 3%) + (1000 * 0.2% * 4) = 1000 + 30 + 8 = 1038
        assertEquals(new BigDecimal("1038.00"), response.getValorCorrigido());
    }
    
    @Test
    @DisplayName("Deve calcular multa 3% e juros 0.2% para 5 dias de atraso")
    void deveCalcularMulta3PorcentoPara5Dias() {
        // Arrange
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Conta Teste");
        dto.setValorOriginal(new BigDecimal("1000.00"));
        dto.setDataVencimento(LocalDate.now().minusDays(5));
        dto.setDataPagamento(LocalDate.now());
        
        // Act
        ContaResponseDTO response = service.criar(dto);
        
        // Assert
        assertEquals(5, response.getDiasAtraso());
        assertEquals(new BigDecimal("3.00"), response.getPercentualMulta());
        assertEquals(new BigDecimal("0.200"), response.getPercentualJurosDia());
        // 1000 + (1000 * 3%) + (1000 * 0.2% * 5) = 1000 + 30 + 10 = 1040
        assertEquals(new BigDecimal("1040.00"), response.getValorCorrigido());
    }
    
    @Test
    @DisplayName("Deve calcular multa 5% e juros 0.3% para mais de 5 dias de atraso")
    void deveCalcularMulta5PorcentoAcima5Dias() {
        // Arrange
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Conta Teste");
        dto.setValorOriginal(new BigDecimal("1000.00"));
        dto.setDataVencimento(LocalDate.now().minusDays(10));
        dto.setDataPagamento(LocalDate.now());
        
        // Act
        ContaResponseDTO response = service.criar(dto);
        
        // Assert
        assertEquals(10, response.getDiasAtraso());
        assertEquals(new BigDecimal("5.00"), response.getPercentualMulta());
        assertEquals(new BigDecimal("0.300"), response.getPercentualJurosDia());
        // 1000 + (1000 * 5%) + (1000 * 0.3% * 10) = 1000 + 50 + 30 = 1080
        assertEquals(new BigDecimal("1080.00"), response.getValorCorrigido());
    }
    
    @Test
    @DisplayName("Deve listar todas as contas cadastradas")
    void deveListarTodasContas() {
        // Arrange
        ContaRequestDTO dto1 = new ContaRequestDTO();
        dto1.setNome("Conta 1");
        dto1.setValorOriginal(new BigDecimal("100.00"));
        dto1.setDataVencimento(LocalDate.now());
        dto1.setDataPagamento(LocalDate.now());
        
        ContaRequestDTO dto2 = new ContaRequestDTO();
        dto2.setNome("Conta 2");
        dto2.setValorOriginal(new BigDecimal("200.00"));
        dto2.setDataVencimento(LocalDate.now().minusDays(2));
        dto2.setDataPagamento(LocalDate.now());
        
        service.criar(dto1);
        service.criar(dto2);
        
        // Act
        List<ContaResponseDTO> contas = service.listar();
        
        // Assert
        assertEquals(2, contas.size());
    }
    
    @Test
    @DisplayName("Deve persistir dias de atraso e percentuais no banco")
    void devePersistirDadosCalculados() {
        // Arrange
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Conta Teste");
        dto.setValorOriginal(new BigDecimal("500.00"));
        dto.setDataVencimento(LocalDate.now().minusDays(2));
        dto.setDataPagamento(LocalDate.now());
        
        // Act
        ContaResponseDTO response = service.criar(dto);
        
        // Assert - Buscar direto do banco
        Conta contaSalva = repository.findById(response.getId()).orElseThrow();
        assertEquals(2, contaSalva.getDiasAtraso());
        assertEquals(new BigDecimal("2.00"), contaSalva.getPercentualMulta());
        assertEquals(new BigDecimal("0.100"), contaSalva.getPercentualJurosDia());
    }
}