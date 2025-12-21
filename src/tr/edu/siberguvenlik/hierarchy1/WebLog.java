package tr.edu.siberguvenlik.hierarchy1;

import tr.edu.siberguvenlik.exceptions.GecersizIpAdresiException;

/**
 * Web sunucularina (Apache, Nginx, IIS vb.) gelen HTTP isteklerini
 * analiz etmek icin ozellestirilmis log sinifi.
 */
public class WebLog extends UygulamaLog {

    private String url;
    private String httpMetodu; // GET, POST, DELETE vb.
    private int httpDurumKodu; // 200, 404, 500 vb.

    // Yapici Metot
    public WebLog(String ipAdresi, String mesaj, String url, String httpMetodu, int httpDurumKodu) throws GecersizIpAdresiException {
        // Ust sinifa (UygulamaLog) verileri gonderiyoruz.
        // Eger HTTP kodu 500 ve uzeriyse bu bir sunucu hatasidir (ERROR), degilse bilgidir (INFO).
        super(ipAdresi, mesaj, "WebServer", (httpDurumKodu >= 500 ? "ERROR" : "INFO"));

        this.url = url;
        this.httpMetodu = httpMetodu;
        this.httpDurumKodu = httpDurumKodu;

        // Kritik bolgeye erisim kontrolu
        if (url.contains("admin") || url.contains("yonetim")) {
            setOncelikSeviyesi((byte) 10); // En yuksek oncelik
        }
    }

    // --- OVERRIDE EDILEN METOTLAR ---

    @Override
    public void logDetaylariniYazdir() {
        // Hem standart log yapisini hem de Web'e ozel detaylari yazdiriyoruz
        System.out.println("WEB LOG [" + getLogSeviyesi() + "]: " + httpMetodu + " -> " + url + " [Kod: " + httpDurumKodu + "]");
    }

    @Override
    public String logTipiniGetir() {
        return "WEB_LOG";
    }


    @Override
    public int riskPuaniHesapla() {
        java.util.Random rand = new java.util.Random();
        int puan = 0;

        if (httpDurumKodu >= 500) {
            puan = 85 + rand.nextInt(16); // 85-100 arası (KRITIK)
        } else if (httpDurumKodu == 403) {
            // Yüksek Risk (65-84) aralığını yakalamak için:
            puan = 65 + rand.nextInt(15); // 65-80 arası (YUKSEK RISK)
        } else if (httpDurumKodu == 404) {
            puan = 35 + rand.nextInt(20); // 35-55 arası (DIKKAT)
        } else if (httpDurumKodu >= 400) {
            puan = 30 + rand.nextInt(15); // 30-45 arası (Normal/Dikkat geçişi)
        }
        return Math.min(puan, 100);
    }

    // JSON formatini WebLog'a ozel hale getiriyoruz
    @Override
    public String toJson() {
        return String.format("{ \"tip\": \"WEB\", \"url\": \"%s\", \"metot\": \"%s\", \"durum\": %d }",
                url, httpMetodu, httpDurumKodu);
    }

    @Override
    public void fromJson(String jsonVerisi) {
        // Gercek bir JSON parser (Gson/Jackson) kullanmadigimiz icin
        // sadece veriyi aldigimizi simule ediyoruz.
        System.out.println("   📥 [PARSER] Gelen JSON isleniyor...");
        System.out.println("   🔹 Ham Veri: " + jsonVerisi);
        System.out.println("   ✅ Veri nesneye basariyla map edildi (Simulasyon).");
    }
}