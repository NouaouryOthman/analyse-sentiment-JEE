package ma.emsi.pfa.web;

import ma.emsi.pfa.dao.RoleRepository;
import ma.emsi.pfa.dao.UserRepository;
import ma.emsi.pfa.entities.Client;
import ma.emsi.pfa.entities.Role;
import ma.emsi.pfa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
@Controller
public class SecurityController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/registration")
    public String formClient(Model model) {
        model.addAttribute("client", new Client());
        return "registration";
    }

    @PostMapping("/saveClient")
    public String saveClient(Model model, @Valid Client client, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "registration";
        PasswordEncoder pe = passwordEncoder();
        client.setPassword(pe.encode(client.getPassword()));
        client.setActive(true);
        Role role = roleRepository.findByName("CLIENT");
        Collection<Role> roles = new ArrayList<>();
        roles.add(role);
        client.setRoles(roles);
        Collection<User> clients = new ArrayList<>();
        clients.add(client);
        role.setUsers(clients);
        userRepository.save(client);
        roleRepository.save(role);
        System.out.println(client.getRoles().toString());
        System.out.println(role.getUsers().toString());
        model.addAttribute("client", client);
        return "redirect:/login";
    }

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/login";
    }

    @GetMapping("/403")
    public String nonAutorise(){
        return "403";
    }
}
