package br.com.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mvc.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
