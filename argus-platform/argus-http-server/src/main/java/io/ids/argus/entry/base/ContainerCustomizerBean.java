package io.ids.argus.entry.base;

import io.ids.argus.center.conf.CenterProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author jalr4real[jalrhex@gmail.com]
 * @since 2023-11-06
 */
@Configuration
public class ContainerCustomizerBean implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    public void customize(ConfigurableServletWebServerFactory factory) {
        String httpServerPort = CenterProperties.get().getHTTPServerPort();
        if (Objects.nonNull(httpServerPort) && !"".equals(httpServerPort)) {
            factory.setPort(Integer.parseInt(httpServerPort));
        }

    }

}
