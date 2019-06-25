package br.com.moneymovements.config;

import org.springframework.context.annotation.Configuration;

import br.com.moneymovements.domain.Account;

@Configuration
public class RepositoryConfig implements RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Account.class);
    }
}