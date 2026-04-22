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
        when(environment.getProperty(FixieProxyPropertiesProvider.USER_ENV_VAR))
                .thenReturn("admin");
        when(environment.getProperty(FixieProxyPropertiesProvider.PASSWORD_ENV_VAR))
                .thenReturn("pw");
        
        FixieProxyPropertiesProvider fixieProxyPropertiesProvider = new FixieProxyPropertiesProvider(environment);

        assertEquals("user", fixieProxyPropertiesProvider.getProxyUser());
        assertEquals("password", fixieProxyPropertiesProvider.getProxyPassword());
        assertEquals("host", fixieProxyPropertiesProvider.getProxyHost());
        assertEquals(8080, fixieProxyPropertiesProvider.getProxyPort());
        assertEquals("admin", fixieProxyPropertiesProvider.getUser());
        assertEquals("pw", fixieProxyPropertiesProvider.getPassword());
    }

    @Test
    @DisplayName("Should fail to initialize via constructor when env var not present")
    void testConstructorFailure() {
        when(environment.getProperty(FixieProxyPropertiesProvider.FIXIE_ENV_VAR)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> new FixieProxyPropertiesProvider(environment));
    }
}