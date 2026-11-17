package br.com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.service.EstoqueService;
import br.com.mvc.service.ProductService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

  private final EstoqueService estoqueService;
  private final ProductService productService;

  @GetMapping
  public String telaEstoque(Model model) {
    model.addAttribute("products", productService.read());
    model.addAttribute("movimentacoes", estoqueService.listarMovimentacoes());
    return "estoque";
  }

  @PostMapping("/entrada")
  public String registrarEntrada(
    @RequestParam Long productId,
    @RequestParam Integer quantidade,
    RedirectAttributes redirectAttributes
  ) {
    try {
      estoqueService.registrarEntrada(productId, quantidade);
      redirectAttributes.addFlashAttribute("sucesso", "Entrada de estoque registrada.");
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("erro", e.getMessage());
    }
    return "redirect:/estoque";
  }

  @PostMapping("/saida")
  public String registrarSaida(
    @RequestParam Long productId,
    @RequestParam Integer quantidade,
    RedirectAttributes redirectAttributes
  ) {
    try {
      estoqueService.registrarSaida(productId, quantidade);
      redirectAttributes.addFlashAttribute("sucesso", "Saída de estoque registrada.");
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("erro", e.getMessage());
    }
    return "redirect:/estoque";
  }
}
