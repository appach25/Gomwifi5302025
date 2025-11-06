package gomwifiv1.gomwifiv1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gomwifiv1.gomwifiv1.model.Equipment;
import gomwifiv1.gomwifiv1.repository.EquipmentRepository;
import java.util.List;

@Controller
@RequestMapping("/equipment")
public class EquipmentController {
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @GetMapping({"", "/"})
    public String listEquipment(Model model, @RequestParam(required = false) String searchType) {
        List<Equipment> equipments;
        if (searchType != null && !searchType.isEmpty()) {
            equipments = equipmentRepository.findByTypeequipmentContainingIgnoreCase(searchType);
        } else {
            equipments = equipmentRepository.findAll();
        }
        model.addAttribute("equipments", equipments);
        model.addAttribute("searchType", searchType);
        return "equipment/list";
    }
    
    @GetMapping("/nouveau")
    public String showCreationForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        return "equipment/creation";
    }
    
    @GetMapping("/modifier/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipment Id:" + id));
        model.addAttribute("equipment", equipment);
        return "equipment/update";
    }
    
    @PostMapping("/enregistrer")
    public String saveEquipment(Equipment equipment, RedirectAttributes redirectAttributes) {
        System.out.println("Attempting to save equipment: " + equipment.getTypeequipment());
        try {
            equipmentRepository.save(equipment);
            redirectAttributes.addFlashAttribute("message", "Equipment " + 
                (equipment.getId() == null ? "enregistré" : "modifié") + " avec succès!");
            return "redirect:/equipment/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de l'opération sur l'équipement.");
            return "redirect:/equipment/" + (equipment.getId() == null ? "nouveau" : "modifier/" + equipment.getId());
        }
    }
    
    @GetMapping("/supprimer/{id}")
    public String deleteEquipment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Equipment equipment = equipmentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid equipment Id:" + id));
            equipmentRepository.delete(equipment);
            redirectAttributes.addFlashAttribute("message", "Equipment supprimé avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la suppression de l'équipement.");
        }
        return "redirect:/equipment/";
    }
}
