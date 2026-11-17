package br.com.mvc.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.mvc.model.PerfilUsuario;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public List<Product> read() {
    return productRepository.findAll();
  }

  public Product findById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
  }

  public Product create(String nome, BigDecimal preco) {
    if (nome == null || nome.isBlank()) {
      throw new IllegalArgumentException("O nome do produto é obrigatório.");
    }
    if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
    }

    Product product = Product.builder()
      .nome(nome.trim())
      .preco(preco)
      .quantidadeEstoque(0)
      .status(StatusProduto.INDISPONIVEL)
      .build();

    return productRepository.save(product);
  }

  public Product descontinuar(Long productId, PerfilUsuario perfil) {
    if (perfil != PerfilUsuario.ADM) {
      throw new IllegalStateException("Somente o administrador (ADM) pode descontinuar um produto.");
    }

    Product product = findById(productId);
    product.setStatus(StatusProduto.DESCONTINUADO);
    return productRepository.save(product);
  }
}
