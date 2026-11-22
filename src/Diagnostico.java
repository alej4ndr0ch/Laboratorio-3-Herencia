

import java.math.BigDecimal;

public class Diagnostico extends OrdenServicio {

    public Diagnostico(
            String codigo,
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona) {

        super(codigo, cliente, direccion, horasEstimadas, zona);

        if (horasEstimadas > 1.5) {
            throw new IllegalArgumentException(
                    "Un diagnostico no puede superar 1.5 horas.");
        }
    }

    @Override
    public BigDecimal calcularCosto() {
        return getZona() == Zona.METROPOLITANA
                ? new BigDecimal("150.00")
                : new BigDecimal("225.00");
    }

    @Override
    public int estimarDias() {
        return getZona() == Zona.METROPOLITANA ? 1 : 2;
    }

    @Override
    public String obtenerCategoria() {
        return "Diagnostico";
    }
}
