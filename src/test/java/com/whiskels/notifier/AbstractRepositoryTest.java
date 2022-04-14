package com.whiskels.notifier;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnableJpaRepositories(basePackages = {"com.whiskels.*"})
@EntityScan("com.whiskels.*")
//@TestPropertySource(properties = {"spring.jpa.defer-datasource-initialization true")
public abstract class AbstractRepositoryTest {
}
