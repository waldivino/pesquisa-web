package com.axreng.backend.context;

import com.axreng.backend.controller.PesquisaController;
import com.axreng.backend.service.PesquisaService;

import java.io.InputStream;
import java.util.Properties;

public class AppContext {
    private final PesquisaService pesquisaService;
    private final PesquisaController pesquisaController;
    private static final String CONFIG_FILE = "config.properties";
    public static String BASE_URL;
    public static String CRAWL_ROUTE;
    public static String RESULTS_ROUTE;

    static {
        initialize();
    }

    private static void initialize() {
        try (InputStream input = AppContext.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                BASE_URL = prop.getProperty("BASE_URL");
                CRAWL_ROUTE = prop.getProperty("crawl.route");
                RESULTS_ROUTE = prop.getProperty("results.route");
            } else {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    public AppContext() {
        this.pesquisaService = createPesquisaService();
        this.pesquisaController = createPesquisaController();
    }

    private PesquisaService createPesquisaService() {
        return new PesquisaService();
    }

    private PesquisaController createPesquisaController() {
        return new PesquisaController(getPesquisaService());
    }

    public PesquisaService getPesquisaService() {
        return pesquisaService;
    }

    public PesquisaController getPesquisaController() {
        return pesquisaController;
    }
}
