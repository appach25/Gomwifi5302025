package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.model.EtatVoucher;
import gomwifiv1.gomwifiv1.model.Voucher;
import gomwifiv1.gomwifiv1.repository.VoucherRepository;
import java.util.List;
import gomwifiv1.gomwifiv1.service.UDRRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherRepository voucherRepository;
    
    @Autowired
    private UDRRouterService udrRouterService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listVouchers(Model model) {
        model.addAttribute("vouchers", voucherRepository.findAll());
        // UDR Router integration temporarily disabled
        model.addAttribute("routerConnected", false);
        model.addAttribute("routerError", "UDR Router integration is currently disabled");
        return "vouchers/list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "vouchers/create";
    }

    @PostMapping("/delete-used")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUsedVouchers(RedirectAttributes redirectAttributes) {
        try {
            List<Voucher> usedVouchers = voucherRepository.findByDisponibilite(EtatVoucher.ALLOUER);
            voucherRepository.deleteAll(usedVouchers);
            redirectAttributes.addFlashAttribute("success", "Les vouchers utilisés ont été supprimés avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression des vouchers: " + e.getMessage());
        }
        return "redirect:/vouchers/list";
    }

    @PostMapping("/create")
    public String createVoucher(@RequestParam String numero, @RequestParam Integer nombreDeJour,
                              RedirectAttributes redirectAttributes) {
        try {
            // Check if voucher number already exists
            Voucher existingVoucher = voucherRepository.findByNumero(numero);
            if (existingVoucher != null) {
                throw new IllegalArgumentException("Ce numéro de voucher existe déjà");
            }

            Double prix = switch (nombreDeJour) {
                case 1 -> 100.0;
                case 7 -> 250.0;
                case 30 -> 750.0;
                default -> throw new IllegalArgumentException("Nombre de jours invalide");
            };

            Voucher voucher = new Voucher();
            voucher.setNumero(numero);
            voucher.setPrix(prix);
            voucher.setNombreDeJour(nombreDeJour);
            voucherRepository.save(voucher);

            redirectAttributes.addFlashAttribute("success", "Voucher créé avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/vouchers/list";
    }
}
