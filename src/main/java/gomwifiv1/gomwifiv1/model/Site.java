package gomwifiv1.gomwifiv1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sites")
public class Site {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String codesite;
    
    private String adresse;
    
    private String typedeconnexion;
    
    private double prixabonnement;

    @OneToMany(mappedBy = "site")
    private List<Equipment> equipments = new ArrayList<>();
    
    public void generateCodeSite() {
        if (typedeconnexion != null && adresse != null) {
            String typePart = typedeconnexion.length() >= 3 ? typedeconnexion.substring(0, 3).toUpperCase() : typedeconnexion.toUpperCase();
            String adressePart = adresse.length() >= 3 ? adresse.substring(0, 3).toUpperCase() : adresse.toUpperCase();
            int randomNum = (int) (Math.random() * 900) + 100; // generates a number between 100 and 999
            this.codesite = typePart + adressePart + randomNum;
        }
    }

    // Constructeurs
    public Site() {
    }

    public Site(String codesite, String adresse, String typedeconnexion, double prixabonnement) {
        this.codesite = codesite;
        this.adresse = adresse;
        this.typedeconnexion = typedeconnexion;
        this.prixabonnement = prixabonnement;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodesite() {
        return codesite;
    }

    public void setCodesite(String codesite) {
        this.codesite = codesite;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTypedeconnexion() {
        return typedeconnexion;
    }

    public void setTypedeconnexion(String typedeconnexion) {
        this.typedeconnexion = typedeconnexion;
    }

    public double getPrixabonnement() {
        return prixabonnement;
    }

    public void setPrixabonnement(double prixabonnement) {
        this.prixabonnement = prixabonnement;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Equipment> equipments) {
        this.equipments = equipments;
    }
}
