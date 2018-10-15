package money.rbk.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 * Freemarker configuration.
 * I am using freemarker templates to construct reports
 */
@Configuration
public class FreemarkerConfig {

    @Value("${freemarker.config.template.loader.path}") // freemarker template loader path
    private String loaderPath;

    @Value("${freemarker.config.template.encoding}") // freemarker template encoding
    private String encoding;

    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath(loaderPath);
        bean.setDefaultEncoding(encoding);
        return bean;
    }
}
