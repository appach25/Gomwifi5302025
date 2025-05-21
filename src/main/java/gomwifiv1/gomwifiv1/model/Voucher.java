package gomwifiv1.gomwifiv1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Voucher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @javax.validation.constraints.NotBlank(message = "Le numéro de voucher est obligatoire")
    private String numero;
    
    private Double prix;
    
    private Integer nombreDeJour;

    @Enumerated(EnumType.STRING)
    private EtatVoucher disponibilite = EtatVoucher.NON_ALLOUER;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Integer getNombreDeJour() {
        return nombreDeJour;
    }

    public void setNombreDeJour(Integer nombreDeJour) {
        this.nombreDeJour = nombreDeJour;
    }

    public EtatVoucher getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(EtatVoucher disponibilite) {
        this.disponibilite = disponibilite;
    }
}
