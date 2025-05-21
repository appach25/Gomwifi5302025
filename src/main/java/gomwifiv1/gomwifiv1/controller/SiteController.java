package gomwifiv1.gomwifiv1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gomwifiv1.gomwifiv1.model.Site;
import gomwifiv1.gomwifiv1.repository.SiteRepository;
import gomwifiv1.gomwifiv1.model.Equipment;
import gomwifiv1.gomwifiv1.repository.EquipmentRepository;
import java.util.List;

@Controller
@RequestMapping("/sites")
@PreAuthorize("hasRole('ADMIN')")
public class SiteController {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @GetMapping({"", "/"})
    public String listSites(Model model, @RequestParam(required = false) String searchCode) {
        List<Site> sites;
        if (searchCode != null && !searchCode.isEmpty()) {
            sites = siteRepository.findByCodesiteContainingIgnoreCase(searchCode);
        } else {
            sites = siteRepository.findAll();
        }
        model.addAttribute("sites", sites);
        model.addAttribute("searchCode", searchCode);
        return "sites/list";
    }

    @GetMapping("/nouveau")
    public String afficherFormulaireCreation(Model model) {
        model.addAttribute("site", new Site());
        return "sites/creation";
    }

    @GetMapping("/modifier/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid site Id:" + id));
        model.addAttribute("site", site);
        return "sites/update";
    }

    @PostMapping("/enregistrer")
    public String enregistrerSite(Site site, RedirectAttributes redirectAttributes) {
        try {
            if (site.getId() == null) {
                site.generateCodeSite();
            }
            siteRepository.save(site);
            redirectAttributes.addFlashAttribute("message", "Site " + (site.getId() == null ? "enregistré" : "modifié") + " avec succès!");
            return "redirect:/sites";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de l'opération sur le site.");
            return "redirect:/sites/" + (site.getId() == null ? "nouveau" : "modifier/" + site.getId());
        }
    }

    @GetMapping("/supprimer/{id}")
    public String deleteSite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Site site = siteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid site Id:" + id));
            siteRepository.delete(site);
            redirectAttributes.addFlashAttribute("message", "Site supprimé avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la suppression du site.");
        }
        return "redirect:/sites/";
    }

    @GetMapping("/{siteId}/add-equipment")
    public String showAddEquipmentForm(@PathVariable Long siteId, Model model) {
        Site site = siteRepository.findById(siteId).orElseThrow();
        model.addAttribute("site", site);
        model.addAttribute("availableEquipments", 
            equipmentRepository.findByDisponibilite(Equipment.DisponibiliteStatus.NON_ALLOUE));
        return "sites/add-equipment";
    }

    @PostMapping("/{siteId}/add-equipment")
    public String addEquipmentToSite(@PathVariable Long siteId, @RequestParam Long equipmentId) {
        Site site = siteRepository.findById(siteId).orElseThrow();
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow();
        
        equipment.setSite(site);
        equipment.setDisponibilite(Equipment.DisponibiliteStatus.ALLOUE);
        equipmentRepository.save(equipment);
        
        return "redirect:/sites";
    }
}
