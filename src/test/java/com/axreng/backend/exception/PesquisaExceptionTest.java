package com.axreng.backend.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PesquisaExceptionTest {

    @Test
    public void testConstructorAndGetMessage() {
        String errorMessage = "Erro";
        PesquisaException exception = new PesquisaException(errorMessage);

        assertEquals(errorMessage, exception.getMessage(), "Erro");
    }

}
