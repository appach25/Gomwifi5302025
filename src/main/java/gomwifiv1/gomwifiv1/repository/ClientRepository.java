package gomwifiv1.gomwifiv1.repository;

import gomwifiv1.gomwifiv1.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByTelephone(String telephone);
}
