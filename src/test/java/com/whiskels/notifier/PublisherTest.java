package com.whiskels.notifier;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@Import(PublisherTest.PublisherTestConfig.class)
public class PublisherTest<T> {
    @Autowired
    public ApplicationEventPublisher publisher;

    @Captor
    protected ArgumentCaptor<T> captor;

    @BeforeEach
    public void resetPublisher() {
        reset(publisher);
    }

    public final T getPublishedObject() {
        verify(publisher).publishEvent(captor.capture());
        return captor.getValue();
    }

    static class PublisherTestConfig {
        @Bean
        @Primary
        GenericApplicationContext genericApplicationContext(final GenericApplicationContext gac) {
            return Mockito.spy(gac);
        }
    }
}
