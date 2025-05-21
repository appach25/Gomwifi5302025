package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.dto.ActivationRequest;
import gomwifiv1.gomwifiv1.model.Activation;
import gomwifiv1.gomwifiv1.model.Appareil;
import gomwifiv1.gomwifiv1.model.Voucher;
import gomwifiv1.gomwifiv1.model.EtatVoucher;
import gomwifiv1.gomwifiv1.repository.AppareilRepository;
import gomwifiv1.gomwifiv1.repository.VoucherRepository;
import gomwifiv1.gomwifiv1.service.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/activation")
public class ActivationController {

    @Autowired
    private ActivationService activationService;

    @Autowired
    private AppareilRepository appareilRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @GetMapping("/")
    public String listActivations(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Model model) {
        System.out.println("Received startDate: " + startDate);
        System.out.println("Received endDate: " + endDate);
        List<Activation> activations;
        if (startDate != null && endDate != null) {
            activations = activationService.findActivationsByDateRange(startDate, endDate);
        } else {
            activations = activationService.getAllActivations();
        }
        double totalAmount = activations.stream()
            .mapToDouble(Activation::getPrix)
            .sum();

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activations", activations);
        model.addAttribute("totalAmount", totalAmount);
        return "activations/list";
    }

    @GetMapping("/check-vouchers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkVouchersAvailable() {
        Map<String, Object> response = new HashMap<>();
        response.put("available", voucherRepository.hasAvailableVouchers());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createActivation(ActivationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find appareil
            Appareil appareil = appareilRepository.findById(request.getAppareilId())
                .orElseThrow(() -> new RuntimeException("Appareil non trouvé"));

            // Find available voucher with matching nombre de jour
            List<Voucher> availableVouchers = voucherRepository.findAvailableVouchersByNombreDeJour(request.getNombreDeJour());
            if (availableVouchers.isEmpty()) {
                throw new RuntimeException("Aucun voucher disponible pour " + request.getNombreDeJour() + " jours");
            }
            
            // Use the first available voucher
            Voucher voucher = availableVouchers.get(0);
            voucher.setDisponibilite(EtatVoucher.ALLOUER);
            voucherRepository.save(voucher);

            // Create activation
            Activation activation = activationService.createActivation(appareil, request.getNombreDeJour(), request.getPrix());

            response.put("success", true);
            response.put("voucherNumber", voucher.getNumero());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String generateVoucherNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
