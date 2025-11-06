package gomwifiv1.gomwifiv1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gomwifiv1.gomwifiv1.model.Site;
import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByCodesiteContainingIgnoreCase(String code);
}
