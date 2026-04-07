package com.alan.sistema.dto.integration.asaas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsaasCustomerListResponseDTO {

    private String object;
    private boolean hasMore;
    private int totalCount;
    private int limit;
    private int offset;
    private List<AsaasCustomerCreateResponseDTO> data; // Reutilizamos o DTO de resposta individual

    // Getters e Setters
    public List<AsaasCustomerCreateResponseDTO> getData() {
        return data;
    }

    public void setData(List<AsaasCustomerCreateResponseDTO> data) {
        this.data = data;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}