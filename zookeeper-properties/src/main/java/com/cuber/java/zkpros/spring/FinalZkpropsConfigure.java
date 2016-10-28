package com.cuber.java.zkpros.spring;

import org.springframework.beans.BeansException;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringValueResolver;

import java.util.Properties;

/**
 * Created by cuber on 2016/10/27.
 */
public class FinalZkpropsConfigure extends ZkPropsConfigurerSupport implements EnvironmentAware{
    public static final String LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME = "localProperties";
    public static final String ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME = "environmentProperties";
    private MutablePropertySources propertySources;
    private PropertySources appliedPropertySources;
    private Environment environment;

    public FinalZkpropsConfigure() {
    }

    public FinalZkpropsConfigure( int order) throws Exception {
        super(order);
    }

    public void setPropertySources(PropertySources propertySources) {
        this.propertySources = new MutablePropertySources(propertySources);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertySources == null) {
            this.propertySources = new MutablePropertySources();
            if (this.environment != null) {
                this.propertySources.addLast(new PropertySource("environmentProperties", this.environment) {
                    public String getProperty(String key) {
                        return ((Environment) this.source).getProperty(key);
                    }
                });
            }
            PropertiesPropertySource ex = new PropertiesPropertySource("localProperties", this.getResult());
            this.propertySources.addLast(ex);
        }

        this.processProperties(beanFactory, (ConfigurablePropertyResolver) (new PropertySourcesPropertyResolver(this.propertySources)));
        this.appliedPropertySources = this.propertySources;
    }

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, final ConfigurablePropertyResolver propertyResolver) throws BeansException {
        propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
        propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
        propertyResolver.setValueSeparator(this.valueSeparator);
        StringValueResolver valueResolver = new StringValueResolver() {
            public String resolveStringValue(String strVal) {
                String resolved = FinalZkpropsConfigure.this.ignoreUnresolvablePlaceholders ? propertyResolver.resolvePlaceholders(strVal) : propertyResolver.resolveRequiredPlaceholders(strVal);
                resolved = resolved.trim();
                return resolved.equals(FinalZkpropsConfigure.this.nullValue) ? null : resolved;
            }
        };
        this.doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) {
        throw new UnsupportedOperationException("Call processProperties(ConfigurableListableBeanFactory, ConfigurablePropertyResolver) instead");
    }

    public PropertySources getAppliedPropertySources() throws IllegalStateException {
        Assert.state(this.appliedPropertySources != null, "PropertySources have not get been applied");
        return this.appliedPropertySources;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
        try{
            initConfigure(environment);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


}