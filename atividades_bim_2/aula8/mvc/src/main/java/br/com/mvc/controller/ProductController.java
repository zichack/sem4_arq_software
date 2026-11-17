package br.com.mvc.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.model.PerfilUsuario;
import br.com.mvc.service.ProductService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public String listar(Model model) {
    model.addAttribute("products", productService.read());
    model.addAttribute("perfis", PerfilUsuario.values());
    return "products";
  }

  @PostMapping
  public String criar(
    @RequestParam String nome,
    @RequestParam BigDecimal preco,
    RedirectAttributes redirectAttributes
  ) {
    try {
      productService.create(nome, preco);
      redirectAttributes.addFlashAttribute("sucesso", "Produto criado com status INDISPONIVEL.");
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("erro", e.getMessage());
    }
    return "redirect:/products";
  }

  @PostMapping("/{id}/descontinuar")
  public String descontinuar(
    @PathVariable Long id,
    @RequestParam(defaultValue = "VENDEDOR") PerfilUsuario perfil,
    RedirectAttributes redirectAttributes
  ) {
    try {
      productService.descontinuar(id, perfil);
      redirectAttributes.addFlashAttribute("sucesso", "Produto descontinuado com sucesso.");
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("erro", e.getMessage());
    }
    return "redirect:/products";
  }
}
