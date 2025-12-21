package tr.edu.siberguvenlik.hierarchy1;

import tr.edu.siberguvenlik.exceptions.GecersizIpAdresiException;

/**
 * Standart yazilim uygulamalarinin (SQL Server, Apache, Backend vb.)
 * urettigi loglari temsil eden sinif.
 */
public class UygulamaLog extends LogEntry {

    private String uygulamaAdi;
    private String logSeviyesi; // Orn: INFO, WARN, ERROR, FATAL

    // Yapici Metot
    public UygulamaLog(String ipAdresi, String mesaj, String uygulamaAdi, String logSeviyesi) throws GecersizIpAdresiException {
        super(ipAdresi, mesaj); // ID ve Tarih atamasi icin ataya git
        this.uygulamaAdi = uygulamaAdi;
        this.logSeviyesi = logSeviyesi;

        // Log seviyesine gore oncelik (Priority) belirle
        if ("ERROR".equals(logSeviyesi) || "FATAL".equals(logSeviyesi)) {
            setOncelikSeviyesi((byte) 9); // Yuksek Oncelik
        } else if ("WARN".equals(logSeviyesi)) {
            setOncelikSeviyesi((byte) 5); // Orta Oncelik
        } else {
            setOncelikSeviyesi((byte) 2); // Dusuk Oncelik
        }
    }

    // --- GETTER METOTLARI ---
    public String getUygulamaAdi() { return uygulamaAdi; }
    public String getLogSeviyesi() { return logSeviyesi; }

    // --- LOG ENTRY SOYUT METOTLARI (OVERRIDE) ---

    @Override
    public void logDetaylariniYazdir() {
        // getUygulamaAdi() burada ve IDS içindeki detaylarda çok daha anlamlı hale geldi
        System.out.println("--------------------------------------------------");
        System.out.println("📌 UYGULAMA KAYNAĞI : " + getUygulamaAdi()); // Getter burada devrede
        System.out.println("📊 LOG SEVİYESİ    : " + logSeviyesi);
        System.out.println("💬 MESAJ           : " + getMesaj());
        System.out.println("--------------------------------------------------");
    }

    @Override
    public String logTipiniGetir() {
        return "UYGULAMA_LOG";
    }

    @Override
    public int riskPuaniHesapla() {
        java.util.Random rand = new java.util.Random();

        switch (this.logSeviyesi) {
            case "FATAL":
                // 85 - 100 arası (KRITIK)
                return 85 + rand.nextInt(16);
            case "ERROR":
                // 65 - 84 arası (YUKSEK RISK)
                return 65 + rand.nextInt(20);
            case "WARN":
                // 35 - 64 arası (DIKKAT)
                return 35 + rand.nextInt(30);
            default:
                // 0 - 34 arası (NORMAL)
                return rand.nextInt(35);
        }
    }

    @Override
    public boolean aksiyonGerekliMi() {
        // Nesne tabanlı kural: 65 ve üzeri puan (ERROR ve FATAL)
        // IDS tarafından otomatik engelleme/aksiyon gerektirir.
        return riskPuaniHesapla() >= 65;
    }


    // --- IJsonDonusturucu INTERFACE METOTLARI ---

    @Override
    public String toJson() {
        // Basit bir JSON formati olusturur
        return String.format("{ \"uygulama\": \"%s\", \"seviye\": \"%s\", \"mesaj\": \"%s\" }",
                uygulamaAdi, logSeviyesi, getMesaj());
    }

    @Override
    public void fromJson(String jsonVerisi) {
        System.out.println("JSON verisi islendi: " + jsonVerisi);
    }

    @Override
    public boolean dogrula() {
        // Uygulama adi bos olmamali
        return uygulamaAdi != null && !uygulamaAdi.isEmpty();
    }
}