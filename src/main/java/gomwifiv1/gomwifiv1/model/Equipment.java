package gomwifiv1.gomwifiv1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "equipments")
public class Equipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String typeequipment;
    
    @Column(nullable = false)
    private String marque;
    
    @Column(nullable = false)
    private String model;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisponibiliteStatus disponibilite = DisponibiliteStatus.NON_ALLOUE;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
    
    // Enum for disponibilite status
    public enum DisponibiliteStatus {
        NON_ALLOUE,
        ALLOUE
    }
    
    // Constructors
    public Equipment() {
    }
    
    public Equipment(String typeequipment, String marque, String model) {
        this.typeequipment = typeequipment;
        this.marque = marque;
        this.model = model;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTypeequipment() {
        return typeequipment;
    }
    
    public void setTypeequipment(String typeequipment) {
        this.typeequipment = typeequipment;
    }
    
    public String getMarque() {
        return marque;
    }
    
    public void setMarque(String marque) {
        this.marque = marque;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public DisponibiliteStatus getDisponibilite() {
        return disponibilite;
    }
    
    public void setDisponibilite(DisponibiliteStatus disponibilite) {
        this.disponibilite = disponibilite;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
