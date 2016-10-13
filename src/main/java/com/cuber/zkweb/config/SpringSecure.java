package com.cuber.zkweb.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by cuber on 2016/9/7.
 */
@Configuration
@AutoConfigureBefore(SecurityAutoConfiguration.class)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SpringSecure extends WebSecurityConfigurerAdapter {
    @Value("${zookeeper.manage.sit}")
    private String sitPassword;

    @Value("${zookeeper.manage.dev}")
    private String devPassword;

    @Value("${zookeeper.manage.pre}")
    private String prePassword;

    @Value("${zookeeper.manage.prod}")
    private String prodPassword;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/**").permitAll().anyRequest()
                .fullyAuthenticated().and().formLogin().loginPage("/login.htm")
                .failureUrl("/login.htm?error").loginProcessingUrl("/login")  //very import add
                .permitAll().and().logout().permitAll();
    }



    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("dev").password(devPassword).roles("dev");
        auth.inMemoryAuthentication()
                .withUser("sit").password(sitPassword).roles("sit","dev");
        auth.inMemoryAuthentication()
                .withUser("pre").password(prePassword).roles("pre","dev");
        auth.inMemoryAuthentication()
                .withUser("prod").password(prodPassword).roles("prod","dev");
    }

}
