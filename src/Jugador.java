import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        boolean[] enFigura = new boolean[cartas.length];
        List<String> mensajes = new ArrayList<>();

        mensajes.addAll(detectarSets(enFigura));

        mensajes.addAll(detectarEscaleras(enFigura));

        StringBuilder sb = new StringBuilder();
        if (mensajes.isEmpty()) {
            sb.append("No se encontraron figuras\n");
        } else {
            for (String m : mensajes) {
                sb.append(m).append("\n");
            }
        }

        sb.append("Puntaje restante: ").append(calcularPuntaje(enFigura));

        return sb.toString();
    }

    public List<String> detectarSets(boolean[] enFigura) {
        List<String> mensajes = new ArrayList<>();
        if (cartas == null) {
            return mensajes;
        }

        int[] contadores = new int[13];
        for (int i = 0; i < cartas.length; i++) {
            int idx = cartas[i].getNombre().ordinal();
            if (idx >= 0 && idx < 13) {
                contadores[idx]++;
            }
        }

        for (int n = 0; n < 13; n++) {
            if (contadores[n] >= 2) {
                NombreCarta nombre = NombreCarta.values()[n];
                String tipo;
                if (contadores[n] == 2) {
                    tipo = "Par";
                } else if (contadores[n] == 3) {
                    tipo = "Terna";
                } else {
                    tipo = "Cuarta";
                }

                mensajes.add(tipo + " de " + nombre);

                int marcadas = 0;
                for (int i = 0; i < cartas.length && marcadas < contadores[n]; i++) {
                    if (cartas[i].getNombre().ordinal() == n) {
                        enFigura[i] = true;
                        marcadas++;
                    }
                }
            }
        }
        return mensajes;
    }

    public List<String> detectarEscaleras(boolean[] enFigura) {
        List<String> mensajes = new ArrayList<>();
        if (cartas == null) {
            return mensajes;
        }

        Map<Pinta, List<int[]>> porPinta = new HashMap<>();

        for (int i = 0; i < cartas.length; i++) {
            Pinta p = cartas[i].getPinta();
            int valor = cartas[i].getNombre().ordinal();
            porPinta.putIfAbsent(p, new ArrayList<>());
            porPinta.get(p).add(new int[] { valor, i });
        }

        for (Pinta p : porPinta.keySet()) {
            List<int[]> lista = porPinta.get(p);
            lista.sort(Comparator.comparingInt(a -> a[0]));

            int Iniciar = 0;
            while (Iniciar < lista.size()) {
                int cd = Iniciar;
                while (cd + 1 < lista.size() && lista.get(cd + 1)[0] == lista.get(cd)[0] + 1) {
                    cd++;
                }

                int len = cd - Iniciar + 1;
                if (len >= 3) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Escalera de ").append(p).append(": ");
                    for (int k = Iniciar; k <= cd; k++) {
                        sb.append(NombreCarta.values()[lista.get(k)[0]]);
                        if (k < cd)
                            sb.append(", ");
                        enFigura[lista.get(k)[1]] = true;
                    }
                    mensajes.add(sb.toString());
                }
                Iniciar = cd + 1;
            }
        }
        return mensajes;
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