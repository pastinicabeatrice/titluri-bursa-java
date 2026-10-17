import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    static File gasesteFisier(String nume) {
        for (String dir : new String[] { "date", "../date", "." }) {
            File f = new File(dir, nume);
            if (f.exists()) {
                return f;
            }
        }
        return new File("date", nume);
    }

    static List<TitluBursa> citesteTitluri(File fisier) throws IOException {
        List<TitluBursa> titluri = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fisier))) {
            String linie = br.readLine();
            while ((linie = br.readLine()) != null) {
                if (!linie.isBlank()) {
                    titluri.add(TitluBursa.dinLinieCsv(linie));
                }
            }
        }
        return titluri;
    }

    static Map<String, String> citesteDenumiri(File bazaDate) {
        Map<String, String> denumiri = new HashMap<>();
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + bazaDate.getPath());
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT Simbol, Denumire FROM Titluri")) {
            while (rs.next()) {
                denumiri.put(rs.getString("Simbol"), rs.getString("Denumire"));
            }
        } catch (SQLException e) {
            System.out.println("Denumirile nu au putut fi citite din baza de date: " + e.getMessage());
        }
        return denumiri;
    }

    static void cerinta1(List<TitluBursa> titluri) {
        TitluBursa min = titluri.stream().min(Comparator.comparingDouble(TitluBursa::getValoare)).orElseThrow();
        TitluBursa max = titluri.stream().max(Comparator.comparingDouble(TitluBursa::getValoare)).orElseThrow();
        System.out.println("Titlurile cu valoarea tranzactionata minima si maxima:");
        System.out.println("Simbol\t\tValoare");
        System.out.printf("%s\t\t%.2f%n", min.getSimbol(), min.getValoare());
        System.out.printf("%s\t\t%.2f%n", max.getSimbol(), max.getValoare());
    }

    static void cerinta2(List<TitluBursa> titluri) {
        System.out.println("\nTitluri sortate descrescator dupa volum:");
        System.out.println("Simbol\t\tDenumire\t\t\t\tVolum");
        titluri.stream()
                .sorted(Comparator.comparingLong(TitluBursa::getVolum).reversed())
                .forEach(t -> System.out.printf("%-8s\t%-36s\t%d%n", t.getSimbol(), t.getDenumire(), t.getVolum()));
    }

    static void cerinta3(List<TitluBursa> titluri) {
        System.out.println("\nTitluri cu diferenta Max-Min peste 1%, descrescator:");
        System.out.println("Simbol\t\tDenumire\t\t\t\tDiferenta");
        titluri.stream()
                .filter(t -> t.getDiferentaProcentuala() > 1)
                .sorted(Comparator.comparingDouble(TitluBursa::getDiferentaProcentuala).reversed())
                .forEach(t -> System.out.printf("%-8s\t%-36s\t%.2f%%%n", t.getSimbol(), t.getDenumire(), t.getDiferentaProcentuala()));
    }

    static void cerinta4(List<TitluBursa> titluri) throws Exception {
        Map<String, TitluBursa> index = new HashMap<>();
        for (TitluBursa t : titluri) {
            index.put(t.getSimbol(), t);
        }
        System.out.println("\nComunicare client-server TCP/IP:");
        try (ServerTitluri server = new ServerTitluri(0, index)) {
            server.porneste();
            List<Thread> clienti = new ArrayList<>();
            for (String simbol : new String[] { "AACG", "AADI", "XYZ" }) {
                Thread t = new Thread(() -> {
                    try {
                        String raspuns = ClientTitluri.interogheaza("localhost", server.getPort(), simbol);
                        System.out.println("  Client " + simbol + " -> " + raspuns);
                    } catch (IOException e) {
                        System.out.println("  Client " + simbol + ": " + e.getMessage());
                    }
                });
                clienti.add(t);
                t.start();
            }
            for (Thread t : clienti) {
                t.join();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<TitluBursa> titluri = citesteTitluri(gasesteFisier("PretVolum.txt"));
        if (titluri.isEmpty()) {
            System.out.println("Nu s-au citit titluri.");
            return;
        }
        Map<String, String> denumiri = citesteDenumiri(gasesteFisier("Titluri.db"));
        titluri.forEach(t -> t.setDenumire(denumiri.get(t.getSimbol())));

        cerinta1(titluri);
        cerinta2(titluri);
        cerinta3(titluri);
        cerinta4(titluri);
    }
}
