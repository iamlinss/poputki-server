package org.smirnova.poputka;

import org.smirnova.poputka.config.RsaKeyConfigProperties;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@EnableJpaRepositories
public class PoputkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoputkaApplication.class, args);
    }
//TODO: сущность с попутчиками и поездками id-id
//TODO: бронирование
//TODO: отмена бронирования
//TODO: отмена поездки(статус менять)
//TODO: отмена поездки(статус менять)




}
