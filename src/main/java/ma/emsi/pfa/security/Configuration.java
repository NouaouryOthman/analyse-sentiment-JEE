package ma.emsi.pfa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;


@org.springframework.context.annotation.Configuration
@Service
public class Configuration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = pe();
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select email as principal,password as credentials, active from user where email=?")
                .authoritiesByUsernameQuery("select u.email, r.name from role_users ru " +
                        "inner join user u on ru.users_id = u.id " +
                        "inner join role r on ru.roles_id = r.id " +
                        "where u.email = ?")
                .rolePrefix("ROLE_")
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login");
        http.authorizeRequests().antMatchers("/login", "/registration", "/saveClient", "/webjars/**").permitAll();
        http.authorizeRequests().antMatchers("/users**", "/supprimerUser**", "/activerUser**").hasRole("ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        http.exceptionHandling().accessDeniedPage("/403");
    }

    @Bean
    PasswordEncoder pe() {
        return new BCryptPasswordEncoder();
    }
}
