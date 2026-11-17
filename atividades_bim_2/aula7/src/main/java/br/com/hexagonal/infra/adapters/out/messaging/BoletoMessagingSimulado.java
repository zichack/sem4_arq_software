package br.com.hexagonal.infra.adapters.out.messaging;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.messaging.BoletoMessaging;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BoletoMessagingSimulado implements BoletoMessaging {

  @Override
  public String emitirBoleto(Fatura fatura) {
    log.info("[FILA] Enviando pedido {} (valor R$ {}) para emissão de boleto...",
      fatura.getPedidoId(), fatura.getValor());

    // Simula o código de barras retornado pela fila de retorno (47 dígitos)
    StringBuilder codigoBarras = new StringBuilder("23790");
    for (int i = 0; i < 42; i++) {
      codigoBarras.append(ThreadLocalRandom.current().nextInt(10));
    }

    log.info("[FILA] Boleto emitido para fatura {} com código de barras: {}",
      fatura.getId(), codigoBarras);
    return codigoBarras.toString();
  }
}
