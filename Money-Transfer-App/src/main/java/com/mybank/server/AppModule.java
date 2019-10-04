package com.mybank.server;
import com.google.inject.AbstractModule;
import com.mybank.guice.module.MyBankModule;

public class AppModule extends AbstractModule {
    protected void configure() {
        bind(Startup.class);
        install(new MyBankModule());
        
    }
}
