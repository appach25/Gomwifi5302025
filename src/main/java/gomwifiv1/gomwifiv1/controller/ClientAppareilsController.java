package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.model.Client;
import gomwifiv1.gomwifiv1.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientAppareilsController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/{id}/appareils")
    public String viewClientAppareils(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Client client = clientRepository.findById(id).orElse(null);
        
        if (client == null) {
            redirectAttributes.addFlashAttribute("error", "Client non trouvé");
            return "redirect:/clients";
        }
        
        model.addAttribute("client", client);
        return "client-appareils";
    }
}
