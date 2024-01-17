package com.axreng.backend.service;

import com.axreng.backend.model.PesquisaResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.axreng.backend.context.AppContext.BASE_URL;

public class PesquisaService {

    private static final Logger logger = Logger.getLogger(PesquisaService.class.getName());
    private final Map<String, CompletableFuture<PesquisaResults>> searchFutures = new ConcurrentHashMap<>();
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    public CompletableFuture<PesquisaResults> realizarPesquisa(String searchId, String keyword) {
        CompletableFuture<PesquisaResults> futureResults = new CompletableFuture<>();
        searchFutures.put(searchId, futureResults);

        List<String> matchingUrls = new CopyOnWriteArrayList<>();
        futureResults.complete(new PesquisaResults(searchId, "active", matchingUrls));
        recuperar(BASE_URL, keyword, new HashSet<>(), matchingUrls);
        searchFutures.remove(searchId, futureResults);
        futureResults = new CompletableFuture<>();
        futureResults.complete(new PesquisaResults(searchId, "done", matchingUrls));
        searchFutures.put(searchId, futureResults);

        return futureResults;
    }

    public void recuperar(String startUrl, String keyword, Set<String> visitedUrls, List<String> matchingUrls) {
        Deque<String> urlQueue = new LinkedList<>();
        Set<String> processedUrls = new HashSet<>();
        urlQueue.offer(startUrl);

        while (!urlQueue.isEmpty()) {
            String currentUrl = urlQueue.poll();

            if (!visitedUrls.contains(currentUrl) && isSameBaseUrl(currentUrl, startUrl)) {
                visitedUrls.add(currentUrl);

                try {
                    URL url = new URL(currentUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(1000);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder content = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            content.append(line);
                        }

                        String htmlContent = content.toString().toLowerCase();
                        Pattern pattern = Pattern.compile(messages.getString("regex.url"), Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(htmlContent);

                        int keywordIndex = htmlContent.indexOf(keyword.toLowerCase());
                        int startIndex = Math.max(0, keywordIndex - 16);
                        int endIndex = Math.min(htmlContent.length(), keywordIndex + keyword.length() + 16);
                        endIndex = Math.min(endIndex, htmlContent.length());

                        String contextSnippet = htmlContent.substring(startIndex, endIndex);

                        while (matcher.find()) {
                            String nextUrl = construirUrlAbsoluta(currentUrl, matcher.group(1));
                            if (nextUrl.startsWith(BASE_URL) && !visitedUrls.contains(nextUrl)) {
                                urlQueue.offer(nextUrl);

                                if (htmlContent.toLowerCase().contains(keyword.toLowerCase()) && !processedUrls.contains(nextUrl)) {
                                    logger.info(String.format(messages.getString("parte.contexto.msn.url"), url, contextSnippet));
                                    processedUrls.add(nextUrl);
                                    matchingUrls.add(nextUrl);
                                }
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    logger.log(Level.SEVERE, messages.getString("error.url.mal.formada") + currentUrl);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, messages.getString("error.conecta.url") + currentUrl);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, messages.getString("error.codigo.resposta") + currentUrl);
                }
            }
        }
    }

    public boolean isSameBaseUrl(String url, String baseUrl) {
        return url.startsWith(baseUrl);
    }

    public boolean validaKeyword(String keyword) {
        return keyword != null && keyword.length() >= 4 && keyword.length() <= 32;
    }

    public String gerarPesquisaId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public CompletableFuture<PesquisaResults> getResultadosPesquisa(String searchId) {
        CompletableFuture<PesquisaResults> future = searchFutures.get(searchId);

        if (future != null) {
            return future.thenApply(results -> {
                if (results != null && future.isCompletedExceptionally()) {
                    return new PesquisaResults(searchId, "done", results.getUrls());
                } else {
                    return new PesquisaResults(searchId, results.getStatus(), results.getUrls());
                }
            }).exceptionally(e -> new PesquisaResults(searchId, "error", null));
        } else {
            return CompletableFuture.completedFuture(new PesquisaResults(searchId, "not-found", null));
        }
    }


    private static String construirUrlAbsoluta(String baseUrl, String relativeUrl) {
        URL base;
        try {
            base = new URL(baseUrl);
            return new URL(base, relativeUrl).toString();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        return baseUrl;
    }
}