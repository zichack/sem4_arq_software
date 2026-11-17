package br.com.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mvc.model.MovimentacaoEstoque;

public interface EstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
  List<MovimentacaoEstoque> findAllByOrderByDataHoraDesc();
}
