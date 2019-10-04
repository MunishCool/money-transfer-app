package com.mybank.user;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mybank.entrypoint.Routing;
import com.mybank.user.repositories.UserRepositoryModule;
import com.mybank.user.services.UserServiceModule;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserController.class);
        install(new UserServiceModule());
        install(new UserRepositoryModule());
        Multibinder.newSetBinder(binder(), Routing.class).addBinding().to(UserRouting.class);
    }
}
