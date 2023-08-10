package lmu.gruppe3.wgmanager.common.configurations

import lmu.gruppe3.wgmanager.common.jwt.JwtTokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class SecurityConfiguration(private val jwtTokenFilter: JwtTokenFilter) {

    @Bean
    @Throws(java.lang.Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {

        http
            .csrf().disable().cors()
            .and()
            .authenticationProvider(CustomAuthProvider())
            .authorizeRequests()
            // Following request must be permitAll
            .antMatchers("/actuator/**").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/auth/**").permitAll()
            .antMatchers("/notification/**").permitAll()
            // Feature Requests --> must be authenticated
            .antMatchers("/wg/**").authenticated()
            .antMatchers("/list/**").authenticated()
            .antMatchers("/item/**").authenticated()
            .antMatchers("/features/**").authenticated()
            .antMatchers("/feature/**").authenticated()
            .antMatchers("/finance/**").authenticated()
            // Following request must be permitAll
            .antMatchers("/swagger-ui/**").permitAll()
            .antMatchers("/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(this.jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}