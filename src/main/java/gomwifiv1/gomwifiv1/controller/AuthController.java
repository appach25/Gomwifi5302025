package gomwifiv1.gomwifiv1.controller;

import gomwifiv1.gomwifiv1.model.User;
import gomwifiv1.gomwifiv1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRegistrationForm(@ModelAttribute("user") User user) {
        return "register";
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                user.getRole() == null) {
                model.addAttribute("error", "Tous les champs sont obligatoires");
                return "register";
            }
            User savedUser = userService.createUser(user);
            if (savedUser != null && savedUser.getId() != null) {
                return "redirect:/users?created=true";
            } else {
                model.addAttribute("error", "Une erreur s'est produite lors de la création de l'utilisateur");
                return "register";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur s'est produite lors de la création de l'utilisateur");
            return "register";
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
}
