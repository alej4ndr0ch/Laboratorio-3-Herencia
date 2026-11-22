

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Cotizacion {
    private final String categoria;
    private final BigDecimal costo;
    private final int diasEstimados;

    public Cotizacion(String categoria, BigDecimal costo, int diasEstimados) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar una categoria.");
        }
        if (costo == null || costo.signum() <= 0) {
            throw new IllegalArgumentException("El costo debe ser positivo.");
        }
        BigDecimal costoRedondeado = costo.setScale(2, RoundingMode.HALF_UP);
        if (costoRedondeado.signum() <= 0 || diasEstimados <= 0) {
            throw new IllegalArgumentException("El costo redondeado y los dias deben ser positivos.");
        }
        this.categoria = categoria.trim();
        this.costo = costoRedondeado;
        this.diasEstimados = diasEstimados;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public int getDiasEstimados() {
        return diasEstimados;
    }
}
