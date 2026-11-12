package src;
public class Moeda {

    private Moeda() {
    }

    public static long paraCentavos(String texto) {
        String limpo = texto.trim();
        if (!limpo.matches("\\d{1,12}([.,]\\d{1,2})?")) {
            throw new IllegalArgumentException("Preço inválido. Use o formato 12,50 ou 12.50.");
        }
        String[] partes = limpo.split("[.,]");
        long reais = Long.parseLong(partes[0]);
        long centavos = 0;
        if (partes.length == 2) {
            String parteCentavos = partes[1].length() == 1 ? partes[1] + "0" : partes[1];
            centavos = Long.parseLong(parteCentavos);
        }
        return reais * 100 + centavos;
    }

    public static String formatar(long centavos) {
        return String.format("R$ %d,%02d", centavos / 100, centavos % 100);
    }
}