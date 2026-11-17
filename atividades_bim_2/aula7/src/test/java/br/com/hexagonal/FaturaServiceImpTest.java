package br.com.hexagonal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.hexagonal.application.FaturaServiceImp;
import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.persistence.FaturaRepository;
import br.com.hexagonal.infra.adapters.out.messaging.BoletoMessagingSimulado;
import br.com.hexagonal.infra.adapters.out.smtp.EmailSmtpSimulado;
import br.com.hexagonal.infra.adapters.out.soap.NotaFiscalSoapSimulado;

class FaturaServiceImpTest {

  @Test
  void deveFaturarPedidoComBoletoNotaFiscalEEmail() {
    List<Fatura> salvas = new ArrayList<>();
    FaturaRepository repoFake = salvas::add;

    FaturaServiceImp service = new FaturaServiceImp(
      repoFake,
      new BoletoMessagingSimulado(),
      new NotaFiscalSoapSimulado(),
      new EmailSmtpSimulado()
    );

    UUID faturaId = service.faturarPedido(10L, new BigDecimal("250.00"));

    assertNotNull(faturaId);
    assertEquals(1, salvas.size());

    Fatura fatura = salvas.get(0);
    assertEquals(faturaId, fatura.getId());
    assertEquals(10L, fatura.getPedidoId());
    assertNotNull(fatura.getCodigoBarras());
    assertNotNull(fatura.getChaveAcesso());
    assertEquals(44, fatura.getChaveAcesso().length());
    assertTrue(fatura.getChaveAcesso().matches("\\d{44}"));
    assertNotNull(fatura.getHashEmail());
  }

  @Test
  void deveRejeitarChaveDeAcessoSem44Digitos() {
    Fatura fatura = Fatura.criar(1L, new BigDecimal("100.00"));
    assertThrows(RuntimeException.class, () -> fatura.inserirNotaFiscal("12345"));
  }
}
