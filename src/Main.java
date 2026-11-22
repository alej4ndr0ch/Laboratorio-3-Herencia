import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SistemaTecniHogar sistema = crearSistemaInicial();
        Scanner entrada = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();
            try {
                int opcion = leerEntero(entrada, "Opcion: ", 0, 11);
                switch (opcion) {
                    case 1: {
                        OrdenServicio propuesta = leerOrden(entrada);
                        mostrarCotizacion(sistema.cotizar(propuesta));
                        break;
                    }
                    case 2: {
                        OrdenServicio orden = leerOrden(entrada);
                        sistema.registrarOrden(orden);
                        System.out.println("Orden registrada y cobro registrado.");
                        mostrarCotizacion(sistema.cotizar(orden));
                        break;
                    }
                    case 3: {
                        String codigo = leerTexto(entrada, "Codigo de cuadrilla: ");
                        double capacidad = leerPositivo(entrada, "Capacidad en horas: ");
                        boolean certificada = leerSiNo(entrada, "Certificada en refrigeracion (s/n): ");
                        sistema.registrarCuadrilla(new Cuadrilla(codigo, capacidad, certificada));
                        System.out.println("Cuadrilla registrada.");
                        break;
                    }
                    case 4: {
                        String jornada = leerTexto(entrada, "Codigo de nueva jornada: ");
                        String cuadrilla = leerTexto(entrada, "Codigo de cuadrilla: ");
                        sistema.crearJornada(jornada, cuadrilla);
                        System.out.println("Jornada creada.");
                        break;
                    }
                    case 5: {
                        String jornada = leerTexto(entrada, "Codigo de jornada: ");
                        String orden = leerTexto(entrada, "Codigo de orden: ");
                        sistema.asignarOrden(jornada, orden);
                        System.out.println("Orden asignada.");
                        break;
                    }
                    case 6: {
                        String jornada = leerTexto(entrada, "Codigo de jornada: ");
                        String orden = leerTexto(entrada, "Codigo de orden: ");
                        sistema.retirarOrden(jornada, orden);
                        System.out.println("Orden retirada; vuelve a pendiente.");
                        break;
                    }
                    case 7: {
                        String jornada = leerTexto(entrada, "Codigo de jornada: ");
                        BigDecimal cargos = sistema.iniciarJornada(jornada);
                        System.out.printf(Locale.US,
                                "Jornada iniciada. Cargos adicionales: Q%.2f%n", cargos);
                        break;
                    }
                    case 8: {
                        String jornada = leerTexto(entrada, "Codigo de jornada: ");
                        String orden = leerTexto(entrada, "Codigo de orden: ");
                        int resultado = leerEntero(entrada, "1. Completada / 2. Fallida: ", 1, 2);
                        sistema.registrarResultado(jornada, orden,
                                resultado == 1 ? ResultadoVisita.COMPLETADA : ResultadoVisita.FALLIDA);
                        System.out.println("Resultado registrado.");
                        break;
                    }
                    case 9:
                        mostrarOrdenes(sistema);
                        break;
                    case 10:
                        mostrarCuadrillas(sistema);
                        break;
                    case 11:
                        mostrarReportes(sistema);
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Programa finalizado.");
                        break;
                    default:
                        break;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Entrada finalizada.");
                continuar = false;
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operacion rechazada: " + e.getMessage());
            }
        }
        entrada.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n===== TECNIHOGAR =====");
        System.out.println("1. Cotizar servicio");
        System.out.println("2. Registrar orden y cobro");
        System.out.println("3. Registrar cuadrilla");
        System.out.println("4. Crear jornada");
        System.out.println("5. Asignar orden a jornada");
        System.out.println("6. Retirar orden de jornada");
        System.out.println("7. Iniciar jornada");
        System.out.println("8. Registrar resultado de visita");
        System.out.println("9. Consultar ordenes");
        System.out.println("10. Consultar cuadrillas");
        System.out.println("11. Generar reportes");
        System.out.println("0. Salir");
    }

    private static OrdenServicio leerOrden(Scanner entrada) {
        System.out.println("1. Diagnostico");
        System.out.println("2. Instalacion");
        System.out.println("3. Instalacion electrica certificada");
        System.out.println("4. Mantenimiento de aire acondicionado");
        int categoria = leerEntero(entrada, "Categoria: ", 1, 4);
        String codigo = leerTexto(entrada, "Codigo de orden: ");
        String cliente = leerTexto(entrada, "Cliente: ");
        String direccion = leerTexto(entrada, "Direccion: ");
        int opcionZona = leerEntero(entrada, "Zona: 1. Metropolitana / 2. Periferica: ", 1, 2);
        Zona zona = opcionZona == 1 ? Zona.METROPOLITANA : Zona.PERIFERICA;
        double horas = leerPositivo(entrada, "Horas estimadas: ");

        switch (categoria) {
            case 1:
                return new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2: {
                int puntos = leerEntero(entrada, "Cantidad de puntos: ", 1, Integer.MAX_VALUE);
                return new Instalacion(codigo, cliente, direccion, horas, zona, puntos);
            }
            case 3: {
                int puntos = leerEntero(entrada, "Cantidad de puntos: ", 1, Integer.MAX_VALUE);
                BigDecimal materiales = leerMonto(entrada, "Valor de materiales: Q");
                return new InstalacionElectricaCertificada(
                        codigo, cliente, direccion, horas, zona, puntos, materiales);
            }
            case 4: {
                int btu = leerEntero(entrada, "Capacidad en BTU: ", 9000, 60000);
                return new MantenimientoAire(codigo, cliente, direccion, horas, zona, btu);
            }
            default:
                throw new IllegalArgumentException("Categoria invalida.");
        }
    }

    private static void mostrarCotizacion(Cotizacion cotizacion) {
        System.out.println("Categoria: " + cotizacion.getCategoria());
        System.out.printf(Locale.US, "Costo: Q%.2f%n", cotizacion.getCosto());
        System.out.println("Tiempo estimado: " + cotizacion.getDiasEstimados() + " dias habiles.");
    }

    private static void mostrarOrdenes(SistemaTecniHogar sistema) {
        if (sistema.consultarOrdenes().isEmpty()) {
            System.out.println("No hay ordenes registradas.");
            return;
        }
        for (OrdenServicio orden : sistema.consultarOrdenes()) {
            System.out.printf(Locale.US,
                    "%s | %s | %s | %s | %s | %.2f h | %s | visitas %d/%d%n",
                    orden.getCodigo(), orden.obtenerCategoria(), orden.getCliente(),
                    orden.getDireccion(), orden.getZona(), orden.getHorasEstimadas(),
                    orden.getEstado(), orden.getVisitasRealizadas(), orden.maximoVisitas());
        }
    }

    private static void mostrarCuadrillas(SistemaTecniHogar sistema) {
        if (sistema.consultarCuadrillas().isEmpty()) {
            System.out.println("No hay cuadrillas registradas.");
            return;
        }
        for (Cuadrilla cuadrilla : sistema.consultarCuadrillas()) {
            System.out.printf(Locale.US,
                    "%s | capacidad %.2f h | refrigeracion: %s | jornada activa: %s%n",
                    cuadrilla.getCodigo(), cuadrilla.getCapacidadHoras(),
                    cuadrilla.estaCertificadaRefrigeracion() ? "si" : "no",
                    cuadrilla.estaDisponible() ? "ninguna" : cuadrilla.getJornadaActiva().getCodigo());
        }
    }

    private static void mostrarReportes(SistemaTecniHogar sistema) {
        Map<String, Map<EstadoOrden, Integer>> conteos = sistema.contarOrdenesPorCategoriaYEstado();
        System.out.println("\nORDENES POR CATEGORIA Y ESTADO");
        for (Map.Entry<String, Map<EstadoOrden, Integer>> categoria : conteos.entrySet()) {
            System.out.println(categoria.getKey());
            for (EstadoOrden estado : EstadoOrden.values()) {
                System.out.println("  " + estado + ": " + categoria.getValue().get(estado));
            }
        }
        System.out.println("\nINGRESOS");
        System.out.printf(Locale.US, "Por registro: Q%.2f%n",
                sistema.calcularIngresos(TipoIngreso.REGISTRO));
        System.out.printf(Locale.US, "Por visitas adicionales: Q%.2f%n",
                sistema.calcularIngresos(TipoIngreso.VISITA_ADICIONAL));
        System.out.printf(Locale.US, "Total: Q%.2f%n", sistema.calcularIngresoTotal());
        for (String categoria : conteos.keySet()) {
            System.out.printf(Locale.US, "%s: Q%.2f%n", categoria,
                    sistema.calcularIngresoPorCategoria(categoria));
        }
        System.out.println("\nJORNADAS SIN CERRAR");
        List<Jornada> abiertas = sistema.consultarJornadasAbiertas();
        if (abiertas.isEmpty()) {
            System.out.println("No hay jornadas abiertas.");
        }
        for (Jornada jornada : abiertas) {
            System.out.printf(Locale.US,
                    "%s | %s | cuadrilla %s | asignadas %.2f h | disponibles %.2f h%n",
                    jornada.getCodigo(), jornada.getEstado(), jornada.getCuadrilla().getCodigo(),
                    jornada.calcularHorasAsignadas(), jornada.calcularCapacidadDisponible());
        }
    }

    private static String leerTexto(Scanner entrada, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("El valor no puede estar vacio.");
        }
    }

    private static int leerEntero(Scanner entrada, String mensaje, int minimo, int maximo) {
        while (true) {
            try {
                int valor = Integer.parseInt(leerTexto(entrada, mensaje));
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Ingrese un entero entre " + minimo + " y " + maximo + ".");
        }
    }

    private static double leerPositivo(Scanner entrada, String mensaje) {
        while (true) {
            try {
                double valor = Double.parseDouble(leerTexto(entrada, mensaje).replace(',', '.'));
                if (Double.isFinite(valor) && valor > 0) {
                    return valor;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Ingrese un numero positivo y finito.");
        }
    }

    private static BigDecimal leerMonto(Scanner entrada, String mensaje) {
        while (true) {
            try {
                BigDecimal monto = new BigDecimal(leerTexto(entrada, mensaje).replace(',', '.'));
                if (monto.signum() > 0) {
                    return monto;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Ingrese un monto positivo, sin separadores de miles.");
        }
    }

    private static boolean leerSiNo(Scanner entrada, String mensaje) {
        while (true) {
            String respuesta = leerTexto(entrada, mensaje);
            if (respuesta.equalsIgnoreCase("s")) {
                return true;
            }
            if (respuesta.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Responda s o n.");
        }
    }

    private static SistemaTecniHogar crearSistemaInicial() {
        List<OrdenServicio> ordenes = Arrays.<OrdenServicio>asList(
                new Diagnostico("D01", "Ana", "Zona 1", 1.0, Zona.METROPOLITANA),
                new Diagnostico("D02", "Luis", "Zona periferica", 1.5, Zona.PERIFERICA),
                new Instalacion("I01", "Maria", "Zona 5", 2.5, Zona.METROPOLITANA, 5),
                new Instalacion("I02", "Pedro", "Zona periferica", 2.0, Zona.PERIFERICA, 2),
                new InstalacionElectricaCertificada("E01", "Sofia", "Zona 10", 2.5,
                        Zona.METROPOLITANA, 5, new BigDecimal("2000.00")),
                new InstalacionElectricaCertificada("E02", "Carlos", "Zona periferica", 3.0,
                        Zona.PERIFERICA, 2, new BigDecimal("1500.00")),
                new MantenimientoAire("A01", "Elena", "Zona 11", 1.0, Zona.METROPOLITANA, 9000),
                new MantenimientoAire("A02", "Jorge", "Zona 14", 2.5, Zona.METROPOLITANA, 24000));
        List<Cuadrilla> cuadrillas = Arrays.asList(
                new Cuadrilla("C01", 8.0, true),
                new Cuadrilla("C02", 6.0, false),
                new Cuadrilla("C03", 10.0, true));
        return new SistemaTecniHogar(ordenes, cuadrillas);
    }
}