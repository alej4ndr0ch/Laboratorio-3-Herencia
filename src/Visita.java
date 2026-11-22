

import java.math.BigDecimal;

public class Visita {

    private final OrdenServicio orden;

    private boolean iniciada = false;
    private int numeroVisita = 0;
    private ResultadoVisita resultado;

    private BigDecimal cargoAdicional = new BigDecimal("0.00");

    // La crea Jornada al asignar una orden.
    Visita(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException(
                    "Debe indicar una orden.");
        }

        this.orden = orden;
    }

    void validarInicio() {
        if (iniciada) {
            throw new IllegalStateException(
                    "La visita ya inicio.");
        }

        orden.validarInicioVisita();
    }

    void iniciar() {
        validarInicio();

        boolean adicional = orden.getVisitasRealizadas() > 0;

        orden.iniciarVisita();

        numeroVisita = orden.getVisitasRealizadas();

        cargoAdicional = new BigDecimal(
                adicional ? "40.00" : "0.00");

        iniciada = true;
    }

    void registrarResultado(ResultadoVisita resultado) {
        if (!iniciada || tieneResultado()) {
            throw new IllegalStateException(
                    "La visita no admite un resultado.");
        }

        if (resultado == null) {
            throw new IllegalArgumentException(
                    "Debe indicar un resultado.");
        }

        orden.resolverVisita(resultado);
        this.resultado = resultado;
    }

    public boolean tieneResultado() {
        return resultado != null;
    }

    public boolean estaIniciada() {
        return iniciada;
    }

    public OrdenServicio getOrden() {
        return orden;
    }

    public int getNumeroVisita() {
        return numeroVisita;
    }

    public ResultadoVisita getResultado() {
        return resultado;
    }

    public BigDecimal getCargoAdicional() {
        return cargoAdicional;
    }
}
