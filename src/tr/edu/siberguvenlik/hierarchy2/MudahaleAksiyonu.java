package tr.edu.siberguvenlik.hierarchy2;

/**
 * [INTERMEDIATE CLASS] - Teknik müdahale gerektiren aksiyonların kategorisidir.
 * Tüm müdahalelerin ortak özelliklerini (süre, etki seviyesi) burada topluyoruz.
 */
public abstract class MudahaleAksiyonu extends GuvenlikAksiyonu {

    private int mudahaleSuresiDakika; // Tüm teknik müdahalelerin bir süresi olur
    private String etkiSeviyesi;      // Örn: HAFIF, ORTA, TAM_ENGELLEME

    public MudahaleAksiyonu(String tetikleyenIp, int sure, String seviye) {
        super(tetikleyenIp);
        this.mudahaleSuresiDakika = sure;
        this.etkiSeviyesi = seviye;
    }


    // Getters
    public int getMudahaleSuresiDakika() { return mudahaleSuresiDakika; }
    public String getEtkiSeviyesi() { return etkiSeviyesi; }
}