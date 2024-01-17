package com.axreng.backend.controller;

import com.axreng.backend.exception.KeywordValidaException;
import com.axreng.backend.exception.PesquisaException;
import com.axreng.backend.model.PesquisaResults;
import com.axreng.backend.service.PesquisaService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PesquisaControllerTest {

    @Mock
    private PesquisaService pesquisaService;

    @InjectMocks
    private PesquisaController pesquisaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRetornaIdValidKeyword() {
        String keyword = "validKeyword";
        String generatedId = "generatedId";

        when(pesquisaService.validaKeyword(keyword)).thenReturn(true);
        when(pesquisaService.gerarPesquisaId()).thenReturn(generatedId);

        String result = pesquisaController.retornaId(keyword);

        assertEquals("generatedId", result);
    }

    @Test
    void testRetornaIdInvalidKeyword() {
        String invalidKeyword = "invalidKeyword";

        when(pesquisaService.validaKeyword(invalidKeyword)).thenReturn(false);

        String result = pesquisaController.retornaId(invalidKeyword);

        assertEquals(new Gson().toJson(Map.of("error", ResourceBundle.getBundle("messages").getString("error.invalid.keyword"))), result);
    }

    @Test
    void testObterResultadosNotFoundError() throws KeywordValidaException, PesquisaException {
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        when(request.params(":id")).thenReturn("123");
        when(pesquisaService.getResultadosPesquisa("123")).thenReturn(null);

        Object result = pesquisaController.obterResultados(request, response);

        verify(response, times(1)).status(404);
        assertEquals("{\"error\":\"Pesquisa não encontrada.\"}", result);
    }

    @Test
    void iniciarRastreamento_Success() {
        Request request = mock(Request.class);
        when(request.body()).thenReturn("{\"keyword\":\"test\"}");

        CompletableFuture<PesquisaResults> futureResults = new CompletableFuture<>();
        when(pesquisaService.realizarPesquisa(any(), eq("test"))).thenReturn(futureResults);

        Response response = mock(Response.class);

        String result = pesquisaController.iniciarRastreamento(request, response, "searchId");

        verify(pesquisaService).realizarPesquisa("searchId", "test");

        futureResults.complete(new PesquisaResults("searchId", "done", null));

        verify(response).status(200);
        assertEquals("{\"id\":\"searchId\"}", result);
    }
}
