package com.whiskels.notifier.infrastructure.config.feign;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class FixieProxyPropertiesProviderTest {

    @Mock
    private Environment environment;

    @Test
    @DisplayName("Should correctly initialize via constructor")
    void testConstructorSuccess() {
        when(environment.getProperty(FixieProxyPropertiesProvider.FIXIE_ENV_VAR))
                .thenReturn("http://user:password@host:8080");
        
        FixieProxyPropertiesProvider fixieProxyPropertiesProvider = new FixieProxyPropertiesProvider(environment);

        assertEquals("user", fixieProxyPropertiesProvider.getUser());
        assertEquals("password", fixieProxyPropertiesProvider.getPassword());
        assertEquals("host", fixieProxyPropertiesProvider.getHost());
        assertEquals(8080, fixieProxyPropertiesProvider.getPort());
    }

    @Test
    @DisplayName("Should fail to initialize via constructor when env var not present")
    void testConstructorFailure() {
        when(environment.getProperty(FixieProxyPropertiesProvider.FIXIE_ENV_VAR)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> new FixieProxyPropertiesProvider(environment));
    }
}