package br.com.mvc.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.model.MovimentacaoEstoque;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.model.TipoMovimentacao;
import br.com.mvc.repository.EstoqueRepository;
import br.com.mvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstoqueService {

  private final EstoqueRepository estoqueRepository;
  private final ProductRepository productRepository;

  public List<MovimentacaoEstoque> listarMovimentacoes() {
    return estoqueRepository.findAllByOrderByDataHoraDesc();
  }

  @Transactional
  public MovimentacaoEstoque registrarEntrada(Long productId, Integer quantidade) {
    if (quantidade == null || quantidade <= 0) {
      throw new IllegalArgumentException("A quantidade de entrada deve ser maior que zero.");
    }

    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + productId));

    if (product.getStatus() == StatusProduto.DESCONTINUADO) {
      throw new IllegalStateException("Produto descontinuado não pode ser manipulado no estoque.");
    }

    int estoqueAnterior = product.getQuantidadeEstoque();
    int novoEstoque = estoqueAnterior + quantidade;
    product.setQuantidadeEstoque(novoEstoque);

    if (estoqueAnterior == 0 && novoEstoque > 0) {
      product.setStatus(StatusProduto.DISPONIVEL);
    }

    productRepository.save(product);

    MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
      .product(product)
      .tipo(TipoMovimentacao.ENTRADA)
      .quantidade(quantidade)
      .dataHora(LocalDateTime.now())
      .build();

    return estoqueRepository.save(movimentacao);
  }

  @Transactional
  public MovimentacaoEstoque registrarSaida(Long productId, Integer quantidade) {
    if (quantidade == null || quantidade <= 0) {
      throw new IllegalArgumentException("A quantidade de saída deve ser maior que zero.");
    }

    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + productId));

    if (product.getStatus() == StatusProduto.DESCONTINUADO) {
      throw new IllegalStateException("Produto descontinuado não pode ser manipulado no estoque.");
    }

    if (product.getStatus() == StatusProduto.INDISPONIVEL || product.getQuantidadeEstoque() <= 0) {
      throw new IllegalStateException("Produto sem estoque disponível para saída.");
    }

    if (product.getQuantidadeEstoque() < quantidade) {
      throw new IllegalStateException("Estoque insuficiente para realizar a saída.");
    }

    int novoEstoque = product.getQuantidadeEstoque() - quantidade;
    product.setQuantidadeEstoque(novoEstoque);

    if (novoEstoque == 0) {
      product.setStatus(StatusProduto.INDISPONIVEL);
    }

    productRepository.save(product);

    MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
      .product(product)
      .tipo(TipoMovimentacao.SAIDA)
      .quantidade(quantidade)
      .dataHora(LocalDateTime.now())
      .build();

    return estoqueRepository.save(movimentacao);
  }
}
