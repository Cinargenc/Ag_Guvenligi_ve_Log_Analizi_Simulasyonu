package tr.edu.siberguvenlik.hierarchy1;

import tr.edu.siberguvenlik.interfaces.IJsonDonusturucu;
import tr.edu.siberguvenlik.interfaces.RiskAnalyzable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tum log turlerinin atasi olan soyut (abstract) sinif.
 * Temel ozellikleri (ID, IP, Mesaj) tutar ve gerekli Interface sozlesmelerini uygular.
 */
public abstract class LogEntry implements RiskAnalyzable, IJsonDonusturucu {

    // Multithread ortamda ID cakismasini onlemek icin AtomicLong kullandik.
    private static final AtomicLong sayac = new AtomicLong(0);

    // Tarih formati (Statik ve degismez)
    private static final DateTimeFormatter TARIH_FORMATI = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Temel Log Ozellikleri (Private - Encapsulation)
    private Long id;
    private String ipAdresi;
    private String mesaj;
    private LocalDateTime zamanDamgasi;
    private byte oncelikSeviyesi;

    // Puan tutarsızlığını bitirmek için mühürleme değişkeni
    private int sabitRiskPuani = -1;

    // Yapici Metot (Constructor)
    public LogEntry(String ipAdresi, String mesaj) {
        this.id = sayac.incrementAndGet(); // Thread-Safe (Guvenli) ID artirma
        this.ipAdresi = ipAdresi;
        this.mesaj = mesaj;
        this.zamanDamgasi = LocalDateTime.now();
        this.oncelikSeviyesi = 1; // Varsayilan dusuk oncelik
    }

    // Puanı bir kez hesaplayan ve hep aynı sonucu dönen metot
    public final int getHesaplanmisRiskPuani() {
        if (this.sabitRiskPuani == -1) {
            // Eğer puan henüz hesaplanmadıysa (ilk çağrı), hesapla ve sakla.
            this.sabitRiskPuani = riskPuaniHesapla();
        }
        return this.sabitRiskPuani;
    }

    // --- GETTER & SETTER METOTLARI ---

    public  String getId(){
        return this.id.toString();
    }

    public String getIpAdresi() {
        return ipAdresi;
    }

    public String getMesaj() {
        return mesaj;
    }

    public LocalDateTime getZamanDamgasi() {
        return zamanDamgasi;
    }

    public byte getOncelikSeviyesi() {
        return oncelikSeviyesi;
    }

    public void setOncelikSeviyesi(byte seviye) {
        // Hatali veri girisini engellemek icin kontrol (Validation)
        if (seviye < 0 || seviye > 10) {
            this.oncelikSeviyesi = 1;
        } else {
            this.oncelikSeviyesi = seviye;
        }
    }

    // --- SOYUT (ABSTRACT) METOTLAR ---
    // Alt siniflar (WebLog, SSHLog) bunlari kendine gore doldurmak zorundadir.

    public abstract void logDetaylariniYazdir();

    public abstract String logTipiniGetir();

    // --- RiskAnalyzable INTERFACE IMPLEMENTASYONU ---

    @Override
    public String riskSeviyesiGetir() {
        // [DÜZELTME]: riskPuaniHesapla() yerine getHesaplanmisRiskPuani() çağrılıyor.
        // Bu sayede etiket her zaman puanla uyumlu (Örn: 99 her zaman KRITIK) olur.
        int puan = getHesaplanmisRiskPuani();

        if (puan >= 85) return "KRITIK";       // %85 ve üzeri   (Kırmızı renksli kritik risk)
        if (puan >= 65) return "YUKSEK RISK"; // %65 - %84 arası (Kırmızı renksli yüksek risk)
        if (puan >= 35) return "DIKKAT";      // %35 - %64 arası (Sarı renkli orta risk)
        return "NORMAL";                      // %35 altı (Yeşil renkli normal risk)
    }

    @Override
    public boolean aksiyonGerekliMi() {
        //Tutarlılık için mühürlenmiş puan kullanılıyor.
        return getHesaplanmisRiskPuani() >= 65;
    }

    @Override
    public String toString() {
        String tarihStr = getZamanDamgasi().format(TARIH_FORMATI);
        return String.format("[%s] [ID:%s] [LVL:%s] [IP:%s] MSG: %s",
                tarihStr,
                getId(),
                getOncelikSeviyesi(),
                ipAdresi,
                mesaj);
    }
}