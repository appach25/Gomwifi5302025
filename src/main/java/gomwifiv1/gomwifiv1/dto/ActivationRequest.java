package gomwifiv1.gomwifiv1.dto;

public class ActivationRequest {
    private Long appareilId;
    private Integer nombreDeJour;

    public Long getAppareilId() {
        return appareilId;
    }

    public void setAppareilId(Long appareilId) {
        this.appareilId = appareilId;
    }

    public Integer getNombreDeJour() {
        return nombreDeJour;
    }

    public void setNombreDeJour(Integer nombreDeJour) {
        this.nombreDeJour = nombreDeJour;
    }

    public Double getPrix() {
        if (nombreDeJour == null) return 0.0;
        return switch (nombreDeJour) {
            case 1 -> 50.0;
            case 7 -> 150.0;
            case 30 -> 500.0;
            default -> 0.0;
        };
    }
}
