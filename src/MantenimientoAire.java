import java.math.BigDecimal;
import java.math.RoundingMode;

public class MantenimientoAire extends OrdenServicio {

    private final int capacidadBTU;

    public MantenimientoAire(
            String codigo,
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona,
            int capacidadBTU) {

        super(
                codigo,
                cliente,
                direccion,
                horasEstimadas,
                zona);

        if (zona != Zona.METROPOLITANA) {
            throw new IllegalArgumentException(
                    "El mantenimiento de aire solo se permite "
                            + "en la zona metropolitana.");
        }

        if (capacidadBTU < 9000 || capacidadBTU > 60000) {
            throw new IllegalArgumentException(
                    "La capacidad debe estar entre 9000 y 60000 BTU.");
        }

        this.capacidadBTU = capacidadBTU;
    }

    @Override
    public BigDecimal calcularCosto() {
        BigDecimal tarifaBase = new BigDecimal("100.00");
        BigDecimal tarifaHora = new BigDecimal("110.00");
        BigDecimal cargoGas = new BigDecimal("80.00");

        BigDecimal costoHoras = BigDecimal.valueOf(getHorasEstimadas())
                .multiply(tarifaHora);

        return tarifaBase
                .add(costoHoras)
                .add(cargoGas)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int estimarDias() {
        return 2;
    }

    @Override
    public int maximoVisitas() {
        return 1;
    }

    @Override
    public boolean esCompatible(Cuadrilla cuadrilla) {
        return cuadrilla != null
                && cuadrilla.estaCertificadaRefrigeracion();
    }

    @Override
    public String obtenerCategoria() {
        return "Mantenimiento de aire";
    }

    public int getCapacidadBTU() {
        return capacidadBTU;
    }
}
