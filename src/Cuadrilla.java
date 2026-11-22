

public class Cuadrilla {

    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;

    private Jornada jornadaActiva;

    public Cuadrilla(
            String codigo,
            double capacidadHoras,
            boolean certificadaRefrigeracion) {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El codigo no puede estar vacio.");
        }

        if (!Double.isFinite(capacidadHoras)
                || capacidadHoras <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser positiva y finita.");
        }

        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    void reservar(Jornada jornada) {
        if (jornada == null || !estaDisponible()) {
            throw new IllegalStateException(
                    "La cuadrilla no puede reservarse.");
        }

        jornadaActiva = jornada;
    }

    void liberar(Jornada jornada) {
        if (jornadaActiva != jornada) {
            throw new IllegalStateException(
                    "La jornada no pertenece a esta cuadrilla.");
        }

        jornadaActiva = null;
    }

    public boolean estaDisponible() {
        return jornadaActiva == null;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean estaCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    public Jornada getJornadaActiva() {
        return jornadaActiva;
    }
}