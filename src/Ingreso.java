

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Ingreso {
    private final OrdenServicio orden;
    private final TipoIngreso tipo;
    private final BigDecimal monto;

    public Ingreso(OrdenServicio orden, TipoIngreso tipo, BigDecimal monto) {
        if (orden == null || tipo == null) {
            throw new IllegalArgumentException("Debe indicar la orden y el tipo de ingreso.");
        }
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo.");
        }
        BigDecimal montoRedondeado = monto.setScale(2, RoundingMode.HALF_UP);
        if (montoRedondeado.signum() <= 0) {
            throw new IllegalArgumentException("El monto redondeado debe ser positivo.");
        }
        this.orden = orden;
        this.tipo = tipo;
        this.monto = montoRedondeado;
    }

    public OrdenServicio getOrden() {
        return orden;
    }

    public TipoIngreso getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}
