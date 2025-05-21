package gomwifiv1.gomwifiv1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gomwifiv1.gomwifiv1.model.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Voucher findByNumero(String numero);
    boolean existsByNumero(String numero);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(v) > 0 FROM Voucher v WHERE v.disponibilite = gomwifiv1.gomwifiv1.model.EtatVoucher.NON_ALLOUER")
    boolean hasAvailableVouchers();

    @org.springframework.data.jpa.repository.Query("SELECT v FROM Voucher v WHERE v.disponibilite = gomwifiv1.gomwifiv1.model.EtatVoucher.NON_ALLOUER AND v.nombreDeJour = :nombreDeJour ORDER BY v.id ASC")
    java.util.List<Voucher> findAvailableVouchersByNombreDeJour(Integer nombreDeJour);
}
