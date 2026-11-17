package br.com.mvc.integration;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HubFaturamentoClient {

  private final RestClient restClient;

  public HubFaturamentoClient(@Value("${hub.faturamento.url}") String hubUrl) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(3));
    factory.setReadTimeout(Duration.ofSeconds(3));

    this.restClient = RestClient.builder()
      .baseUrl(hubUrl)
      .requestFactory(factory)
      .build();
  }

  public String faturarPedido(Long pedidoId, BigDecimal valor) {
    FaturaPostResponse response = restClient.post()
      .uri("/fatura")
      .contentType(MediaType.APPLICATION_JSON)
      .body(new FaturaPostRequest(pedidoId, valor))
      .retrieve()
      .body(FaturaPostResponse.class);

    if (response == null || response.faturaId() == null || response.faturaId().isBlank()) {
      throw new IllegalStateException("O hub de faturamento não retornou o ID da fatura.");
    }
    return response.faturaId();
  }

  public record FaturaPostRequest(Long pedidoId, BigDecimal valor) {
  }

  public record FaturaPostResponse(String faturaId) {
  }
}
