package tr.edu.siberguvenlik.network;

import tr.edu.siberguvenlik.exceptions.GecersizIpAdresiException;
import tr.edu.siberguvenlik.hierarchy1.LogEntry;
import tr.edu.siberguvenlik.interfaces.ISistemTakibi;

/**
 * Sunuculara yapilan uzaktan baglanti (SSH) girisimlerini kayit altina alir.
 * Hem LogEntry ozelliklerini tasir hem de ISistemTakibi ile sunucu kontrolu yapar.
 */
public class SSHLog extends LogEntry implements ISistemTakibi {

    private String kullaniciAdi;
    private boolean basariliMi; // Giris basarili oldu mu?
    private int port;           // Genelde 22'dir ama degistirilebilir

    // --- CONSTRUCTOR 1: Tam Detayli ---
    public SSHLog(String ipAdresi, String mesaj, String kullaniciAdi, boolean basariliMi, int port) throws GecersizIpAdresiException {
        super(ipAdresi, mesaj);
        this.kullaniciAdi = kullaniciAdi;
        this.basariliMi = basariliMi;
        this.port = port;

        // Basarisiz giris denemeleri suphelidir, onceligi artir.
        if (!basariliMi) {
            setOncelikSeviyesi((byte) 7);
        }
        // "root" kullanicisina basarisiz giris denemesi cok kritiktir!
        if (!basariliMi && "root".equals(kullaniciAdi)) {
            setOncelikSeviyesi((byte) 10);
        }
    }

    // --- CONSTRUCTOR 2: Basit/Hizli Kullanim (Overloading) ---
    // Varsayilan olarak port 22 ve basarisiz giris kabul eder.
    public SSHLog(String ipAdresi, String kullaniciAdi) throws GecersizIpAdresiException {
        // Constructor Chaining: Kod tekrari yapmadan yukaridaki ana yapiciyi cagiriyoruz
        this(ipAdresi, "Otomatik SSH Denemesi", kullaniciAdi, false, 22);
    }

    // --- LOG ENTRY SOYUT METOTLARI ---

    @Override
    public void logDetaylariniYazdir() {
        String durum = basariliMi ? "BASARILI" : "BASARISIZ";
        System.out.println("SSH LOG [" + durum + "] Kullanici: " + kullaniciAdi + " | Port: " + port);
    }

    @Override
    public String logTipiniGetir() {
        return "SSH_LOG";
    }

    @Override
    public int riskPuaniHesapla() {
        java.util.Random rand = new java.util.Random();
        int puan = 0;

        if (!basariliMi) {
            // Standart başarısızlık: 40-60 arası (DIKKAT)
            puan = 40 + rand.nextInt(21);

            if (kullaniciAdi.equals("root")) {
                // Root zorlaması: Mevcut puana 30-40 daha ekle
                puan += (30 + rand.nextInt(11)); // Toplamda 70-100 arası (YUKSEK/KRITIK)
            }
        }
        return Math.min(puan, 100);
    }

    @Override
    public boolean aksiyonGerekliMi() {
        // Basarisiz root denemesi veya cok yuksek risk varsa aksiyon al
        return riskPuaniHesapla() >= 80;
    }

    // --- ISistemTakibi INTERFACE METODU ---
    @Override
    public void sistemKontroluYap() {
        System.out.println("SISTEM TAKIBI: SSH Servisi (" + getIpAdresi() + ":" + port + ") yanit veriyor.");
    }

    // --- IJsonDonusturucu INTERFACE METOTLARI ---

    @Override
    public String toJson() {
        return String.format("{ \"tip\": \"SSH\", \"user\": \"%s\", \"success\": %b, \"port\": %d }",
                kullaniciAdi, basariliMi, port);
    }

    @Override
    public void fromJson(String jsonVerisi) {
        System.out.println("SSH JSON verisi islendi: " + jsonVerisi);
    }

    @Override
    public boolean dogrula() {
        // Kullanici adi bos olamaz ve port mantikli bir aralikta olmali
        return kullaniciAdi != null && !kullaniciAdi.isEmpty() && port > 0;
    }
}