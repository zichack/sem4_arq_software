package br.com.hexagonal.infra.adapters.out.soap;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.soap.NotaFiscalSoap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotaFiscalSoapSimulado implements NotaFiscalSoap {

  @Override
  public String emitirNotaFiscal(Fatura fatura) {
    log.info("[SOAP] Chamando serviço fiscal XML para fatura {} do pedido {}...",
      fatura.getId(), fatura.getPedidoId());

    // Simula a chave de acesso da NF-e com exatamente 44 dígitos
    StringBuilder chaveAcesso = new StringBuilder("4126");
    for (int i = 0; i < 40; i++) {
      chaveAcesso.append(ThreadLocalRandom.current().nextInt(10));
    }

    log.info("[SOAP] Nota fiscal emitida com chave de acesso: {}", chaveAcesso);
    return chaveAcesso.toString();
  }
}
