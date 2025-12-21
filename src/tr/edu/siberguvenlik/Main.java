package tr.edu.siberguvenlik;

import tr.edu.siberguvenlik.exceptions.GecersizIpAdresiException;
import tr.edu.siberguvenlik.hierarchy1.LogEntry;
import tr.edu.siberguvenlik.hierarchy1.WebLog;
import tr.edu.siberguvenlik.services.*;
import tr.edu.siberguvenlik.utils.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * [ANA SINIF ]
 * * Gazi Üniversitesi Siber Güvenlik Simülasyonu'nun merkezi kontrol birimidir.
 * * Çok kanallı (Multi-threaded) yapıyı başlatır, yönetir ve sonlandırır.
 * * Kullanıcı arabirimi (Console UI) üzerinden tüm alt sistemlerin test edilmesini sağlar.
 * *
 * * @author Gazi Üniversitesi - Bilgisayar Mühendisliği
 */
public class Main {

    // =========================================================================
    // SİSTEM YAPILANDIRMASI VE KÜRESEL KAYNAKLAR
    // =========================================================================

    // Kullanıcı girişi için standart girdi tarayıcı
    private static final Scanner tarayici = new Scanner(System.in);

    // Benzersiz IP adreslerini tutan kara liste (Blacklist) - O(1) erişim hızı için HashSet seçildi
    private static final java.util.Set<String> yasakliIpListesi = new java.util.HashSet<>();

    // Üretici ve Tüketici thread'leri arasında güvenli veri iletimi sağlayan thread-safe kuyruk
    private static final BlockingQueue<LogEntry> logKuyrugu = new LinkedBlockingQueue<>(100);

    // Tüm işlenen logların saklandığı ana bellek deposu
    private static final LogDeposu<LogEntry> anaDepo = new LogDeposu<>();

    // Sistem servislerinin referansları
    private static LogGenerator ureticiServis;
    private static IntrusionDetectionSystem tespitServisi;
    private static CopToplayici copToplayiciServis;

    // İşlem birimleri (Threads)
    private static Thread threadUretici, threadTuketici, threadCopToplayici;

    // Verilerin kalıcı olarak saklanacağı dosya yolu (Platform bağımsız separator kullanımı)
    private static final String DOSYA_YOLU = System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "siber_guvenlik_loglari.txt";


