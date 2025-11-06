package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.model.Client;
import gomwifiv1.gomwifiv1.model.Appareil;
import gomwifiv1.gomwifiv1.repository.ClientRepository;
import gomwifiv1.gomwifiv1.repository.AppareilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ClientAppareilController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppareilRepository appareilRepository;

    @GetMapping("/client-appareil/new-client")
    public String showNewClientForm(Model model) {
        if (!model.containsAttribute("client")) {
            model.addAttribute("client", new Client());
        }
        return "create-client";
    }

    @PostMapping("/client-appareil/save-client")
    public String saveClient(@ModelAttribute Client client, Model model) {
        try {
            Client existingClient = clientRepository.findByTelephone(client.getTelephone());
            if (existingClient != null) {
                model.addAttribute("telephoneError", "Ce numéro de téléphone est déjà utilisé");
                model.addAttribute("client", client);
                return "create-client";
            }
            clientRepository.save(client);
            return "redirect:/client-appareil/new-appareil?clientId=" + client.getId();
        } catch (Exception e) {
            model.addAttribute("telephoneError", "Une erreur s'est produite lors de la sauvegarde");
            model.addAttribute("client", client);
            return "create-client";
        }
    }

    @GetMapping("/client-appareil/new-appareil")
    public String showNewAppareilForm(@RequestParam Long clientId, Model model) {
        Appareil appareil = new Appareil();
        model.addAttribute("appareil", appareil);
        model.addAttribute("clientId", clientId);
        return "create-appareil";
    }

    @PostMapping("/client-appareil/save-appareil")
    public String saveAppareil(@ModelAttribute Appareil appareil, @RequestParam Long clientId, Model model) {
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid client Id:" + clientId));
            appareil.setClient(client);
            appareilRepository.save(appareil);
            return "redirect:/";
        } catch (Exception e) {
            if (e.getMessage().contains("macadresse")) {
                model.addAttribute("macError", "Cette adresse MAC est déjà utilisée");
            }
            model.addAttribute("clientId", clientId);
            return "create-appareil";
        }
    }


}
