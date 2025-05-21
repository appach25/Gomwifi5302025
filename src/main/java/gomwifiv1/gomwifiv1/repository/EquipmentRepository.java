package gomwifiv1.gomwifiv1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomwifiv1.gomwifiv1.model.Equipment;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByDisponibilite(Equipment.DisponibiliteStatus disponibilite);
    List<Equipment> findByTypeequipmentContainingIgnoreCase(String typeequipment);
}
