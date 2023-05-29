package ru.job4j.cinema.configuration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Configuration
public class DataSourceConfiguration {
    @Bean
    public DataSource connectionPool(@Value("${datasource.url}") String url,
                                     @Value("${datasource.username}") String username,
                                     @Value("${datasource.password}") String password) {
        return new BasicDataSource() {
            {
                setUrl(url);
                setUsername(username);
                setPassword(password);
            }
        };
    }

    @Bean
    public Sql2o databaseClient(DataSource dataSource) {
        return new Sql2o(dataSource, createConverters());
    }

    private Quirks createConverters() {
        return new NoQuirks() {
            {
                converters.put(LocalDateTime.class, new Converter<LocalDateTime>() {
                    @Override
                    public LocalDateTime convert(Object o) throws ConverterException {
                        if (o == null) {
                            return null;
                        }
                        if (!(o instanceof Timestamp)) {
                            throw new ConverterException("Invalid value to convert.");
                        }
                        return ((Timestamp) o).toLocalDateTime();
                    }

                    @Override
                    public Object toDatabaseParam(LocalDateTime localDateTime) {
                        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
                    }
                });
            }
        };
    }
}
