package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.model.Activation;
import gomwifiv1.gomwifiv1.service.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ActivationService activationService;

    @GetMapping("/")
    public String home(Model model) {
        // Get today's activations
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endDate = cal.getTime();

        List<Activation> todayActivations = activationService.findActivationsByDateRange(startDate, endDate);
        
        // Calculate total revenue for today
        double todayRevenue = todayActivations.stream()
                .mapToDouble(Activation::getPrix)
                .sum();

        // Get all active activations
        List<Activation> allActivations = activationService.getAllActivations();
        long activeCount = allActivations.stream()
                .filter(a -> a.getDateFin().after(new Date()))
                .count();

        model.addAttribute("todayActivations", todayActivations);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("activeSubscriptions", activeCount);
        
        return "home";
    }
}
