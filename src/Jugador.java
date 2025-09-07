import java.util.Random;

import javax.swing.JPanel;

public class Jugador {

    private final int TOTAL_CARTAS = 10;
    private final int SEPARACION = 40;
    private final int MARGEN = 10;
    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    public Jugador() {
        r = new Random();
    }

    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        int posicion = MARGEN + TOTAL_CARTAS * SEPARACION;
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i].mostrar(pnl, posicion, MARGEN);
            posicion -= SEPARACION;
        }
        pnl.repaint();
    }

    public String getGrupos() {
        StringBuilder resultado = new StringBuilder();

        // Iniciar contadores por valor (A=0, 2=1, ..., 10=9, J=10, Q=11, K=12)
        int[] contadores = new int[13];
        for (Carta carta : cartas) {
            contadores[carta.getNombre().ordinal()]++;
        }

        // ¿Hay algún grupo (par, terna, cuarta)?
        boolean hayGrupos = false;
        for (int c : contadores) {
            if (c >= 2) {
                hayGrupos = true;
                break;
            }
        }

        int puntajeRestante = 0;

        if (hayGrupos) {
            resultado.append("Se encontraron los siguientes grupos:\n");
            for (int p = 0; p < contadores.length; p++) {
                int c = contadores[p];
                if (c >= 2) {
                    resultado.append(Grupo.values()[c])
                            .append(" de ")
                            .append(NombreCarta.values()[p])
                            .append("\n");
                } else if (c == 1) {
                    // Carta suelta: sumar su valor según regla (A,J,Q,K = 10)
                    puntajeRestante += valorCartaSueltaPorIndice(p);
                }
            }
        } else {
            resultado.append("No se encontraron figuras\n");
            // Si no hay grupos, todas las cartas suman al puntaje
            for (int p = 0; p < contadores.length; p++) {
                int c = contadores[p];
                if (c > 0) {
                    puntajeRestante += valorCartaSueltaPorIndice(p) * c;
                }
            }
        }

        resultado.append("Puntaje restante: ").append(puntajeRestante);
        return resultado.toString();
    }

    // 0: AS, 1..9: 2..10, 10: JACK, 11: QUEEN, 12: KING
    private int valorCartaSueltaPorIndice(int idx) {
        // AS (0) y figuras (J,Q,K -> 10,11,12) valen 10
        if (idx == 0 || idx >= 10)
            return 10;
        // Números 2..10 valen su número
        return idx + 1; // 1->2, 2->3, ..., 9->10
    }

    public int calcularPuntaje(boolean[] enFigura) {
        int suma = 0;
        for (int i = 0; i < cartas.length; i++) {
            if (!enFigura[i]) {
                NombreCarta none = cartas[i].getNombre();
                int val;
                switch (none) {
                    case AS:
                    case JACK:
                    case QUEEN:
                    case KING:
                        val = 10;
                        break;
                    default:
                        val = none.ordinal() + 1;
                }
                suma += val;
            }
        }
        return suma;
    }
}