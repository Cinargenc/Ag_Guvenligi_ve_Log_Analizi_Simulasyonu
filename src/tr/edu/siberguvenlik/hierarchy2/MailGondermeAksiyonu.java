package tr.edu.siberguvenlik.hierarchy2;

/**
 * [CONCRETE CLASS] - Kritik tehditleri yöneticiye bildiren aksiyon sınıfı.
 * Doğrudan GuvenlikAksiyonu sınıfından türetilmiştir.
 */
public class MailGondermeAksiyonu extends GuvenlikAksiyonu {

    private String konuBasligi;

    public MailGondermeAksiyonu(String saldirganIp, String riskDetayi) {
        super(saldirganIp); // Kimlik ve zaman bilgisi için ata sınıfa git
        this.konuBasligi = "ACIL DURUM: [" + saldirganIp + "] - " + riskDetayi;
    }

    @Override
    public void aksiyonuUygula() {
        // İstediğin tek satırlık net çıktı
        System.out.print("📧 [BİLDİRİM: admine eposta gonderildi] ");
    }
}