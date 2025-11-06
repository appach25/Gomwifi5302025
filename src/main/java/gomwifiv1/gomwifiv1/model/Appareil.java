package gomwifiv1.gomwifiv1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Appareil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String type;
    private String marque;
    
    @Column(unique = true)
    private String macadresse;
    
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    private EtatAppareil etat = EtatAppareil.NON_ACTIVER;

    @OneToOne(mappedBy = "appareil")
    private Activation activation;

    // Constructors
    public Appareil() {
    }

    public Appareil(String type, String marque, String macadresse, String commentaire) {
        this.type = type;
        this.marque = marque;
        this.macadresse = macadresse;
        this.commentaire = commentaire;
        this.etat = EtatAppareil.NON_ACTIVER;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getMacadresse() {
        return macadresse;
    }

    public void setMacadresse(String macadresse) {
        this.macadresse = macadresse;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public EtatAppareil getEtat() {
        return etat;
    }

    public void setEtat(EtatAppareil etat) {
        this.etat = etat;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }
}
