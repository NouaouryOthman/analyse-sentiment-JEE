package ma.emsi.pfa.web;

import ma.emsi.pfa.dao.CompteRepository;
import ma.emsi.pfa.dao.UserRepository;
import ma.emsi.pfa.entities.Client;
import ma.emsi.pfa.entities.Compte;
import ma.emsi.pfa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class ClientController {
    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/comptes")
    public String listePages(Model model,
                             @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size) {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) auth).getUsername();
        User user = userRepository.findByEmail(email);
        Page<Compte> pageComptes = compteRepository.listeComptes(user.getId(), PageRequest.of(page, size));
        model.addAttribute("comptes", pageComptes.getContent());
        model.addAttribute("pages", new int[pageComptes.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        return "comptes";
    }

    @GetMapping("/ajouterCompte")
    public String ajouterCompte(Model model) {
        model.addAttribute("compte", new Compte());
        model.addAttribute("mode", "new");
        return "formCompte";
    }

    @PostMapping("/saveCompte")
    public String savePatient(Model model, @Valid Compte compte, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formCompte";
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) auth).getUsername();
        Client client = (Client) userRepository.findByEmail(email);
        compte.setClient(client);
        compteRepository.save(compte);
        model.addAttribute("compte", compte);
        return "afficherCompte";
    }

    @GetMapping("/detailsCompte")
    public String detailsCompte(Model model, Long id) {
        Compte compte = compteRepository.findById(id).get();
        model.addAttribute("mode", "affichage");
        model.addAttribute("compte", compte);
        return "afficherCompte";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/comptes";
    }

    @GetMapping(path = "/supprimerCompte")
    public String supprimerCompte(Long id, int page, int size) {
        compteRepository.deleteById(id);
        return "redirect:/comptes?page=" + page + "&size=" + size;
    }

}
