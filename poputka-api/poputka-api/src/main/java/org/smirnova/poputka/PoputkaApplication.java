package org.smirnova.poputka;

import org.smirnova.poputka.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
