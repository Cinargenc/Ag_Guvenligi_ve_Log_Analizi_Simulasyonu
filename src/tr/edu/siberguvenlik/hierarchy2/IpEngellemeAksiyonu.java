package tr.edu.siberguvenlik.hierarchy2;

/**
 * [CONCRETE CLASS]
 * Tespit edilen tehdit kaynağının IP adresini Firewall seviyesinde
 * engellemek için kullanılan aktif müdahale sınıfıdır.
 */
public class IpEngellemeAksiyonu extends MudahaleAksiyonu {

    // Müdahaleye özel ek teknik detaylar (Üst sınıfta olmayanlar burada kalır)
    private String firewallKurali;
    private String protokol;
    private boolean kaliciMi;

    public IpEngellemeAksiyonu(String hedefIp) {
        // Üst sınıfın (MudahaleAksiyonu) constructor'ına
        // IP adresi, süre (60 dk) ve etki seviyesini gönderiyoruz.
        super(hedefIp, 60, "TAM_ENGELLEME");

        // Müdahale Politikası Detayları
        this.firewallKurali = "TUM_GIRISLERI_ENGELLE";
        this.protokol = "TCP/UDP";
        this.kaliciMi = false;
    }

    // IpEngellemeAksiyonu.java
    @Override
    public void aksiyonuUygula() {
        // Çıktı Örneği: 🛡️ [MÜDAHALE: log girisi gelen ip engellendi] [TAM_ENGELLEME - 60 dk]
        System.out.print("🛡️ [MÜDAHALE: log girisi gelen ip engellendi] [" +
                getEtkiSeviyesi() + " - " + getMudahaleSuresiDakika() + " dk] ");
    }
}