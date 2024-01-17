package com.axreng.backend;

import com.axreng.backend.context.AppContext;
import com.axreng.backend.controller.PesquisaController;
import com.axreng.backend.model.PesquisaRequest;
import com.google.gson.Gson;
import spark.Spark;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MainApp {

    private final AppContext appContext;

    public MainApp(AppContext appContext) {
        this.appContext = appContext;
    }

    public void start() {
        PesquisaController pesquisaController = appContext.getPesquisaController();
        Spark.port(4567);
        final String[] searchId = {""};

        Spark.post(AppContext.CRAWL_ROUTE, "application/json", (request, response) -> {
            String requestBody = request.body();
            PesquisaRequest searchRequest = new Gson().fromJson(requestBody, PesquisaRequest.class);
            searchId[0] = pesquisaController.retornaId(searchRequest.getKeyword());

            if (searchId[0].contains("error")) {
                response.type("application/json");
                response.body(searchId[0]);
                response.status(400);
                return new Gson().toJson(Map.of("error", searchId[0]));
            }

            CompletableFuture<String> searchIdFuture = new CompletableFuture<>();
            new Thread(() -> {
                pesquisaController.iniciarRastreamento(request, response, searchId[0]);
                searchIdFuture.complete(searchId[0]);
            }).start();

            response.type("application/json");
            response.body(searchId[0]);
            response.status(200);

            return new Gson().toJson(Map.of("id", searchId[0]));
        });

        Spark.get(AppContext.RESULTS_ROUTE, "application/json", pesquisaController::obterResultados);
    }

    public static void main(String[] args) {
        AppContext appContext = new AppContext();
        MainApp mainApp = new MainApp(appContext);
        mainApp.start();
    }
}