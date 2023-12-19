package com.ps20652.DATN.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private UserDetailsService userDetailsService;
        
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        
        @Bean
        public SpringSecurityDialect springSecurityDialect(){
            return new SpringSecurityDialect();
        }


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                .antMatchers("/admin/accounts", "/admin/revenue-chart", "/admin/history").hasRole("ADMIN")
                .antMatchers("/admin/products", "/admin/feedback", "/admin/orders", "/admin/vouchers", "/admin/category").hasAnyRole("ADMIN", "STAFF")
                .antMatchers("/cart", "/pay").authenticated() // Các URL yêu cầu xác thực
                .anyRequest().permitAll() // Các URL còn lại cho phép truy cập không yêu cầu xác thực
                .and()
                .formLogin()
        	.loginPage("/login")
        	.defaultSuccessUrl("/")
        	.failureUrl("/login?error") // Đường dẫn khi đăng nhập không thành công
        	.permitAll()
        .failureHandler((request, response, exception) -> {
            String errorMessage = "Sai thông tin đăng nhập";
            request.getSession().setAttribute("error", errorMessage);
            response.sendRedirect("/login?error");
        })
                .and()
               	.logout()
                	.logoutUrl("/logout")
                	.logoutSuccessUrl("/")
               	.and()
                .exceptionHandling()
                	.accessDeniedPage("/access-denied") // Chuyển hướng đến trang access-denied khi bị cấm truy cập (403)
                .and()
                .csrf().disable();
    }
    }


