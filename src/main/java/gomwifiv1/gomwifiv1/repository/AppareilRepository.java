package gomwifiv1.gomwifiv1.repository;

import gomwifiv1.gomwifiv1.model.Appareil;
import gomwifiv1.gomwifiv1.model.EtatAppareil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppareilRepository extends JpaRepository<Appareil, Long> {
    boolean existsByMacadresse(String macadresse);

    @Modifying
    @Query("UPDATE Appareil a SET a.etat = :etat WHERE a.id = :id")
    void updateEtat(Long id, EtatAppareil etat);
}
