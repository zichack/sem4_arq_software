package br.com.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.model.PerfilUsuario;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.service.EstoqueService;
import br.com.mvc.service.ProductService;

@SpringBootTest
@Transactional
class EstoqueServiceTest {

  @Autowired
  private ProductService productService;

  @Autowired
  private EstoqueService estoqueService;

  @Test
  void deveIniciarProdutoComoIndisponivelEMudarStatusConformeEstoque() {
    Product p = productService.create("Mouse", new BigDecimal("89.90"));
    assertEquals(StatusProduto.INDISPONIVEL, p.getStatus());
    assertEquals(0, p.getQuantidadeEstoque());

    estoqueService.registrarEntrada(p.getId(), 5);
    Product atualizado = productService.findById(p.getId());
    assertEquals(5, atualizado.getQuantidadeEstoque());
    assertEquals(StatusProduto.DISPONIVEL, atualizado.getStatus());

    estoqueService.registrarSaida(p.getId(), 5);
    Product zerado = productService.findById(p.getId());
    assertEquals(0, zerado.getQuantidadeEstoque());
    assertEquals(StatusProduto.INDISPONIVEL, zerado.getStatus());

    assertThrows(IllegalStateException.class, () -> estoqueService.registrarSaida(p.getId(), 1));
  }

  @Test
  void somenteAdmPodeDescontinuarEProdutoDescontinuadoNaoManipulaEstoque() {
    Product p = productService.create("Teclado", new BigDecimal("199.90"));

    assertThrows(IllegalStateException.class, () -> productService.descontinuar(p.getId(), PerfilUsuario.ESTOQUE));
    assertThrows(IllegalStateException.class, () -> productService.descontinuar(p.getId(), PerfilUsuario.VENDEDOR));

    productService.descontinuar(p.getId(), PerfilUsuario.ADM);
    Product descontinuado = productService.findById(p.getId());
    assertEquals(StatusProduto.DESCONTINUADO, descontinuado.getStatus());

    assertThrows(IllegalStateException.class, () -> estoqueService.registrarEntrada(p.getId(), 10));
    assertThrows(IllegalStateException.class, () -> estoqueService.registrarSaida(p.getId(), 1));
  }
}
