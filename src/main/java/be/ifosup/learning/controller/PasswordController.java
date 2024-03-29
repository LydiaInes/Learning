package be.ifosup.learning.controller;

import be.ifosup.learning.users.entities.User;
import be.ifosup.learning.users.service.UserService;
import be.ifosup.learning.utils.PasswordValidation;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private UserService userservice;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping
    public String resetPasswordPage() {
        return "public/requestpassword.html";
    }

    @PostMapping("/reset")
    public String resetPasswordFormPage(HttpServletRequest request, RedirectAttributes attributes) {
        String email = request.getParameter("email");
        String token = RandomString.make(50);
        String appUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        try {
            userservice.updateResetPassword(token, email);

            String Link = appUrl + "/password/resetpassword?token=" + token;
            sendEmail(email, Link);
            attributes.addFlashAttribute("messagepos", "Le mail pour réinitialiser le mot de passe a bien été envoyé");

        } catch (Exception e) {
            System.out.print(e);
            attributes.addFlashAttribute("messageneg", "Impossible d'envoyer le mail pour réinitialiser le mot de passe");
        }
        return "redirect:/";
    }

    private void sendEmail(String email, String Link) throws MessagingException, UnsupportedEncodingException, MailSendException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("learningifosup@hotmail.com", "Lia support");
        helper.setTo(email);
        String subject = "Réinitialisation de mot de passe";
        String content = "Bonjour,</p>"
                + "<p>Oublié votre mot de passe?</p>"
                + "<p>Nous avons reçu une demande pour réinialiser le mot de passe de votre compte</p>"
                + "<p>Pour réinialiser votre mot de passe, veuillez cliquer sur le lien ci-dessous</p>"
                + "<p><b><a href=\"" + Link + "\" > Réinialisé le mot de passe</a></b></p>";
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/resetpassword")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model, RedirectAttributes attributes) {
        User user = userservice.findByToken(token);
        if (user == null) {
            attributes.addFlashAttribute("messageneg", "Il n'y a pas de changement de mot de passe associé avec ce token");
            return "redirect:/";
        }
        model.addAttribute("token", token);
        return "/public/resetpassword.html";
    }

    @PostMapping("/resetpassword")
    public String updatePass(@RequestParam("password") String password, @RequestParam("matchingpassword") String matchingpassword,@RequestParam("token") String token, RedirectAttributes attributes) {
        User user = userservice.findByToken(token);
        if(user == null){
            attributes.addFlashAttribute("messageneg", "Demande de réinitialisation de mot de passe non valide");
            return "redirect:/";
        } else if(password.isEmpty() || matchingpassword.isEmpty()) {
            attributes.addFlashAttribute("messageneg", "Tous les champs doivent être remplis");
            return "redirect:/password/resetpassword?token="+token;
        } else if (!PasswordValidation.main(password)) {
            attributes.addFlashAttribute("messageneg", "Le mot de passe doit contenir au moins 1 majuscule, 1 miniscule, 1 chiffre et 1 caractère spécial");
            return "redirect:/password/resetpassword?token="+token;
        }else if (!password.equals(matchingpassword)) {
            attributes.addFlashAttribute("messageneg", "Les deux mots de passe ne correspondent pas");
            return "redirect:/password/resetpassword?token="+token;
        }
        try {
            userservice.updatePassword(user.getId(), password);
            attributes.addFlashAttribute("messagepos", "Succès pour le mot de passe");
        }
        catch(Exception e){
            System.out.print(e);
            attributes.addFlashAttribute("messageneg", "Erreur pour le mot de passe");
        }
        return "/login.html";
    }
}
