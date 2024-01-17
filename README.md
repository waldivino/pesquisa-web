# pesquisa-web

### Autor: Waldivino Frederico Moreira Prudêncio

A realização de pesquisas em páginas da web é uma tarefa comum em muitos cenários de desenvolvimento, e Java oferece ferramentas poderosas para lidar com operações assíncronas. Neste artigo, exploraremos a implementação de pesquisas web assíncronas utilizando CompletableFuture e HttpURLConnection, destacando uma abordagem eficiente para buscar informações em várias páginas simultaneamente.

## Introdução
Ao lidar com a busca de informações em páginas web, é crucial otimizar o processo para torná-lo eficiente e responsivo. A combinação de CompletableFuture e HttpURLConnection oferece uma solução robusta para realizar pesquisas web de maneira assíncrona em Java.

Utilizando CompletableFuture para Assincronicidade
O CompletableFuture é uma classe introduzida no Java 8 que simplifica o trabalho com operações assíncronas. Permite a execução de tarefas de forma concorrente e a manipulação de resultados intermediários. No contexto de pesquisas web, isso é especialmente útil, pois podemos realizar várias buscas simultaneamente.

public CompletableFuture<PesquisaResults> realizarPesquisa(String searchId, String keyword) {

    CompletableFuture<PesquisaResults> futureResults = new CompletableFuture<>();

    // Configurações iniciais do futuro

    recuperar(BASE_URL, keyword, new HashSet<>(), matchingUrls);

    // Finalização da pesquisa e atualização do futuro

    return futureResults;

}

Neste método, um CompletableFuture é criado para representar os resultados da pesquisa. Após a configuração inicial, a pesquisa é iniciada chamando o método recuperar, permitindo que a busca ocorra de forma assíncrona.

Utilizando HttpURLConnection para Requisições HTTP
Para realizar as solicitações HTTP e obter o conteúdo das páginas web, utilizamos a classe HttpURLConnection. Essa classe proporciona uma interface fácil para estabelecer conexões e recuperar dados da web.

try {

    URL url = new URL(currentUrl);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setConnectTimeout(1000);

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

        // Processamento do conteúdo da página

    }

} catch (MalformedURLException e) {

    // Tratamento de URL mal formada

} catch (IOException e) {

    // Tratamento de erro de conexão

} catch (Exception e) {

    // Outras exceções

}

No trecho acima, uma conexão HTTP é estabelecida usando HttpURLConnection. O conteúdo da página é então processado a partir do fluxo de entrada fornecido pela conexão.

Explorando URLs de Forma Assíncrona
A lógica central da pesquisa ocorre no método recuperar, que explora URLs de forma assíncrona utilizando um algoritmo de busca em largura (BFS). Este método utiliza as funcionalidades do CompletableFuture para fornecer resultados intermediários à medida que as URLs são processadas.

public void recuperar(String startUrl, String keyword, Set<String> visitedUrls, List<String> matchingUrls) {

    Deque<String> urlQueue = new LinkedList<>();

    Set<String> processedUrls = new HashSet<>();

    urlQueue.offer(startUrl);

    while (!urlQueue.isEmpty()) {

        String currentUrl = urlQueue.poll();

        // Lógica de processamento da URL

    }

}

A utilização de uma fila (urlQueue) permite explorar as páginas web de forma sistemática, enquanto processedUrls evita o processamento repetido das mesmas URLs.

Resultados e Manipulação de CompletableFuture
A obtenção dos resultados da pesquisa é feita através do método getResultadosPesquisa, que manipula os resultados intermediários e lida com possíveis exceções.

public CompletableFuture<PesquisaResults> getResultadosPesquisa(String searchId) {

    CompletableFuture<PesquisaResults> future = searchFutures.get(searchId);

    if (future != null) {

        return future.thenApply(results -> {

            // Lógica de manipulação dos resultados

        }).exceptionally(e -> new PesquisaResults(searchId, "error", null));

    } else {

        return CompletableFuture.completedFuture(new PesquisaResults(searchId, "not-found", null));

    }

}

A manipulação de exceções e a criação de um CompletableFuture final garantem que os resultados sejam entregues de maneira consistente.

## Conclusão
A combinação de CompletableFuture e HttpURLConnection proporciona uma solução eficiente e assíncrona para realizar pesquisas em páginas web. O código apresentado demonstra como explorar URLs de forma concorrente, manipulando resultados intermediários de maneira eficaz. Ao utilizar essa abordagem, os desenvolvedores podem criar aplicações responsivas e capazes de lidar com operações web assíncronas de maneira elegante em Java.
