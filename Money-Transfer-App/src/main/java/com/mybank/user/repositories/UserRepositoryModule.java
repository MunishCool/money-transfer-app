package com.mybank.user.repositories;

import com.google.inject.AbstractModule;

public class UserRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(InMemoryUserRepository.class);
    }
}
