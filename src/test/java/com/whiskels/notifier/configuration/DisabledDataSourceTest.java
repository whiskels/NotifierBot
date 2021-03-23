package com.whiskels.notifier.configuration;

import com.whiskels.notifier.DisabledDataSourceConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = DataSource.class)
class DisabledDataSourceTest extends DisabledDataSourceConfiguration {
    @Autowired
    ApplicationContext context;

    @Test
    void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(DataSource.class));
    }
}
