package net.sorted.exchange.web.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("john").password("password").roles("USER");
        auth.inMemoryAuthentication().withUser("jane").password("password").roles("USER");
        auth.inMemoryAuthentication().withUser("doug").password("password").roles("USER");
    }

    @Override
    // Switch off CSRF (Cross Site Request Forgery) - not needed for REST
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable();
//        super.configure(http);

        http.authorizeRequests().anyRequest().fullyAuthenticated().and().
                httpBasic().and().
                csrf().disable();

        super.configure(http);
    }
}