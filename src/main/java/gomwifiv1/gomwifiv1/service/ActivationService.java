package gomwifiv1.gomwifiv1.service;

import gomwifiv1.gomwifiv1.model.Activation;
import gomwifiv1.gomwifiv1.model.Appareil;
import gomwifiv1.gomwifiv1.model.EtatAppareil;
import gomwifiv1.gomwifiv1.repository.ActivationRepository;
import gomwifiv1.gomwifiv1.repository.AppareilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ActivationService {

    @Autowired
    private ActivationRepository activationRepository;

    @Autowired
    private AppareilRepository appareilRepository;

    @Transactional
    public Activation createActivation(Appareil appareil, Integer nombreDeJour, Double prix) {
        // Check if appareil already has an active activation
        activationRepository.findByAppareil(appareil).ifPresent(activationRepository::delete);

        // Calculate dates
        Date dateDebut = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateDebut);
        calendar.add(Calendar.DAY_OF_MONTH, nombreDeJour);
        Date dateFin = calendar.getTime();

        // Create new activation
        Activation activation = new Activation();
        activation.setAppareil(appareil);
        activation.setNombreDeJour(nombreDeJour);
        activation.setDateDebut(dateDebut);
        activation.setDateFin(dateFin);
        activation.setPrix(prix);

        // Update appareil state
        appareil.setEtat(EtatAppareil.ACTIVER);
        appareilRepository.save(appareil);

        return activationRepository.save(activation);
    }

    public List<Activation> getAllActivations() {
        return activationRepository.findAll();
    }

    public List<Activation> findActivationsByDateRange(Date startDate, Date endDate) {
        // Set the time of startDate to beginning of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startDate = cal.getTime();

        // Set the time of endDate to end of day
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        endDate = cal.getTime();

        System.out.println("Searching with adjusted startDate: " + startDate);
        System.out.println("Searching with adjusted endDate: " + endDate);

        return activationRepository.findByDateDebutBetween(startDate, endDate);
    }

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void checkExpiredActivations() {
        Date now = new Date();
        List<Activation> activations = activationRepository.findAll();
        
        for (Activation activation : activations) {
            if (activation.getDateFin().before(now)) {
                // Deactivate appareil
                Appareil appareil = activation.getAppareil();
                appareil.setEtat(EtatAppareil.NON_ACTIVER);
                appareilRepository.save(appareil);
                
                // Remove expired activation
                activationRepository.delete(activation);
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean isAppareilActive(Appareil appareil) {
        return activationRepository.findByAppareil(appareil)
                .map(activation -> activation.getDateFin().after(new Date()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Integer getRemainingDays(Appareil appareil) {
        return activationRepository.findByAppareil(appareil)
                .map(activation -> {
                    Date now = new Date();
                    Date fin = activation.getDateFin();
                    long diff = fin.getTime() - now.getTime();
                    return (int) (diff / (1000 * 60 * 60 * 24));
                })
                .orElse(0);
    }
}
