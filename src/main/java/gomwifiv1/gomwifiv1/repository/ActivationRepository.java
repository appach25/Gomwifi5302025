package gomwifiv1.gomwifiv1.repository;

import gomwifiv1.gomwifiv1.model.Activation;
import gomwifiv1.gomwifiv1.model.Appareil;
import gomwifiv1.gomwifiv1.model.Client;
import gomwifiv1.gomwifiv1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivationRepository extends JpaRepository<Activation, Long> {
    Optional<Activation> findByAppareil(Appareil appareil);
    Optional<Activation> findTopByAppareilOrderByDateFinDesc(Appareil appareil);
    List<Activation> findByDateDebutBetween(Date startDate, Date endDate);
    List<Activation> findByAppareil_Client_IdAndDateDebutBetween(Long clientId, Date startDate, Date endDate);
    List<Activation> findByAppareil_Client_Id(Long clientId);
    List<Activation> findByUser(User user);
    List<Activation> findByUserAndDateDebutBetween(User user, Date startDate, Date endDate);
}
