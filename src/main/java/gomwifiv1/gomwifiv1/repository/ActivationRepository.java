package gomwifiv1.gomwifiv1.repository;

import gomwifiv1.gomwifiv1.model.Activation;
import gomwifiv1.gomwifiv1.model.Appareil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivationRepository extends JpaRepository<Activation, Long> {
    Optional<Activation> findByAppareil(Appareil appareil);
    List<Activation> findByDateDebutBetween(Date startDate, Date endDate);
}
