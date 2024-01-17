package com.axreng.backend.controller;

import com.axreng.backend.exception.KeywordValidaException;
import com.axreng.backend.exception.PesquisaException;
import com.axreng.backend.model.PesquisaRequest;
import com.axreng.backend.model.PesquisaResults;
import com.axreng.backend.service.PesquisaService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class PesquisaController {

    private static final Logger logger = LoggerFactory.getLogger(PesquisaController.class);
    private final PesquisaService pesquisaService;
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    public PesquisaController(PesquisaService pesquisaService) {
        this.pesquisaService = pesquisaService;
    }

    public String retornaId(String keyWord){
        if (!pesquisaService.validaKeyword(keyWord)) {
            return new Gson().toJson(Map.of("error", messages.getString("error.invalid.keyword")));
        }
        return pesquisaService.gerarPesquisaId();
    }

    public String iniciarRastreamento(Request request, Response response, String searchId) {
        try {
            logger.info(messages.getString("inicio"));
            String requestBody = request.body();
            PesquisaRequest searchRequest = new Gson().fromJson(requestBody, PesquisaRequest.class);

            CompletableFuture<PesquisaResults> retorno = pesquisaService.realizarPesquisa(searchId, searchRequest.getKeyword());

            if (Objects.isNull(retorno)) {
                response.status(HTTP_BAD_REQUEST);
                return new Gson().toJson(Map.of("error", messages.getString("error.search.not.found")));
            }

            response.status(HTTP_OK);
            return new Gson().toJson(Map.of("id", searchId));
        } catch (KeywordValidaException e) {
            logger.warn(messages.getString("palavra.invalida"), e);
            response.status(HTTP_BAD_REQUEST);
            return new Gson().toJson(Map.of("error", messages.getString("error.invalid.keyword")));
        } catch (PesquisaException e) {
            logger.error(messages.getString("error.rastreamento"), e);
            response.status(SC_INTERNAL_SERVER_ERROR);
            return new Gson().toJson(Map.of("error", messages.getString("error.internal.server")));
        }
    }

    public String obterResultados(Request request, Response response) {
        String searchId = request.params(":id");
        CompletableFuture<PesquisaResults> futureResults = pesquisaService.getResultadosPesquisa(searchId);

        if (futureResults != null) {
            try {
                PesquisaResults results = futureResults.join();
                if (results != null) {
                    response.status(HTTP_OK);
                    response.type(messages.getString("app.json"));
                    return new Gson().toJson(results);
                }
            } catch (Exception e) {
                response.status(HTTP_INTERNAL_ERROR);
                response.type(messages.getString("app.json"));
                return new Gson().toJson(Map.of("error", messages.getString("error.internal.server")));
            }
        }
        response.status(HTTP_NOT_FOUND);
        response.type(messages.getString("app.json"));
        return new Gson().toJson(Map.of("error", messages.getString("error.search.not.found")));
    }
}