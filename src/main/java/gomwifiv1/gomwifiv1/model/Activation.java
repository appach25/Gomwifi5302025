package gomwifiv1.gomwifiv1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class Activation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer nombreDeJour;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateDebut;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateFin;
    
    @OneToOne
    @JoinColumn(name = "appareil_id", nullable = false)
    private Appareil appareil;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double prix;
    
    // Constructors
    public Activation() {
    }
    
    public Activation(Integer nombreDeJour, Date dateDebut, Date dateFin, Appareil appareil, User user, Double prix) {
        this.nombreDeJour = nombreDeJour;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.appareil = appareil;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getNombreDeJour() {
        return nombreDeJour;
    }
    
    public void setNombreDeJour(Integer nombreDeJour) {
        this.nombreDeJour = nombreDeJour;
    }
    
    public Date getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public Date getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }
    
    public Appareil getAppareil() {
        return appareil;
    }
    
    public void setAppareil(Appareil appareil) {
        this.appareil = appareil;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
