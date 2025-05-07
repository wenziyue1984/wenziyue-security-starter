package com.wenziyue.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;


/**
 * @author wenziyue
 */
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    private final MyLoginFailureHandler myLoginFailureHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;


    /**
     * 表单登录基本用法
     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable();
//        http.authorizeHttpRequests(auth -> auth
//                .antMatchers("/public", "/hello").permitAll() // 放行接口
//                .anyRequest().authenticated() // 其他接口都需要登录
//        );
//        http.formLogin(login -> login
//                .successHandler(customAuthenticationSuccessHandler) //登录成功处理器，设置之后就不能使用默认登录页，必须自己添加登录页
//                .failureHandler(myLoginFailureHandler) // 登录失败处理器
//        );// 使用默认登录页
//
//        http.logout(logout -> logout
//                .logoutUrl("/logout")  // 默认就是 /logout
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    response.setContentType("application/json;charset=UTF-8");
//                    response.getWriter().write("{\"message\":\"退出成功，bye\"}");
//                })
//        );
//
//        http.exceptionHandling()
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/myLogin")) // 未登录处理
//                .accessDeniedHandler(new MyAccessDeniedHandler());           // 无权限处理
//
//        return http.build();
//    }

    /**
     * 前后端分离，接口登录
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests(auth -> auth
                .antMatchers("/public", "/hello", "/login").permitAll() // 放行接口
                .anyRequest().authenticated() // 其他接口都需要登录
        );

        http.formLogin().disable(); // 禁用表单登录

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling()
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")) // 未登录处理
                .accessDeniedHandler(new MyAccessDeniedHandler());

        return http.build();
    }

    // 密码加密器（必须配置，否则会报错）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }


    public static void main(String[] args) {
        String rawPassword = "123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(rawPassword));
    }

}
