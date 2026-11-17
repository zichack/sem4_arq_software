package br.com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.service.PedidoService;
import br.com.mvc.service.ProductService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

  private final PedidoService pedidoService;
  private final ProductService productService;

  @GetMapping
  public String listar(Model model) {
    model.addAttribute("pedidos", pedidoService.listar());
    model.addAttribute("products", productService.read());
    return "pedidos";
  }

  @PostMapping
  public String criar(
    @RequestParam Long productId,
    @RequestParam Integer quantidade,
    RedirectAttributes redirectAttributes
  ) {
    try {
      pedidoService.criarPedido(productId, quantidade);
      redirectAttributes.addFlashAttribute("sucesso", "Pedido criado com sucesso.");
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("erro", e.getMessage());
    }
    return "redirect:/pedidos";
  }

  @PostMapping("/{id}/fechar")
  public String fechar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      pedidoService.fecharPedido(id);
      redirectAttributes.addFlashAttribute("sucesso", "Pedido fechado com sucesso.");
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("erro", e.getMessage());
    }
    return "redirect:/pedidos";
  }
}
