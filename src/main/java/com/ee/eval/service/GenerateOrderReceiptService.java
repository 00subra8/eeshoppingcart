package com.ee.eval.service;


import com.ee.eval.configuration.ApplicationProperties;
import com.ee.eval.dao.EESCDao;
import com.ee.eval.exception.EESCInputException;
import com.ee.eval.exception.EESCInternalException;
import com.ee.eval.model.CartItem;
import com.ee.eval.model.CartOrder;
import com.ee.eval.model.ReceiptBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Objects;

public class GenerateOrderReceiptService {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ReceiptBuilder receiptBuilder;

    @Autowired
    private EESCDao eescDao;

    public String getReceipt(CartOrder cartOrder) {
        if (cartOrder == null || isCartOrderEmpty(cartOrder)) {
            throw new EESCInputException("Error while trying to generate receipt. No Order details found");
        }
        receiptBuilder.clear();

        receiptBuilder.addEntry(String.format("\t\t\t\t\t\t%s", applicationProperties.getName()))
                .addEntry(getNewLine())
                .addEntry(String.format("\t\t %s", applicationProperties.getTagLine()))
                .addEntry(getNewLine())
                .addEntry(String.format("\t\t\t\t\t\t%s", applicationProperties.getAddressLine1()))
                .addEntry(getNewLine())
                .addEntry(String.format("\t\t\t\t\t\t%s", applicationProperties.getAddressLine2()))
                .addEntry(getNewLine())
                .addEntry(String.format("\t\t\t\t\t\t%s", applicationProperties.getAddressLine3()))
                .addEntry(getNewLine())
                .addEntry(getNewLine())
                .addEntry(String.format("Time Of Order: %s", cartOrder.getOrderTimeStamp()))
                .addEntry(getNewLine())
                .addEntry(getNewLine())
                .addEntry(getNewLine())
                .addEntry(String.format("Item%-25sQuantity%-18sPrice", " ", " "))
                .addEntry(getNewLine());

        populateItemList(cartOrder);


        return receiptBuilder
                .addEntry(String.format("\t\t\t %s", applicationProperties.getBottomLine()))
                .addEntry(getNewLine())
                .build();
    }

    private boolean isCartOrderEmpty(CartOrder cartOrder) {
        if (cartOrder == null) {
            return true;
        }

        return CollectionUtils.isEmpty(cartOrder.getItemList());
    }

    private void populateItemList(CartOrder cartOrder) {
        if (CollectionUtils.isEmpty(cartOrder.getItemList())) {
            throw new EESCInternalException("Error while generating receipt. It's on the house");
        }

        cartOrder.getItemList().stream()
                .filter(Objects::nonNull)
                .forEach(this::addLineItem);

        addBillAmount(cartOrder.getTotalPrice(), cartOrder.getVat());
    }

    private void addBillAmount(BigDecimal totalPrice, BigDecimal vat) {
        receiptBuilder.addEntry(getNewLine())
                .addEntry(String.format("VAT (%s%%): %s", applicationProperties.getVatPercentage(), vat))
                .addEntry(getNewLine())
                .addEntry(getNewLine())
                .addEntry(String.format("BILL AMOUNT (With VAT): %s", totalPrice))
                .addEntry(getNewLine())
                .addEntry(getNewLine());
    }

    private void addLineItem(CartItem cartItem) {
        double price = eescDao.getPrice(cartItem.getProductName());
        double itemPrice = price * cartItem.getQuantity();

        receiptBuilder.addEntry(String.format("%s%-25s%s%-25s%s", cartItem.getProductName(), " ", cartItem.getQuantity(),
                " ", itemPrice))
                .addEntry(getNewLine());
    }

    private String getNewLine() {
        return String.format("%n");
    }
}
