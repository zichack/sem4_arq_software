package br.com.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.integration.HubFaturamentoClient;
import br.com.mvc.model.Pedido;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusPedido;
import br.com.mvc.service.EstoqueService;
import br.com.mvc.service.PedidoService;
import br.com.mvc.service.ProductService;

@SpringBootTest
@Transactional
class PedidoServiceIntegrationTest {

  @Autowired
  private ProductService productService;

  @Autowired
  private EstoqueService estoqueService;

  @Autowired
  private PedidoService pedidoService;

  @MockitoBean
  private HubFaturamentoClient hubFaturamentoClient;

  @Test
  void deveFecharPedidoEGuardarIdDaFaturaRetornadoPeloHub() {
    Product p = productService.create("Monitor", new BigDecimal("1200.00"));
    estoqueService.registrarEntrada(p.getId(), 3);

    Pedido pedido = pedidoService.criarPedido(p.getId(), 2);

    when(hubFaturamentoClient.faturarPedido(eq(pedido.getId()), any(BigDecimal.class)))
      .thenReturn("fatura-uuid-1234");

    Pedido fechado = pedidoService.fecharPedido(pedido.getId());

    assertEquals(StatusPedido.FECHADO, fechado.getStatus());
    assertEquals("fatura-uuid-1234", fechado.getFaturaId());
  }
}
