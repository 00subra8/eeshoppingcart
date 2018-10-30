package com.ee.eval.configuration;

import com.ee.eval.dao.EESCDao;
import com.ee.eval.helper.ActionsControllerHelper;
import com.ee.eval.model.ReceiptBuilder;
import com.ee.eval.service.GenerateOrderReceiptService;
import com.ee.eval.service.InputValidatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EESCConfiguration {

    @Bean
    public ApplicationProperties applicationProperties() {
        return new ApplicationProperties();
    }

    @Bean
    public InputValidatorService inputValidatorService() {
        return new InputValidatorService();
    }

    @Bean
    public EESCDao eescdao() {
        return new EESCDao();
    }

    @Bean
    public GenerateOrderReceiptService generateOrderReceiptService() {
        return new GenerateOrderReceiptService();
    }

    @Bean
    public ReceiptBuilder receiptBuilder() {
        return new ReceiptBuilder();
    }

    @Bean
    public ActionsControllerHelper actionsControllerHelper() {
        return new ActionsControllerHelper();
    }
}
