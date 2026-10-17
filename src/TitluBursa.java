public class TitluBursa {
    private final String simbol;
    private final double deschidere;
    private final double max;
    private final double min;
    private final double inchidere;
    private final long volum;
    private String denumire = "";

    public TitluBursa(String simbol, double deschidere, double max, double min, double inchidere, long volum) {
        this.simbol = simbol;
        this.deschidere = deschidere;
        this.max = max;
        this.min = min;
        this.inchidere = inchidere;
        this.volum = volum;
    }

    public static TitluBursa dinLinieCsv(String linie) {
        String[] p = linie.split(",");
        return new TitluBursa(p[0].trim(),
                Double.parseDouble(p[1].trim()),
                Double.parseDouble(p[2].trim()),
                Double.parseDouble(p[3].trim()),
                Double.parseDouble(p[4].trim()),
                Long.parseLong(p[5].trim()));
    }

    public String getSimbol() {
        return simbol;
    }

    public double getDeschidere() {
        return deschidere;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getInchidere() {
        return inchidere;
    }

    public long getVolum() {
        return volum;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire == null ? "" : denumire;
    }

    public double getValoare() {
        return inchidere * volum;
    }

    public double getDiferentaProcentuala() {
        return min > 0 ? (max - min) / min * 100 : 0;
    }
}
