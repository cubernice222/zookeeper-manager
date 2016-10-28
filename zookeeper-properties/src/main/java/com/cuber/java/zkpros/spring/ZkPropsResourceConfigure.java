package com.cuber.java.zkpros.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by cuber on 2016/9/6.
 */
public abstract class ZkPropsResourceConfigure extends ZkPropsResource
        implements BeanFactoryPostProcessor, PriorityOrdered {

    public ZkPropsResourceConfigure() {
        super();
    }

    public ZkPropsResourceConfigure( int order) throws Exception {
        this.order = order;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            Properties mergedProps = this.getResult();

            // Convert the merged properties, if necessary.
            convertProperties(mergedProps);

            // Let the subclass process the properties.
            processProperties(beanFactory, mergedProps);
        }
        catch (Exception ex) {
            throw new BeanInitializationException("Could not load properties", ex);
        }
    }

    protected abstract void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
            throws BeansException;

    protected void convertProperties(Properties props) {
        Enumeration<?> propertyNames = props.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            String propertyValue = props.getProperty(propertyName);
            String convertedValue = convertProperty(propertyName, propertyValue);
            if (!ObjectUtils.nullSafeEquals(propertyValue, convertedValue)) {
                props.setProperty(propertyName, convertedValue);
            }
        }
    }

    protected String convertProperty(String propertyName, String propertyValue) {
        return convertPropertyValue(propertyValue);
    }

    protected String convertPropertyValue(String originalValue) {
        return originalValue;
    }


    private int order = Ordered.LOWEST_PRECEDENCE;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }


}
