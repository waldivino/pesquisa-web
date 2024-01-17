package com.axreng.backend.exception;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeywordValidaExceptionTest {

    @Test
    public void testConstructorAndGetMessage() {
        String errorMessage = "Test error message";
        KeywordValidaException exception = new KeywordValidaException(errorMessage);

        assertEquals(errorMessage, exception.getMessage(), "Erro mensagem muito curta");
    }

    @Test
    public void testConstructorWithNullMessage() {
        KeywordValidaException exception = new KeywordValidaException(null);

        assertEquals(null, exception.getMessage(), "Erro de mensagem curta.");
    }

    @Test
    public void testConstructorWithEmptyMessage() {
        KeywordValidaException exception = new KeywordValidaException("");

        assertEquals("", exception.getMessage(), "Erro de mensagem.");
    }
}