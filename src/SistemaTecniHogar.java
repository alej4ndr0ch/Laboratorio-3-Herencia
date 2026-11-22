

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaTecniHogar {
    private final Map<String, OrdenServicio> ordenes = new LinkedHashMap<>();
    private final Map<String, Cuadrilla> cuadrillas = new LinkedHashMap<>();
    private final Map<String, Jornada> jornadas = new LinkedHashMap<>();
    private final List<Ingreso> ingresos = new ArrayList<>();

    public SistemaTecniHogar() {
    }

    public SistemaTecniHogar(List<OrdenServicio> ordenesIniciales,
            List<Cuadrilla> cuadrillasIniciales) {
        if (ordenesIniciales == null || cuadrillasIniciales == null) {
            throw new IllegalArgumentException("Las listas iniciales no pueden ser nulas.");
        }
        for (OrdenServicio orden : ordenesIniciales) {
            validarOrdenNueva(orden);
            ordenes.put(orden.getCodigo(), orden);
        }
        for (Cuadrilla cuadrilla : cuadrillasIniciales) {
            registrarCuadrilla(cuadrilla);
        }
    }

    public Cotizacion cotizar(OrdenServicio propuesta) {
        if (propuesta == null) {
            throw new IllegalArgumentException("Debe indicar una propuesta de servicio.");
        }
        return new Cotizacion(propuesta.obtenerCategoria(),
                propuesta.calcularCosto(), propuesta.estimarDias());
    }

    public void registrarOrden(OrdenServicio orden) {
        validarOrdenNueva(orden);
        Cotizacion cotizacion = cotizar(orden);
        Ingreso ingreso = new Ingreso(orden, TipoIngreso.REGISTRO, cotizacion.getCosto());
        ordenes.put(orden.getCodigo(), orden);
        ingresos.add(ingreso);
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new IllegalArgumentException("Debe indicar una cuadrilla.");
        }
        if (cuadrillas.containsKey(cuadrilla.getCodigo())) {
            throw new IllegalArgumentException("El codigo de cuadrilla ya existe.");
        }
        if (!cuadrilla.estaDisponible()) {
            throw new IllegalStateException("La cuadrilla ya tiene una jornada activa.");
        }
        cuadrillas.put(cuadrilla.getCodigo(), cuadrilla);
    }

    public Jornada crearJornada(String codigo, String codigoCuadrilla) {
        String clave = validarCodigo(codigo);
        if (jornadas.containsKey(clave)) {
            throw new IllegalArgumentException("El codigo de jornada ya existe.");
        }
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        Jornada jornada = new Jornada(clave, cuadrilla);
        jornadas.put(clave, jornada);
        return jornada;
    }

    public void asignarOrden(String codigoJornada, String codigoOrden) {
        buscarJornada(codigoJornada).asignarOrden(buscarOrden(codigoOrden));
    }

    public void retirarOrden(String codigoJornada, String codigoOrden) {
        buscarJornada(codigoJornada).retirarOrden(codigoOrden);
    }

    public BigDecimal iniciarJornada(String codigoJornada) {
        Jornada jornada = buscarJornada(codigoJornada);
        BigDecimal total = jornada.iniciar();
        for (Visita visita : jornada.getVisitas()) {
            if (visita.getCargoAdicional().signum() > 0) {
                ingresos.add(new Ingreso(visita.getOrden(),
                        TipoIngreso.VISITA_ADICIONAL, visita.getCargoAdicional()));
            }
        }
        return total;
    }

    public void registrarResultado(String codigoJornada, String codigoOrden,
            ResultadoVisita resultado) {
        buscarJornada(codigoJornada).registrarResultado(codigoOrden, resultado);
    }

    public BigDecimal calcularIngresoTotal() {
        BigDecimal total = new BigDecimal("0.00");
        for (Ingreso ingreso : ingresos) {
            total = total.add(ingreso.getMonto());
        }
        return total;
    }

    public BigDecimal calcularIngresos(TipoIngreso tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Debe indicar un tipo de ingreso.");
        }
        BigDecimal total = new BigDecimal("0.00");
        for (Ingreso ingreso : ingresos) {
            if (ingreso.getTipo() == tipo) {
                total = total.add(ingreso.getMonto());
            }
        }
        return total;
    }

    public BigDecimal calcularIngresoPorCategoria(String categoria) {
        String clave = validarCodigo(categoria);
        BigDecimal total = new BigDecimal("0.00");
        for (Ingreso ingreso : ingresos) {
            if (ingreso.getOrden().obtenerCategoria().equals(clave)) {
                total = total.add(ingreso.getMonto());
            }
        }
        return total;
    }

    public Map<String, Map<EstadoOrden, Integer>> contarOrdenesPorCategoriaYEstado() {
        Map<String, Map<EstadoOrden, Integer>> reporte = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes.values()) {
            String categoria = orden.obtenerCategoria();
            if (!reporte.containsKey(categoria)) {
                Map<EstadoOrden, Integer> conteos = new EnumMap<>(EstadoOrden.class);
                for (EstadoOrden estado : EstadoOrden.values()) {
                    conteos.put(estado, 0);
                }
                reporte.put(categoria, conteos);
            }
            Map<EstadoOrden, Integer> conteos = reporte.get(categoria);
            EstadoOrden estado = orden.getEstado();
            conteos.put(estado, conteos.get(estado) + 1);
        }
        return reporte;
    }

    public List<Jornada> consultarJornadasAbiertas() {
        List<Jornada> abiertas = new ArrayList<>();
        for (Jornada jornada : jornadas.values()) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                abiertas.add(jornada);
            }
        }
        return Collections.unmodifiableList(abiertas);
    }

    public List<OrdenServicio> consultarOrdenes() {
        return Collections.unmodifiableList(new ArrayList<>(ordenes.values()));
    }

    public List<Cuadrilla> consultarCuadrillas() {
        return Collections.unmodifiableList(new ArrayList<>(cuadrillas.values()));
    }

    public List<Ingreso> consultarIngresos() {
        return Collections.unmodifiableList(new ArrayList<>(ingresos));
    }

    private void validarOrdenNueva(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("Debe indicar una orden.");
        }
        if (ordenes.containsKey(orden.getCodigo())) {
            throw new IllegalArgumentException("El codigo de orden ya existe.");
        }
        if (orden.getEstado() != EstadoOrden.PENDIENTE || orden.getVisitasRealizadas() != 0) {
            throw new IllegalStateException("La orden debe ser nueva y estar pendiente.");
        }
    }

    private OrdenServicio buscarOrden(String codigo) {
        OrdenServicio orden = ordenes.get(validarCodigo(codigo));
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden indicada.");
        }
        return orden;
    }

    private Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla cuadrilla = cuadrillas.get(validarCodigo(codigo));
        if (cuadrilla == null) {
            throw new IllegalArgumentException("No existe la cuadrilla indicada.");
        }
        return cuadrilla;
    }

    private Jornada buscarJornada(String codigo) {
        Jornada jornada = jornadas.get(validarCodigo(codigo));
        if (jornada == null) {
            throw new IllegalArgumentException("No existe la jornada indicada.");
        }
        return jornada;
    }

    private String validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar un codigo o categoria no vacios.");
        }
        return codigo.trim();
    }
}
