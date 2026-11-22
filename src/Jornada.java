

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jornada {

    private final String codigo;
    private final Cuadrilla cuadrilla;

    private final List<Visita> visitas = new ArrayList<>();

    private EstadoJornada estado = EstadoJornada.EN_PREPARACION;

    public Jornada(String codigo, Cuadrilla cuadrilla) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El codigo no puede estar vacio.");
        }

        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                    "Debe indicar una cuadrilla.");
        }

        this.codigo = codigo.trim();
        this.cuadrilla = cuadrilla;

        cuadrilla.reservar(this);
    }

    public void asignarOrden(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION);

        if (orden == null) {
            throw new IllegalArgumentException(
                    "Debe indicar una orden.");
        }

        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException(
                    "La orden debe estar pendiente.");
        }

        for (Visita visita : visitas) {
            if (visita.getOrden().getCodigo()
                    .equals(orden.getCodigo())) {
                throw new IllegalArgumentException(
                        "La orden ya esta en esta jornada.");
            }
        }

        if (!orden.esCompatible(cuadrilla)) {
            throw new IllegalArgumentException(
                    "Orden incompatible con la cuadrilla.");
        }

        double nuevasHoras = calcularHorasAsignadas()
                + orden.getHorasEstimadas();

        if (nuevasHoras > cuadrilla.getCapacidadHoras()) {
            throw new IllegalArgumentException(
                    "La asignacion supera la capacidad.");
        }

        Visita visita = new Visita(orden);

        orden.marcarAsignada();
        visitas.add(visita);
    }

    public void retirarOrden(String codigoOrden) {
        exigirEstado(EstadoJornada.EN_PREPARACION);

        Visita visita = buscarVisita(codigoOrden);

        visita.getOrden().retirarAsignacion();
        visitas.remove(visita);
    }

    public BigDecimal iniciar() {
        exigirEstado(EstadoJornada.EN_PREPARACION);

        if (visitas.isEmpty()) {
            throw new IllegalStateException(
                    "No puede iniciar una jornada vacia.");
        }

        for (Visita visita : visitas) {
            visita.validarInicio();
        }

        BigDecimal cargos = new BigDecimal("0.00");

        for (Visita visita : visitas) {
            visita.iniciar();
            cargos = cargos.add(visita.getCargoAdicional());
        }

        estado = EstadoJornada.EN_CURSO;

        return cargos;
    }

    public void registrarResultado(
            String codigoOrden,
            ResultadoVisita resultado) {

        exigirEstado(EstadoJornada.EN_CURSO);

        Visita visita = buscarVisita(codigoOrden);
        visita.registrarResultado(resultado);

        for (Visita actual : visitas) {
            if (!actual.tieneResultado()) {
                return;
            }
        }

        estado = EstadoJornada.CERRADA;
        cuadrilla.liberar(this);
    }

    public double calcularHorasAsignadas() {
        double total = 0;

        for (Visita visita : visitas) {
            total += visita.getOrden().getHorasEstimadas();
        }

        return total;
    }

    public double calcularCapacidadDisponible() {
        return cuadrilla.getCapacidadHoras()
                - calcularHorasAsignadas();
    }

    private Visita buscarVisita(String codigoOrden) {
        if (codigoOrden != null) {
            for (Visita visita : visitas) {
                if (visita.getOrden().getCodigo()
                        .equals(codigoOrden.trim())) {
                    return visita;
                }
            }
        }

        throw new IllegalArgumentException(
                "La orden no pertenece a esta jornada.");
    }

    private void exigirEstado(EstadoJornada esperado) {
        if (estado != esperado) {
            throw new IllegalStateException(
                    "Se requiere " + esperado
                            + "; estado actual: " + estado);
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public List<Visita> getVisitas() {
        return Collections.unmodifiableList(
                new ArrayList<>(visitas));
    }
}
