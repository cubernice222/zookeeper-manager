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
    @Value("${zookeeper.manage.sitpwd}")
    private String sitPassword;

    @Value("${zookeeper.manage.devpwd}")
    private String devPassword;

    @Value("${zookeeper.manage.prepwd}")
    private String prePassword;

    @Value("${zookeeper.manage.prodpwd}")
    private String prodPassword;

    @Value("${zookeeper.manage.adminPwd}")
    private String adminPwd;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/**").permitAll().anyRequest()
                .fullyAuthenticated().and().formLogin().loginPage("/login.htm")
                .failureUrl("/login.htm?error").loginProcessingUrl("/login")  //very import add
                .permitAll().and().logout().permitAll();
        http.csrf().disable();
    }



    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password(adminPwd).roles("admin");
        auth.inMemoryAuthentication()
                .withUser("dev").password(devPassword).roles("Zkdev");
        auth.inMemoryAuthentication()
                .withUser("sit").password(sitPassword).roles("Zksit","Zkdev");
        auth.inMemoryAuthentication()
                .withUser("pre").password(prePassword).roles("Zkpre","Zkdev");
        auth.inMemoryAuthentication()
                .withUser("prod").password(prodPassword).roles("Zkprod","Zkdev");
    }

}
