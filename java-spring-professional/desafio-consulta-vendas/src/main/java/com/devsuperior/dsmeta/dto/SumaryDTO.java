package com.devsuperior.dsmeta.dto;

import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.entities.Seller;

public class SumaryDTO {

    private String sellerName;
    private Double total;

    public SumaryDTO() {
    }

    public SumaryDTO(String sellerName, Double total) {
        this.sellerName = sellerName;
        this.total = total;
    }

    public SumaryDTO(Seller entity) {
        sellerName = entity.getName();
        for (Sale sale : entity.getSales()) {
            total += sale.getAmount();
        }
    }

    public String getSellerName() {
        return sellerName;
    }

    public Double getTotal() {
        return total;
    }
}
