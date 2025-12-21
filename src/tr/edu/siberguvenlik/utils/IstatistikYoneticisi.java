package tr.edu.siberguvenlik.utils;

import tr.edu.siberguvenlik.hierarchy1.LogEntry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IstatistikYoneticisi {

    // --- TEMEL SAYACLAR ---
    private static int toplamLogSayisi = 0;
    private static int sshLogSayisi = 0;
    private static int webLogSayisi = 0;
    private static int firewallLogSayisi = 0;
    private static int uygulamaLogSayisi = 0;

    private static int kritikRiskSayisi = 0;
    private static int reddedilenLogSayisi = 0;

    private static final LocalDateTime BASLANGIC_ZAMANI = LocalDateTime.now();

    // --- HAFIZA YAPILARI ---
    private static final Map<String, Integer> ipFrekansHaritasi = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Integer> ipRiskPuaniHaritasi = Collections.synchronizedMap(new HashMap<>());

    // --- COK BOYUTLU DIZI ---
    private static String[][] riskMatrisi = {
            {"DUSUK RISK ", "0"}, // Index 0
            {"ORTA RISK  ", "0"}, // Index 1
            {"YUKSEK RISK", "0"}, // Index 2
            {"KRITIK RISK", "0"}  // Index 3
    };

    //  Geçersiz IP hatası aldığımızda burayı çağırıyoruz
    public static synchronized void reddedilenLogArtir() {
        reddedilenLogSayisi++;
    }

    // LOG EKLEME VE ANALIZ METODU
    public static synchronized void istatistikGuncelle(LogEntry log) {
        toplamLogSayisi++;

        String tip = log.logTipiniGetir();

        if (tip.contains("SSH")) sshLogSayisi++;
        else if (tip.contains("WEB")) webLogSayisi++;
        else if (tip.contains("FIREWALL")) firewallLogSayisi++;
        else if (tip.contains("UYGULAMA")) uygulamaLogSayisi++;

        // Risk Analizi
        int riskPuani = log.riskPuaniHesapla();

        // Matris Indexleme Mantigi
        int matrisIndex = 0;

        if (riskPuani >= 85) {
            kritikRiskSayisi++;
            matrisIndex = 3; // KRITIK
        } else if (riskPuani >= 65) {
            matrisIndex = 2; // YUKSEK
        } else if (riskPuani >= 35) {
            matrisIndex = 1; // ORTA
        }

        // Matristeki ilgili sayacı arttır
        int mevcutSayi = Integer.parseInt(riskMatrisi[matrisIndex][1]);
        riskMatrisi[matrisIndex][1] = String.valueOf(mevcutSayi + 1);

        String ip = log.getIpAdresi();
        ipFrekansHaritasi.put(ip, ipFrekansHaritasi.getOrDefault(ip, 0) + 1);

        // --- IP bazlı toplam risk puanını 100 ile sınırla ---
        int eskiToplamPuan = ipRiskPuaniHaritasi.getOrDefault(ip, 0);

        // Math.min(deger, 100) kullanarak toplamın 100'ü geçmesini engelliyoruz.
        int yeniToplamPuan = Math.min(eskiToplamPuan + riskPuani, 100);

        ipRiskPuaniHaritasi.put(ip, yeniToplamPuan);
    }

    // --- RAPORLAMA METOTLARI ---

    public static void raporuGoster() {
        System.out.println("\n📊 --- GENEL SISTEM DURUM RAPORU --- 📊");

        Duration calismaSuresi = Duration.between(BASLANGIC_ZAMANI, LocalDateTime.now());
        System.out.println("⏱️  Sistem Acik Kalma Suresi: " +
                calismaSuresi.toMinutes() + " dk " + (calismaSuresi.getSeconds() % 60) + " sn");

        System.out.println("----------------------------------------");
        System.out.println("Toplam Islenen Log : " + toplamLogSayisi);
        System.out.println("SSH Girisimleri    : " + sshLogSayisi);
        System.out.println("Web Istekleri      : " + webLogSayisi);
        System.out.println("Uygulama Loglari   : " + uygulamaLogSayisi);
        System.out.println("Firewall Bloklari  : " + firewallLogSayisi);
        System.out.println("----------------------------------------");
        System.out.println("🔴 Tespit Edilen Kritik Olay: " + kritikRiskSayisi);

        System.out.println("\n--- RISK DAGILIM MATRISI ---");
        for (int i = 0; i < riskMatrisi.length; i++) {
            System.out.println("| " + riskMatrisi[i][0] + " | Adet: " + riskMatrisi[i][1] + " |");
        }
    }

    public static void enTehlikeliIpleriListele() {
        System.out.println("\n💀 --- EN TEHLIKELI 5 SALDIRGAN (TOP 5) --- 💀");
        System.out.println(String.format("%-18s %-12s %-15s", "IP ADRESI", "RISK PUANI", "TEHDIT SEVIYESI"));
        System.out.println("-----------------------------------------------------");

        if (ipRiskPuaniHaritasi.isEmpty()) {
            System.out.println(">> Henuz yeterli veri toplanmadi.");
            return;
        }

        ipRiskPuaniHaritasi.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(kayit -> {
                    String ip = kayit.getKey();
                    int puan = kayit.getValue();

                    //Yukaridaki matris mantigiyla ayni esikler
                    String seviye;
                    if (puan >= 85) {
                        seviye = "KRITIK (APT)";
                    } else if (puan >= 65) {
                        seviye = "YUKSEK";
                    } else if (puan >= 35) {
                        seviye = "ORTA";
                    } else {
                        seviye = "DUSUK";
                    }

                    System.out.println(String.format("%-18s %-12d %-15s", ip, puan, seviye));
                });
        System.out.println("-----------------------------------------------------");
    }
}