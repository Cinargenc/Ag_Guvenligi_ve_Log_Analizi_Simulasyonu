package tr.edu.siberguvenlik.utils;

import tr.edu.siberguvenlik.exceptions.GecersizIpAdresiException;
import tr.edu.siberguvenlik.exceptions.HataliLogTipiException;
import tr.edu.siberguvenlik.hierarchy1.LogEntry;
import tr.edu.siberguvenlik.hierarchy1.WebLog;
import tr.edu.siberguvenlik.interfaces.IJsonDonusturucu;

import java.util.List;

/**
 * [UTILITY CLASS] - VERİ İŞLEMCİSİ VE ANALİZ MERKEZİ
 * * Bu sınıf, siber güvenlik simülasyonu içindeki yardımcı araçları barındırır.
 * Matematiksel analizler, veri dönüşümleri (casting), string manipülasyonları
 * ve generic test yapıları bu merkez üzerinden yönetilir.
 * * @note Tüm metotlar static olarak tanımlanmıştır (Utility Pattern).
 */
public class VeriIslemcisi {

    // =========================================================================
    // 1. LOG NORMALİZASYONU VE METİN ANALİZİ (String İşlemleri)
    // =========================================================================
    public static void metinAnalizi(String hamVeri) {
        if (hamVeri == null) return;

        System.out.println("\n📝 --- LOG NORMALIZASYONU VE STRING ANALIZI ---");
        System.out.println("   (Ham veri temizleniyor ve normalize ediliyor...)");
        System.out.println("   🔹 Gelen Ham Veri: '" + hamVeri + "'");

        // Trim & Case Normalization
        String islenen = hamVeri.trim().toUpperCase();
        System.out.println("   🔹 Normalize Edildi: " + islenen);

        // Protokol İmzası Kontrolü (startsWith)
        if (islenen.startsWith("WEB") || islenen.startsWith("SSH")) {
            System.out.println("   -> [TESPIT] Protokol imzası başlangıçta bulundu.");
        }

        // Kritik Hata Tespiti (indexOf & substring)
        int hataIndex = islenen.indexOf("ERROR");
        if (hataIndex != -1) {
            System.out.println("   -> [KRITIK] Hata kodu konumu: " + hataIndex);
            String parca = islenen.substring(hataIndex);
            System.out.println("   -> [PARCA] Kesilen Hata Bloğu: " + parca);
        }

        // Format Düzenleme (replace)
        String temizlenmis = islenen.replace("-", " ");
        System.out.println("   -> [FORMAT] Okunabilirlik artırıldı: " + temizlenmis);

        // Karakter Doğrulama (charAt)
        if (!temizlenmis.isEmpty()) {
            System.out.println("   -> [CHECK] İlk Karakter Kontrolü: " + temizlenmis.charAt(0));
        }
    }

    // =========================================================================
    // 2. VERİ TÜRÜ DÖNÜŞÜMLERİ (Implicit & Explicit Casting)
    // =========================================================================
    public static void hesaplamaTesti() {
        System.out.println("\n📊 --- AG TRAFIK VE KAYNAK RAPORU (Casting Analizi) ---");

        int toplamPaket = 10000;
        int engellenenPaket = 2450;

        // Implicit Casting: int -> double dönüşümü
        double tehditOrani = (double) engellenenPaket / toplamPaket * 100;

        // Explicit Casting: double -> int (Veri kaybı göze alınarak)
        double sensorVerisi = 68.95;
        int cpuSicakligi = (int) sensorVerisi;

        System.out.println("   • Toplam Trafik : " + toplamPaket + " Paket");
        System.out.println("   • Tehdit Oranı  : %" + String.format("%.2f", tehditOrani));
        System.out.println("   • Donanım Isısı : " + cpuSicakligi + "°C (Sensör: " + sensorVerisi + ")");
    }

