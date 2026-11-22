

import java.math.BigDecimal;

public class Instalacion extends OrdenServicio {

    private final int cantidadPuntos;

    public Instalacion(
            String codigo,
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona,
            int cantidadPuntos) {

        super(codigo, cliente, direccion, horasEstimadas, zona);

        if (cantidadPuntos <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de puntos debe ser positiva.");
        }

        this.cantidadPuntos = cantidadPuntos;
    }

    public double calcularHorasFacturables() {
        double horasPorPuntos = cantidadPuntos * 0.75;

        return Math.ceil(
                Math.max(getHorasEstimadas(), horasPorPuntos));
    }

    @Override
    public BigDecimal calcularCosto() {
        BigDecimal tarifaBase = getZona() == Zona.METROPOLITANA
                ? new BigDecimal("100.00")
                : new BigDecimal("175.00");

        BigDecimal costoHoras = BigDecimal.valueOf(calcularHorasFacturables())
                .multiply(new BigDecimal("90.00"));

        return tarifaBase.add(costoHoras);
    }

    @Override
    public int estimarDias() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }

    @Override
    public String obtenerCategoria() {
        return "Instalacion";
    }

    public int getCantidadPuntos() {
        return cantidadPuntos;
    }
}
