package com.ee.eval.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class CartOrder {
    private List<CartItem> itemList;
    private Timestamp orderTimeStamp;
    private BigDecimal vat;
    private BigDecimal totalPrice;

    public List<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<CartItem> itemList) {
        this.itemList = itemList;
    }

    public Timestamp getOrderTimeStamp() {
        return orderTimeStamp;
    }

    public void setOrderTimeStamp(Timestamp orderTimeStamp) {
        this.orderTimeStamp = orderTimeStamp;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }
}
