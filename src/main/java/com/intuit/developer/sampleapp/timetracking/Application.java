package com.intuit.developer.sampleapp.timetracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.intuit.developer.sampleapp.timetracking.controllers.OAuthInfoProviderImpl;
import com.intuit.developer.sampleapp.timetracking.domain.Company;
import com.intuit.developer.sampleapp.timetracking.handlers.RoleEventHandler;
import com.intuit.developer.sampleapp.timetracking.qbo.DataServiceFactory;
import com.intuit.developer.sampleapp.timetracking.qbo.QBODataManager;
import com.intuit.developer.sampleapp.timetracking.serializers.MoneyDeserializer;
import com.intuit.developer.sampleapp.timetracking.serializers.MoneySerializer;
import oauth.OAuthInfoProvider;
import org.apache.commons.io.FileUtils;
import org.joda.money.Money;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import java.io.File;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/18/14
 * Time: 9:33 AM
 */


@Configuration
@EnableJpaRepositories
@Import(RepositoryRestMvcConfiguration.class)
@EnableAutoConfiguration
@ComponentScan
public class Application extends RepositoryRestMvcConfiguration {

    public static void main(String[] args) {

        try {
            FileUtils.deleteDirectory(new File("database"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        DataLoader.initializeData(context);
    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setReturnBodyOnCreate(true);
        config.exposeIdsFor(Company.class);
    }

    @Override
    protected void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        //add validators here
//        validatingListener.addValidator("beforeCreate", new SyncRequestValidator());
    }


    //add REST event handler beans here
    @Bean
    RoleEventHandler roleEventHandler() {
        return new RoleEventHandler();
    }

    @Override
    protected void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        final SimpleModule myCustomModule = new SimpleModule("MyCustomModule");

        myCustomModule.addSerializer(Money.class, new MoneySerializer());
        myCustomModule.addDeserializer(Money.class, new MoneyDeserializer());

        objectMapper.registerModule(myCustomModule);

    }

    @Bean
    OAuthInfoProvider oAuthInfoProvider() {
        return new OAuthInfoProviderImpl();
    }

    @Bean
    QBODataManager qboDataManager() {
        return new QBODataManager();
    }

    @Bean
    DataServiceFactory dataServiceFactory() {
        return new DataServiceFactory();
    }
}