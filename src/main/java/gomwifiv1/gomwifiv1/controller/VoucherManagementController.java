package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.model.Voucher;
import gomwifiv1.gomwifiv1.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/gestion-vouchers")
public class VoucherManagementController {

    @Autowired
    private VoucherRepository voucherRepository;

    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "vouchers/create-voucher";
    }

    @GetMapping("/check-numero/{numero}")
    @ResponseBody
    public ResponseEntity<Boolean> checkNumeroExists(@PathVariable String numero) {
        boolean exists = voucherRepository.existsByNumero(numero);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/create")
    public String createVoucher(@ModelAttribute @javax.validation.Valid Voucher voucher, BindingResult result, Model model) {
        if (voucher.getNumero() == null || voucher.getNumero().trim().isEmpty()) {
            result.rejectValue("numero", "error.voucher", "Le numéro de voucher est obligatoire");
            return "vouchers/create-voucher";
        }
        
        if (voucherRepository.existsByNumero(voucher.getNumero())) {
            result.rejectValue("numero", "error.voucher", "Ce numéro de voucher existe déjà");
            return "vouchers/create-voucher";
        }
        
        try {
            voucherRepository.save(voucher);
            model.addAttribute("message", "Voucher créé avec succès!");
            return "redirect:/gestion-vouchers";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du voucher: " + e.getMessage());
            return "vouchers/create-voucher";
        }
    }

    @GetMapping("")
    public String listVouchers(Model model) {
        model.addAttribute("vouchers", voucherRepository.findAll());
        return "vouchers/list-vouchers";
    }
}
