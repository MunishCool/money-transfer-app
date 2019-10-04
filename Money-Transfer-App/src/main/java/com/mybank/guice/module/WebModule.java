package com.mybank.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.mybank.entrypoint.AppEntrypoint;
import com.mybank.entrypoint.EntrypointType;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

class WebModule extends AbstractModule {
    private Javalin app;

    private WebModule(Javalin app) {
        this.app = app;
    }

    @NotNull
    public static WebModule create() {
        return new WebModule(Javalin.create());
    }

    @Override
    protected void configure() {
        bind(Javalin.class).toInstance(app);
        MapBinder.newMapBinder(binder(), EntrypointType.class, AppEntrypoint.class).addBinding(EntrypointType.REST).to(WebEntrypoint.class);
    }
}
