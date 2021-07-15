package ma.emsi.pfa.web;

import ma.emsi.pfa.dao.UserRepository;
import ma.emsi.pfa.entities.Client;
import ma.emsi.pfa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    UserRepository userRepository;


    @GetMapping("/users")
    public String patients(Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "5") int size,
                           @RequestParam(name = "motCle", defaultValue = "") String motCle) {
        Page<User> pageUsers = userRepository.findByEmailContains(motCle, PageRequest.of(page, size));
        model.addAttribute("users", pageUsers.getContent());
        model.addAttribute("pages", new int[pageUsers.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("motCle", motCle);
        model.addAttribute("size", size);
        return "users";
    }

    @GetMapping(path = "/supprimerUser")
    public String supprimerUser(Long id, int page, int size, String motCle) {
        userRepository.deleteById(id);
        return "redirect:/users?page=" + page + "&size=" + size + "&motCle" + motCle;
    }

    @GetMapping(path = "/activerUser")
    public String activerUser(Long id, int page, int size, String motCle) {
        User user = userRepository.findById(id).get();
        if(user.isActive())
            user.setActive(false);
        else
            user.setActive(true);
        userRepository.save(user);
        return "redirect:/users?page=" + page + "&size=" + size + "&motCle" + motCle;
    }
}
