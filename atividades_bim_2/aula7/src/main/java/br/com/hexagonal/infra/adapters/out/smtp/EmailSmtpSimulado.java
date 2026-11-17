package br.com.hexagonal.infra.adapters.out.smtp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.smtp.EmailSmtp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailSmtpSimulado implements EmailSmtp {

  @Override
  public String enviarEmail(Fatura fatura) {
    String conteudo = String.format(
      "Fatura %s | Pedido: %d | Valor: R$ %s | Boleto: %s | NF-e: %s",
      fatura.getId(),
      fatura.getPedidoId(),
      fatura.getValor(),
      fatura.getCodigoBarras(),
      fatura.getChaveAcesso()
    );

    log.info("[SMTP] Enviando e-mail ao cliente: {}", conteudo);
    String hash = gerarHashSha256(conteudo);
    log.info("[SMTP] E-mail enviado com hash do conteúdo: {}", hash);
    return hash;
  }

  private String gerarHashSha256(String texto) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(bytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Erro ao calcular hash do e-mail", e);
    }
  }
}
