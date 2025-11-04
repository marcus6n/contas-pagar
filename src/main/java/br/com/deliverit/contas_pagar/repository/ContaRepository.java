package br.com.deliverit.contas_pagar.repository;

import br.com.deliverit.contas_pagar.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
}