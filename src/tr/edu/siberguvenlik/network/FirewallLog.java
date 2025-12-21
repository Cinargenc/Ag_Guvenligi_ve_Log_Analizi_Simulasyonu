package tr.edu.siberguvenlik.network;

import tr.edu.siberguvenlik.hierarchy1.LogEntry;

/**
 * Ag trafigini izleyen guvenlik duvari (Firewall) kayitlarini tutar.
 * Bellek tasarrufu icin Primitive (ilkel) tipler agirlikli kullanilmistir.
 */
public class FirewallLog extends LogEntry {

    // --- PRIMITIVE TIPLER (BELLEK DOSTU) ---
    private int hedefPort;       // Orn: 80, 443, 22
    private byte onemDerecesi;   // 1-10 arasi (byte -128 ile 127 arasi tutar)
    private short kuralId;       // Firewall kural numarasi (short 32bin'e kadar tutar)
    private char islemBayragi;   // 'E': Engellendi, 'I': Izin Verildi
    private float paketBoyutu;   // KB cinsinden boyut (orn: 12.5f)

    // Yapici Metot
    public FirewallLog(String ipAdresi, String mesaj, int hedefPort, boolean engellendiMi) {
        super(ipAdresi, mesaj); // ID ve Tarih atadan gelir

        this.hedefPort = hedefPort;
        // Ternary Operator: Engellendiyse 'E', degilse 'I'
        this.islemBayragi = engellendiMi ? 'E' : 'I';

        // Varsayilan degerler
        this.onemDerecesi = (byte) 5;
        this.kuralId = 101;
        this.paketBoyutu = 12.5f;
    }

    // Port numarasi icin guvenli Setter (0-65535 arasi olmali)
    public void setHedefPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("HATA: Gecersiz Port Numarasi -> " + port);
        }
        this.hedefPort = port;
    }

    // Constructor Overloading
    // Sadece IP verilirse varsayilan ayarlarla olustur
    public FirewallLog(String ipAdresi) {
        super(ipAdresi, "Otomatik Firewall Kurali");
        this.hedefPort = 80;   // Varsayilan
        this.islemBayragi = 'I'; // Izin verildi
        this.kuralId = 101;
        this.paketBoyutu = 0.0f;
    }

    // --- LOG ENTRY SOYUT METOTLARI ---

    @Override
    public void logDetaylariniYazdir() {
        String durum = (islemBayragi == 'E') ? "BLOKLANDI" : "IZIN VERILDI";
        System.out.println("FIREWALL [" + durum + "] Port: " + hedefPort + " | Boyut: " + paketBoyutu + "KB");
    }

    @Override
    public String logTipiniGetir() {
        return "FIREWALL_LOG";
    }

    @Override
    public int riskPuaniHesapla() {
        java.util.Random rand = new java.util.Random();

        // Eğer IP bloklanmışsa ('E') risk puanı dalgalı olsun
        if (islemBayragi == 'E') {
            if (hedefPort == 22 || hedefPort == 3389) {
                return 70 + rand.nextInt(21); // 70-91 arası (YUKSEK/KRITIK)
            }
            // Diğer portlar için 30-55 arası (DIKKAT/NORMAL)
            return 30 + rand.nextInt(26);
        }
        return 5 + rand.nextInt(20); // Normal trafik (5-25 arası)
    }



    @Override
    public boolean aksiyonGerekliMi() {
        // Firewall genelde pasif log tutar, aksiyonu IDS alir.
        // Ancak risk puani cok yuksekse (orn: kritik porta saldiri) true donebilir.
        return riskPuaniHesapla() > 80;
    }

    // --- JSON DONUSTURUCU METOTLARI ---

    @Override
    public String toJson() {
        return String.format("{ \"tip\": \"FW\", \"port\": %d, \"islem\": \"%c\", \"boyut\": %.2f }",
                hedefPort, islemBayragi, paketBoyutu);
    }

    @Override
    public void fromJson(String jsonVerisi) {
        System.out.println("Firewall JSON islendi: " + jsonVerisi);
    }

    @Override
    public boolean dogrula() {
        // Port numarasi gecerli aralikta mi?
        return hedefPort > 0 && hedefPort <= 65535;
    }
}