    // =========================================================================
    // 3. SALDIRI MATRİSİ VE DÖNGÜLER (Multi-dimensional Arrays)
    // =========================================================================
    public static void agSaldiriHaritasi() {
        System.out.println("\n🗺️  --- KRITIK AG BOLGELERI SALDIRI MATRISI ---");

        // Satırlar: VLAN Bölgeleri | Sütunlar: Farklı Sensör Verileri
        int[][] saldiriSayilari = {
                {5, 12, 3},   // VLAN-10 (Personel)
                {45, 60, 32}, // VLAN-20 (Misafir)
                {120, 95, 88} // VLAN-30 (DMZ)
        };

        String[] vlanIsimleri = {"VLAN-10 (Personel)", "VLAN-20 (Misafir) ", "VLAN-30 (DMZ-Web) "};

        // İç İçe Döngülerle Matris Analizi
        for (int i = 0; i < saldiriSayilari.length; i++) {
            System.out.print("   " + vlanIsimleri[i] + ": ");
            for (int j = 0; j < saldiriSayilari[i].length; j++) {
                int saldiri = saldiriSayilari[i][j];
                if (saldiri > 80) System.out.print("[🔥KRITIK:" + saldiri + "] ");
                else if (saldiri > 30) System.out.print("[⚠️ ORTA:" + saldiri + "] ");
                else System.out.print("[✅GUVENLI:" + saldiri + "] ");
            }
            System.out.println();
        }

        // Port Tarama Simülasyonu (Do-While)
        System.out.print("   >> Canlı Port Taraması Sürüyor (Nmap)");
        int sayac = 0;
        do {
            try { Thread.sleep(400); } catch (Exception e) {}
            System.out.print(".");
            sayac++;
        } while (sayac < 4);
        System.out.println(" [TARAMA TAMAMLANDI]\n");
    }

    // =========================================================================
    // 4. ZAMAN ANALİZİ VE PERFORMANS (Date/Time API)
    // =========================================================================
    public static void tarihIslemleri(LogEntry log) {
        System.out.println("\n⏱️ --- LOG GECIKME VE ZAMAN ANALIZI ---");

        java.time.LocalDateTime logZamani = log.getZamanDamgasi();
        java.time.LocalDateTime suAn = java.time.LocalDateTime.now();

        System.out.println("   🔹 Log Oluşturulma : " + logZamani);
        System.out.println("   🔹 İşlem Zamanı     : " + suAn);

        // Latency (Gecikme) Hesaplama
        long fark = java.time.temporal.ChronoUnit.MILLIS.between(logZamani, suAn);
        System.out.println("   ⚡ Sistem Gecikmesi (Latency): " + fark + " ms");

        if(fark > 1000) {
            System.out.println("   ⚠️ UYARI: İşlem sırasında darboğaz (bottleneck) tespit edildi!");
        } else {
            System.out.println("   ✅ PERFORMANS: Gerçek zamanlı veri işleme stabil.");
        }
    }

    // =========================================================================
    // 5. GENERIC YAPILAR (Generic Methods)
    // =========================================================================
    public static <T> void diziYazdir(String baslik, T[] dizi) {
        System.out.print("   🔹 " + baslik + ": ");
        for (T eleman : dizi) {
            System.out.print("[" + eleman + "] ");
        }
        System.out.println();
    }

    // =========================================================================
    // 6. JSON ENTEGRASYON TESTİ (Interface & Polymorphism)
    // =========================================================================
    public static void jsonVeriAktarimTesti() throws GecersizIpAdresiException {
        System.out.println("\n📡 --- SIEM VERI AKTARIM & API TESTI (JSON) ---");

        // Inbound Test
        IJsonDonusturucu gelenLog = new WebLog("0.0.0.0", "Dummy", "/", "NONE", 0);
        String siberIstihbarat = "{ \"kaynak_ip\": \"185.22.33.44\", \"tehdit\": \"Botnet_C2\" }";
        System.out.println("   [INBOUND] Dış kaynaklardan istihbarat alınıyor...");
        gelenLog.fromJson(siberIstihbarat);

        // Outbound Test
        IJsonDonusturucu gidenLog = new WebLog("10.0.0.5", "Şüpheli API", "/api/v1", "GET", 401);
        System.out.println("   [OUTBOUND] JSON Çıktısı: " + gidenLog.toJson());
    }

