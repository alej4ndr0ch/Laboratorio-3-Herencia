

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InstalacionElectricaCertificada
        extends Instalacion {

    private final BigDecimal valorMateriales;

    public InstalacionElectricaCertificada(
            String codigo,
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona,
            int cantidadPuntos,
            BigDecimal valorMateriales) {

        super(
                codigo,
                cliente,
                direccion,
                horasEstimadas,
                zona,
                cantidadPuntos);

        if (valorMateriales == null
                || valorMateriales.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El valor de materiales debe ser positivo.");
        }

        this.valorMateriales = valorMateriales;
    }

    @Override
    public BigDecimal calcularCosto() {
        BigDecimal costoInstalacion = super.calcularCosto();

        BigDecimal certificado = new BigDecimal("120.00");

        BigDecimal garantia = valorMateriales.multiply(
                new BigDecimal("0.05"));

        return costoInstalacion
                .add(certificado)
                .add(garantia)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int estimarDias() {
        return super.estimarDias() + 2;
    }

    @Override
    public int maximoVisitas() {
        return 2;
    }

    @Override
    public String obtenerCategoria() {
        return "Instalacion electrica certificada";
    }

    public BigDecimal getValorMateriales() {
        return valorMateriales;
    }
}
