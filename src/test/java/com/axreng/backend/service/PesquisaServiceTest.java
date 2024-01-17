package com.axreng.backend.service;

import com.axreng.backend.model.PesquisaResults;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PesquisaServiceTest {

    @Test
    void testIsSameBaseUrl() {
        PesquisaService pesquisaService = new PesquisaService();
        String baseUrl = "https://fred.com";
        String url1 = "https://fred.com/path1";
        String url2 = "https://fred.org/path2";

        boolean result1 = pesquisaService.isSameBaseUrl(url1, baseUrl);
        boolean result2 = pesquisaService.isSameBaseUrl(url2, baseUrl);

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    void testIsValidKeyword() {
        PesquisaService pesquisaService = new PesquisaService();
        String validKeyword = "test";
        String invalidKeywordShort = "abc";
        String invalidKeywordLong = "abcdefghijklmnopqrstuvwxyzdsfgsdfg";

        boolean resultValid = pesquisaService.validaKeyword(validKeyword);
        boolean resultInvalidShort = pesquisaService.validaKeyword(invalidKeywordShort);
        boolean resultInvalidLong = pesquisaService.validaKeyword(invalidKeywordLong);

        assertTrue(resultValid);
        assertFalse(resultInvalidShort);
        assertFalse(resultInvalidLong);
    }

    @Test
    void testGenerateSearchId() {

        PesquisaService pesquisaService = new PesquisaService();

        String searchId = pesquisaService.gerarPesquisaId();

        assertNotNull(searchId);
        assertEquals(8, searchId.length());
    }

    @Test
    void testGetResultadosPesquisaNotFound() {
        String searchId = "123";
        PesquisaService pesquisaService = new PesquisaService();

        CompletableFuture<PesquisaResults> futureResults = pesquisaService.getResultadosPesquisa(searchId);

        PesquisaResults results = futureResults.join();

        assertNotNull(results);
        assertEquals("not-found", results.getStatus());
        assertNull(results.getUrls());
    }
}