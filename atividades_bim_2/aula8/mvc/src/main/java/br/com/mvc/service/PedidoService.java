package br.com.mvc.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.integration.HubFaturamentoClient;
import br.com.mvc.model.Pedido;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusPedido;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.repository.PedidoRepository;
import br.com.mvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

  private final PedidoRepository pedidoRepository;
  private final ProductRepository productRepository;
  private final EstoqueService estoqueService;
  private final HubFaturamentoClient hubFaturamentoClient;

  public List<Pedido> listar() {
    return pedidoRepository.findAll();
  }

  public Pedido criarPedido(Long productId, Integer quantidade) {
    if (quantidade == null || quantidade <= 0) {
      throw new IllegalArgumentException("A quantidade do pedido deve ser maior que zero.");
    }

    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + productId));

    if (product.getStatus() != StatusProduto.DISPONIVEL) {
      throw new IllegalStateException("Produto precisa estar DISPONÍVEL para abrir pedido.");
    }

    BigDecimal valorTotal = product.getPreco().multiply(BigDecimal.valueOf(quantidade));
    Pedido pedido = Pedido.builder()
      .product(product)
      .quantidade(quantidade)
      .valorTotal(valorTotal)
      .status(StatusPedido.ABERTO)
      .criadoEm(LocalDateTime.now())
      .build();

    return pedidoRepository.save(pedido);
  }

  @Transactional
  public Pedido fecharPedido(Long pedidoId) {
    Pedido pedido = pedidoRepository.findById(pedidoId)
      .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

    if (pedido.getStatus() != StatusPedido.ABERTO) {
      throw new IllegalStateException("Apenas pedidos em aberto podem ser fechados.");
    }

    estoqueService.registrarSaida(pedido.getProduct().getId(), pedido.getQuantidade());
    String faturaId = hubFaturamentoClient.faturarPedido(pedido.getId(), pedido.getValorTotal());
    pedido.setFaturaId(faturaId);
    pedido.setStatus(StatusPedido.FECHADO);
    return pedidoRepository.save(pedido);
  }
}