    // =========================================================================
    // 7. HATA YÖNETİMİ (Exception Handling)
    // =========================================================================
    public static void exceptionTesti() {
        System.out.println("\n💣 --- HATA YONETIMI TESTI (Custom Exception) ---");
        String[] senaryolar = {"WEB", "SSH", "UYGULAMA", "BILINMEYEN_TUR"};

        for (String tip : senaryolar) {
            try {
                logTipiniDogrula(tip);
                System.out.println("   ✅ Tip Onaylandı: " + tip);
            } catch (HataliLogTipiException e) {
                System.out.println("   ⛔ HATA YAKALANDI: " + e.getMessage());
            }
        }
    }

    private static void logTipiniDogrula(String tip) throws HataliLogTipiException {
        if (!(tip.equals("WEB") || tip.equals("SSH") || tip.equals("UYGULAMA") || tip.equals("FIREWALL"))) {
            throw new HataliLogTipiException("Geçersiz log tipi tanımlandı -> " + tip);
        }
    }

    // =========================================================================
    // 8. FIREWALL KURAL DOĞRULAMA (Encapsulation Test)
    // =========================================================================
    public static void firewallKuralTesti() {
        System.out.println("\n🔥 --- FIREWALL KURAL YONETIM SIMULASYONU ---");
        tr.edu.siberguvenlik.network.FirewallLog fwLog = new tr.edu.siberguvenlik.network.FirewallLog("10.20.30.40");

        try {
            fwLog.setHedefPort(443);
            System.out.println("   ✅ Port Güncellendi: 443 (HTTPS)");
        } catch (Exception e) {
            System.out.println("   ❌ Port Güncelleme Hatası: " + e.getMessage());
        }

        System.out.print("   🛡️ Bütünlük Kontrolü: ");
        System.out.println(fwLog.dogrula() ? "GEÇERLİ" : "GEÇERSİZ (Hatalı Konfigürasyon)");
        System.out.println("   🔹 Güncel Kural: " + fwLog.toString());
    }

    // =========================================================================
    // 9. DDOS VE VERİ BÜTÜNLÜĞÜ (Wildcards & Generics)
    // =========================================================================
    public static void ddosAnaliz(List<? extends Number> pingSureleri) {
        System.out.println("\n📶 --- AG GECIKME VE DDOS ANALIZI ---");
        if (pingSureleri.isEmpty()) return;

        double toplamGecikme = 0;
        for (Number sure : pingSureleri) toplamGecikme += sure.doubleValue();

        double ortalama = toplamGecikme / pingSureleri.size();
        System.out.println("   🔹 Ortalama Ping: " + String.format("%.2f", ortalama) + " ms");

        if (ortalama > 150) {
            System.out.println("   ⛔ ALARM: Ping kritik seviyede! Olası DDoS saldırısı.");
        } else {
            System.out.println("   ✅ DURUM: Ağ performansı stabil.");
        }
    }

    public static <T> void guvenlikHashKontrol(T istemciVerisi, T sunucuVerisi) {
        System.out.print("   🔒 Veri Bütünlüğü: ");
        if (istemciVerisi.equals(sunucuVerisi)) {
            System.out.println("BAŞARILI (Hashler Eşleşti)");
        } else {
            System.out.println("HATA (Hash Uyumsuzluğu!)");
        }
    }

    // =========================================================================
    // EXTRA: CONSTRUCTOR OVERLOADING TESTİ
    // =========================================================================
    public static void sshLogTesti() {
        System.out.println("\n🔑 --- SSH HIZLI GIRIS VE OVERLOADING TESTI ---");
        try {
            tr.edu.siberguvenlik.network.SSHLog hizliLog =
                    new tr.edu.siberguvenlik.network.SSHLog("192.168.1.99", "root_user");

            System.out.println("   ✅ Otomatik Log Oluştu: " + hizliLog.toString());
            System.out.println("   🔹 Mesaj: " + hizliLog.getMesaj());
        } catch (Exception e) {
            System.out.println("   ❌ Hata: " + e.getMessage());
        }
    }
}