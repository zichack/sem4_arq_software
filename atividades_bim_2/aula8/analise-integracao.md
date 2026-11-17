# Análise da Integração REST entre Vendas (MVC) e Hub de Faturamento (Hexagonal)

## Visão Geral da Integração

Na aplicação de vendas em MVC (`PedidoService.fecharPedido`), ao fechar um pedido é feita uma chamada síncrona via REST (`POST http://localhost:8081/fatura`) para o hub de faturamento em arquitetura hexagonal, enviando `pedidoId` e `valor`, e aguardando o `faturaId` gerado para salvá-lo no pedido.

Como essa comunicação passa pela rede entre dois processos distintos, ela está sujeita a falhas distribuídas. Abaixo estão dois problemas possíveis nessa integração e suas respectivas soluções.

---

## Problema 1: Atraso na resposta do Hub de Faturamento (Lentidão / Falha em Cascata)

### O que acontece com o pedido
Como a chamada REST no `PedidoService` é síncrona, a aplicação MVC fica parada esperando o retorno do hub. Se o hub demorar muito para responder (por exemplo, porque o serviço SOAP da nota fiscal ou o servidor SMTP travou), a requisição do fechamento do pedido fica presa aguardando até estourar o timeout ou cair por erro de conexão. Enquanto isso, a transação do pedido não conclui e as threads do servidor MVC vão sendo ocupadas por requisições pendentes.

### O que o negócio percebe
O vendedor clica para fechar o pedido e a tela fica carregando por muito tempo até dar erro. Se vários vendedores tentarem fechar vendas ao mesmo tempo durante uma lentidão do hub, a própria aplicação de vendas para de responder (falha em cascata), impedindo novas vendas mesmo que o problema esteja apenas no faturamento externo.

### Solução indicada: Timeout + Circuit Breaker
- **Onde atua:** Na **aplicação de vendas (MVC)**, mais especificamente no cliente REST (`HubFaturamentoClient`) que faz a chamada ao hub.
- **Justificativa:**
  - O **Timeout** (por exemplo, 3 segundos de leitura) evita que a thread do MVC fique presa indefinidamente esperando um hub lento.
  - O **Circuit Breaker** (com os estados *Fechado*, *Aberto* e *Meio Aberto*) monitora as falhas consecutivas e, quando percebe que o hub está fora ou instável, abre o circuito para falhar imediatamente sem nem tentar novas conexões por um tempo. Assim, a aplicação de vendas se protege contra falha em cascata e pode tratar o pedido (por exemplo, devolvendo um aviso rápido ao vendedor ou marcando o faturamento como pendente para reprocessamento).

---

## Problema 2: Requisição duplicada após falha de rede ou timeout (Repetição)

### O que acontece com o pedido
O MVC envia `POST /fatura` para o hub. O hub recebe o pedido, cria a fatura, emite o boleto, gera a nota fiscal, envia o e-mail e salva tudo no banco de dados. Porém, antes de a resposta HTTP `201 Created` voltar para o MVC, ocorre uma queda de rede ou o timeout estoura no cliente. Para a aplicação de vendas, o pedido continua sem `faturaId` (ou com erro no fechamento), levando o vendedor a clicar novamente em "Fechar Pedido" (ou um mecanismo de *retry* reenviar a requisição). O hub recebe o mesmo `pedidoId` uma segunda vez e executa todo o processo de faturamento novamente, criando uma segunda fatura para o mesmo pedido.

### O que o negócio percebe
O pedido acaba gerando duas faturas, dois boletos e duas notificações por e-mail para o mesmo cliente, causando cobrança duplicada, inconsistência financeira e necessidade de atendimento manual para cancelar o documento extra.

### Solução indicada: Idempotência
- **Onde atua:** No **Hub de Faturamento (arquitetura hexagonal)**, na camada de aplicação (`FaturaServiceImp`) em conjunto com a porta de persistência (`FaturaRepository` / restrição única no banco para `pedido_id`).
- **Justificativa:** Tornando a operação de faturamento **idempotente** por `pedidoId`, antes de criar uma nova fatura e chamar boleto/nota/e-mail, o hub verifica se já existe uma fatura registrada para aquele `pedidoId`. Caso já exista, o hub não reprocessa os serviços externos e apenas devolve o `faturaId` já existente. Dessa forma, mesmo que a aplicação MVC repita a chamada REST por causa de um timeout, o resultado final continua sendo apenas uma fatura gerada.
