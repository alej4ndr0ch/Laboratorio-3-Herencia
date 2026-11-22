

import java.math.BigDecimal;

public abstract class OrdenServicio {
    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    private int visitasRealizadas = 0;

    protected OrdenServicio(String codigo, String cliente, String direccion,
            double horasEstimadas, Zona zona) {
        this.codigo = validarTexto(codigo, "Codigo");
        this.cliente = validarTexto(cliente, "Cliente");
        this.direccion = validarTexto(direccion, "Direccion");
        if (!Double.isFinite(horasEstimadas) || horasEstimadas <= 0) {
            throw new IllegalArgumentException("Las horas deben ser positivas y finitas.");
        }
        if (zona == null) {
            throw new IllegalArgumentException("Debe indicar una zona.");
        }
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
    }

    public abstract BigDecimal calcularCosto();

    public abstract int estimarDias();

    public abstract String obtenerCategoria();

    public int maximoVisitas() {
        return 3;
    }

    public boolean esCompatible(Cuadrilla cuadrilla) {
        return cuadrilla != null;
    }

    final void marcarAsignada() {
        exigirEstado(EstadoOrden.PENDIENTE);
        validarVisitasDisponibles();
        estado = EstadoOrden.ASIGNADA;
    }

    final void retirarAsignacion() {
        exigirEstado(EstadoOrden.ASIGNADA);
        estado = EstadoOrden.PENDIENTE;
    }

    final void validarInicioVisita() {
        exigirEstado(EstadoOrden.ASIGNADA);
        validarVisitasDisponibles();
    }

    final void iniciarVisita() {
        validarInicioVisita();
        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
    }

    final void resolverVisita(ResultadoVisita resultado) {
        exigirEstado(EstadoOrden.EN_ATENCION);
        if (resultado == null) {
            throw new IllegalArgumentException("Debe indicar un resultado.");
        }
        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitasRealizadas >= maximoVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    private void validarVisitasDisponibles() {
        if (visitasRealizadas >= maximoVisitas()) {
            throw new IllegalStateException("La orden agoto sus visitas.");
        }
    }

    private void exigirEstado(EstadoOrden esperado) {
        if (estado != esperado) {
            throw new IllegalStateException("Se requiere " + esperado + "; estado actual: " + estado);
        }
    }

    private static String validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacio.");
        }
        return texto.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitasRealizadas() {
        return visitasRealizadas;
    }
}
