package com.axreng.backend.model;

import java.util.List;

public class PesquisaResults {
    private String id;
    private String status;
    private List<String> urls;

    public PesquisaResults(String id, String status, List<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }

    public String getStatus(){
        return this.status;
    }
    public List<String> getUrls(){
        return this.urls;
    }
}
