package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.model.Activation;
import gomwifiv1.gomwifiv1.model.Appareil;
import gomwifiv1.gomwifiv1.model.Client;
import gomwifiv1.gomwifiv1.model.EtatAppareil;
import gomwifiv1.gomwifiv1.repository.ActivationRepository;
import gomwifiv1.gomwifiv1.repository.AppareilRepository;
import gomwifiv1.gomwifiv1.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/clients")
public class ClientListController {

    private static final Logger logger = LoggerFactory.getLogger(ClientListController.class);

    private final ClientRepository clientRepository;
    private final ActivationRepository activationRepository;
    private final AppareilRepository appareilRepository;

    @Autowired
    public ClientListController(ClientRepository clientRepository, 
                              ActivationRepository activationRepository,
                              AppareilRepository appareilRepository) {
        this.clientRepository = clientRepository;
        this.activationRepository = activationRepository;
        this.appareilRepository = appareilRepository;
    }

    @GetMapping("/list")
    public String listClients(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "client-list";
    }

    @GetMapping("/search")
    public String showSearchForm() {
        return "client-search";
    }

    @PostMapping("/search")
    public String searchClient(@RequestParam String telephone, Model model) {
        logger.info("Searching for client with telephone: {}", telephone);
        Client client = clientRepository.findByTelephone(telephone);
        
        if (client != null && client.getAppareils() != null) {
            logger.info("Found client: {} with id: {}", client.getNom(), client.getId());
            Date now = new Date();
            
            for (Appareil appareil : client.getAppareils()) {
                Optional<Activation> activation = activationRepository.findByAppareil(appareil);
                if (activation.isPresent()) {
                    Activation act = activation.get();
                    appareil.setActivation(act);
                    
                    // Update appareil status based on activation expiration
                    if (act.getDateFin().before(now)) {
                        appareil.setEtat(EtatAppareil.NON_ACTIVER);
                        appareilRepository.save(appareil);
                    }
                }
            }
        }
        
        model.addAttribute("client", client);
        return "client-search";
    }
}
