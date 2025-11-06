package gomwifiv1.gomwifiv1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "Une erreur s'est produite";
        String errorTitle = "Erreur";
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorTitle = "Accès Refusé";
                errorMessage = "Vous n'avez pas la permission d'accéder à cette page. Veuillez contacter votre administrateur si vous pensez que c'est une erreur.";
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorTitle = "Page Non Trouvée";
                errorMessage = "La page demandée n'existe pas.";
            }
        }
        
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }
}