    // =========================================================================
    // ANA DÖNGÜ (SİSTEM GİRİŞ NOKTASI)
    // =========================================================================
    public static void main(String[] args) throws GecersizIpAdresiException {
        boolean sistemAcik = true;

        System.out.println("################################################");
        System.out.println("###  GAZI UNIVERSITESI - AG GUVENLIGI VE LOG ANALIZI SIM  ###");
        System.out.println("################################################");
        System.out.println("📂 Kayit Dosyasi: " + DOSYA_YOLU);

        // [BAŞLANGIÇ] Varsayılan yasaklı IP adreslerinin sisteme yüklenmesi
        yasakliIpListesi.add("192.168.1.66");
        yasakliIpListesi.add("10.10.10.10");

        // Ana kullanıcı etkileşim döngüsü
        while (sistemAcik) {
            menuyuGoster();
            String secim = tarayici.nextLine();

            switch (secim) {
                case "1":
                    // Çok kanallı simülasyon akışını başlatır
                    simulasyonuBaslat();
                    break;
                case "2":
                    // Çalışan tüm threadleri güvenli bir şekilde durdurur
                    simulasyonuDurdur();
                    break;
                case "3":
                    // Kalıcı depolamadaki log kayıtlarını konsola döker
                    dosyadanLogOku();
                    break;
                case "4":
                    // VeriIslemcisi yardımcı sınıfındaki tüm analiz ve test fonksiyonlarını çalıştırır
                    System.out.println("\n🛠️ --- SISTEM ANALIZ TESTLERI BASLATILIYOR ---");

                    // 1. Matematiksel analiz ve ağ haritalama testi
                    VeriIslemcisi.hesaplamaTesti();
                    VeriIslemcisi.agSaldiriHaritasi();

                    // 2. Zamanlama ve gecikme (latency) analizi testi
                    WebLog testLog = new WebLog("192.168.1.100", "Admin Paneli Tarama", "/admin/login.php", "GET", 404);
                    System.out.println("   [TEST] Olusturulan Log Seviyesi: " + testLog.riskSeviyesiGetir());
                    VeriIslemcisi.tarihIslemleri(testLog);

                    // 3. Mimari gereksinim testleri (JSON, Exception, Polymorphism)
                    VeriIslemcisi.jsonVeriAktarimTesti();
                    VeriIslemcisi.exceptionTesti();
                    VeriIslemcisi.firewallKuralTesti();
                    VeriIslemcisi.sshLogTesti();

                    // 4. Metin madenciliği ve trafik analizi
                    String ornekBozukLog = "   web-server-connection-error-code-503   ";
                    VeriIslemcisi.metinAnalizi(ornekBozukLog);
                    VeriIslemcisi.ddosAnaliz(Arrays.asList(10, 20, 30.5, 40));

                    // 5. Port ve servis yapılandırma doğrulaması
                    System.out.println("\n⚙️  --- SISTEM YAPILANDIRMA VE BUTUNLUK KONTROLU ---");
                    String[] protokoller = {"SSH (Port 22)", "HTTPS (Port 443)", "SFTP (Secure)", "RDP (Disabled)"};
                    VeriIslemcisi.diziYazdir("Aktif Servis Listesi", protokoller);

                    // 6. Veri bütünlüğü (Hash) doğrulama testi
                    String orijinalHash = "5e884898da28047151d0e56f8dc62927";
                    VeriIslemcisi.guvenlikHashKontrol(orijinalHash, orijinalHash);
                    break;

                case "5":
                    // JVM Runtime üzerinden anlık bellek kullanımı ve doluluk analizi yapar
                    System.out.println("\n💾 --- SISTEM BELLEK (RAM) ANALIZI ---");
                    int logSayisi = anaDepo.depoBoyutu();
                    System.out.println("   📦 RAM'deki Log Nesneleri : " + logSayisi + " Adet");

                    Runtime rt = Runtime.getRuntime();
                    long gercekKullanilanMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
                    long sanalLogYukuMB = (logSayisi * 256) / 1024; // Nesne başına tahmini yük hesaplanır
                    long gosterilenKullanilan = gercekKullanilanMB + sanalLogYukuMB;
                    long toplamHafiza = rt.totalMemory() / (1024 * 1024);

                    System.out.println("   📊 Kullanilan RAM Miktari : " + gosterilenKullanilan + " MB");
                    System.out.println("   💾 Toplam Ayrilan Bellek  : " + toplamHafiza + " MB");

                    // Görsel bellek çubuğu (Progress Bar) oluşturma
                    int dolulukYuzdesi = (int) ((gosterilenKullanilan * 100) / toplamHafiza);
                    if (dolulukYuzdesi > 100) dolulukYuzdesi = 100;

                    System.out.print("   [");
                    for (int i = 0; i < 20; i++) {
                        if (i < (dolulukYuzdesi / 5)) System.out.print("#");
                        else System.out.print("-");
                    }
                    System.out.println("] %" + dolulukYuzdesi + " Dolu");

                    System.out.println("\n----------------------------------------");
                    // IDS servisinin sağlık durumunu kontrol eden Interface metodu
                    if (tespitServisi != null) {
                        tespitServisi.sistemKontroluYap();
                    } else {
                        System.out.println("   ⚠️ IDS Servisi henuz baslatilmadi.");
                    }
                    break;
                case "6":
                    // Bellekteki verileri fiziksel dosyaya yazar
                    dosyayaKaydet();
                    break;
                case "7":
                    // İstatistik yöneticisinden risk ve trafik raporlarını çeker
                    IstatistikYoneticisi.raporuGoster();
                    IstatistikYoneticisi.enTehlikeliIpleriListele();

                    System.out.println("\n🚫 --- KARA LISTE (Blacklist / HashSet) ---");
                    System.out.println("   Kayıtlı Yasaklı IP Sayısı: " + yasakliIpListesi.size());
                    System.out.println("   Liste Icerigi: " + yasakliIpListesi);
                    break;
                case "8":
                    // Sistemi güvenli kapatma prosedürü
                    simulasyonuDurdur();
                    System.out.println("🛑 Sistem kapatiliyor. Guvenli gunler dileriz.");
                    sistemAcik = false;
                    break;
                default:
                    System.out.println("⚠️ Gecersiz secim!");
            }
        }
        tarayici.close();
    }

    // =========================================================================
    // THREAD YÖNETİM VE SİMÜLASYON METOTLARI
    // =========================================================================

