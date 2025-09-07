
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

    
    int[] contadores = new int[13]; 
    for (Carta carta : cartas) {
        contadores[carta.getNombre().ordinal()]++;
    }

    boolean hayGrupos = false;
    int puntajeRestante = 0;

    // tectectar ternas y cuartas 
    for (int c : contadores) {
        if (c >= 2) {
            hayGrupos = true;
            break;
        }
    }

    if (hayGrupos) {
        resultado.append("Se encontraron los siguientes grupos:\n");
        for (int p = 0; p < contadores.length; p++) {
            int c = contadores[p];
            if (c == 2) {
                resultado.append("Par de ").append(NombreCarta.values()[p]).append("\n");
            } else if (c == 3) {
                resultado.append("Terna de ").append(NombreCarta.values()[p]).append("\n");
            } else if (c == 4) {
                resultado.append("Cuarta de ").append(NombreCarta.values()[p]).append("\n");
            } else if (c == 1) {
                puntajeRestante += valorCartaSueltaPorIndice(p);
            }
        }
    } else {
        resultado.append("No se encontraron grupos\n");
        for (int p = 0; p < contadores.length; p++) {
            int c = contadores[p];
            if (c > 0) {
                puntajeRestante += valorCartaSueltaPorIndice(p) * c;
            }
        }
    }

    //dectetar escaleras de 3 o mas
    for (Pinta pinta : Pinta.values()) {
        boolean[] presentes = new boolean[13]; 

        for (Carta carta : cartas) {
            if (carta.getPinta() == pinta) {
                presentes[carta.getNombre().ordinal()] = true;
            }
        }

        // Buscar escalares
        int i = 0;
        while (i < 13) {
            if (presentes[i]) {
                int j = i;
                while (j + 1 < 13 && presentes[j + 1]) {
                    j++;
                }

                int len = j - i + 1;
                if (len >= 3) {
                    resultado.append("Escalera de ").append(pinta).append(": ");
                    for (int k = i; k <= j; k++) {
                        resultado.append(NombreCarta.values()[k]);
                        if (k < j) resultado.append(", ");
                    }
                    resultado.append("\n");
                }
                i = j + 1;
            } else {
                i++;
            }
        }
    }

    resultado.append("Puntaje restante: ").append(puntajeRestante);

    return resultado.toString();
}
private int valorCartaSueltaPorIndice(int idx) {
    if (idx == 0 || idx >= 10) return 10; 
    return idx + 1;
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