    /**
     * Producer-Consumer (Üretici-Tüketici) modeline dayalı simülasyonu başlatır.
     */
    private static void simulasyonuBaslat() {
        if (threadUretici != null && threadUretici.isAlive()) {
            System.out.println("⚠️ Sistem zaten arka planda calisiyor!");
            return;
        }

        // Servis nesnelerinin oluşturulması
        ureticiServis = new LogGenerator(logKuyrugu);
        tespitServisi = new IntrusionDetectionSystem(logKuyrugu);
        copToplayiciServis = new CopToplayici();

        // Runnable görevlerin Thread nesnelerine bağlanması
        threadUretici = new Thread(ureticiServis);
        threadTuketici = new Thread(tespitServisi);
        threadCopToplayici = new Thread(copToplayiciServis);

        // Arka plan temizlik görevinin işlemci önceliğini düşürme
        threadCopToplayici.setPriority(Thread.MIN_PRIORITY);

        // Thread'lerin yürütülmeye başlanması
        threadUretici.start();
        threadTuketici.start();
        threadCopToplayici.start();

        System.out.println("✅ Simulasyon BASLATILDI. Log akisi ve GC aktif...");
    }

    /**
     * Çalışan tüm thread'leri interrupt ederek ve join mekanizmasıyla bekleyerek sistemi kapatır.
     */
    private static void simulasyonuDurdur() {
        if (ureticiServis != null) {
            System.out.println("⏳ Sistem durduruluyor, lutfen bekleyin...");

            // Çalışma bayraklarını (volatile boolean) pasife çekme
            ureticiServis.durdur();
            tespitServisi.durdur();
            if (copToplayiciServis != null) copToplayiciServis.durdur();

            // Bekleyen thread'leri uyandırarak sonlandırma
            if (threadUretici != null) threadUretici.interrupt();
            if (threadTuketici != null) threadTuketici.interrupt();
            if (threadCopToplayici != null) threadCopToplayici.interrupt();

            try {
                // Thread'lerin tamamen kapanması için ana thread'i bekletme
                if (threadUretici != null) threadUretici.join(2000);
                if (threadTuketici != null) threadTuketici.join(2000);
                if (threadCopToplayici != null) threadCopToplayici.join(2000);
            } catch (InterruptedException e) {
                System.out.println("⚠️ Thread kapatilirken hata olustu.");
            }

            System.out.println("⏸️  Simulasyon tamamen DURDURULDU.");
            ureticiServis = null;
            tespitServisi = null;
            copToplayiciServis = null;
        } else {
            System.out.println("⚠️ Sistem zaten kapali.");
        }
    }

    // =========================================================================
    // DOSYA SİSTEMİ (I/O) OPERASYONLARI
    // =========================================================================

    /**
     * RAM'de biriken tüm log verilerini fiziksel bir metin dosyasına kalıcı olarak yazar.
     */
    private static void dosyayaKaydet() {
        System.out.println(">> Loglar diske yaziliyor...");
        List<LogEntry> loglar = LogDeposu.getLogListesi();
        if (loglar.isEmpty()) {
            System.out.println("⚠️ Kaydedilecek veri yok.");
            return;
        }
        // Try-with-resources kullanarak dosya akışını güvenli kapatma
        try (FileWriter yazar = new FileWriter(DOSYA_YOLU)) {
            for (LogEntry l : loglar) {
                yazar.write(l.toString() + "\n");
            }
            System.out.println("✅ BASARILI: " + loglar.size() + " log kaydedildi.");
        } catch (IOException e) {
            System.out.println("❌ Dosya Hatasi: " + e.getMessage());
        }
    }

    /**
     * Kaydedilen log dosyasını satır satır okuyarak konsol ekranına yazdırır.
     */
    private static void dosyadanLogOku() {
        try (Scanner okuyucu = new Scanner(new File(DOSYA_YOLU))) {
            System.out.println("\n📂 DOSYA ICERIGI OKUNUYOR...");
            while (okuyucu.hasNextLine()) {
                System.out.println(okuyucu.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("❌ Dosya bulunamadi.");
        }
    }

    /**
     * Kullanıcıya sunulacak etkileşim menüsünü ve sistem durumunu ekrana basar.
     */
    private static void menuyuGoster() {
        if (threadUretici != null && threadUretici.isAlive()) {
            System.out.println("\n🚀 SIMULASYON AKTIF - LOGLAR AKIYOR... (Durdurmak icin 2)");
            return;
        }
        System.out.println("\n--- 🛡️ SIBER GUVENLIK YONETIM PANELI 🛡️ ---");
        System.out.println("1. ▶️ Simulasyonu Baslat (Live)");
        System.out.println("2. ⏸️ Simulasyonu Durdur");
        System.out.println("3. 📂 Gecmis Loglari Dosyadan Oku");
        System.out.println("4. 🛠️ Sistem Testleri ve Analizler");
        System.out.println("5. 💾 Bellek (RAM) Durumu");
        System.out.println("6. 💾 Loglari Dosyaya Kaydet");
        System.out.println("7. 📊 Detayli Istatistik ve Risk Raporu");
        System.out.println("8. ❌ Cikis");
        System.out.print("👉 Seciminiz: ");
    }
}