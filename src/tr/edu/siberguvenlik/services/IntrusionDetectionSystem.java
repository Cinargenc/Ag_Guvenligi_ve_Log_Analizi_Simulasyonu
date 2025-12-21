package tr.edu.siberguvenlik.services;

import tr.edu.siberguvenlik.exceptions.GecersizIpAdresiException;
import tr.edu.siberguvenlik.hierarchy1.LogEntry;
import tr.edu.siberguvenlik.hierarchy2.MudahaleAksiyonu;
import tr.edu.siberguvenlik.interfaces.ISistemTakibi;
import tr.edu.siberguvenlik.hierarchy2.GuvenlikAksiyonu;
import tr.edu.siberguvenlik.hierarchy2.IpEngellemeAksiyonu;
import tr.edu.siberguvenlik.hierarchy2.MailGondermeAksiyonu;
import tr.edu.siberguvenlik.utils.IstatistikYoneticisi;
import tr.edu.siberguvenlik.utils.LogDeposu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class IntrusionDetectionSystem implements Runnable, ISistemTakibi {

    private final BlockingQueue<LogEntry> logKuyrugu;
    private volatile boolean calisiyorMu = true;

    // --- KONSOL RENKLENDIRME ---
    private static final String RENK_SIFIRLA = "\u001B[0m";
    private static final String RENK_KIRMIZI = "\u001B[31m";
    private static final String RENK_YESIL = "\u001B[32m";
    private static final String RENK_SARI = "\u001B[33m";
    private static final String RENK_MOR = "\u001B[35m";

    public IntrusionDetectionSystem(BlockingQueue<LogEntry> logKuyrugu) {
        this.logKuyrugu = logKuyrugu;
    }

    @Override
    public void run() {
        System.out.println(">> [IDS] Saldırı Tespit Sistemi Devreye Girdi...");
        while (calisiyorMu || !logKuyrugu.isEmpty()) {
            try {
                LogEntry log = logKuyrugu.poll(1, TimeUnit.SECONDS);
                if (log != null) {
                    analizVeIslemYap(log);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.out.println(">> [IDS] GENEL HATA: " + e.getMessage());
            }
        }
    }

    private void analizVeIslemYap(LogEntry log) {
        try {
            // [1. ADIM: HATA YÖNETİMİ] IP doğrulaması (Hatalıysa catch bloğuna atar)
            ipFormatiniDogrula(log.getIpAdresi());

            // [2. ADIM: TUTARLILIK KİLİDİ]
            // DİKKAT: riskPuaniHesapla() yerine getHesaplanmisRiskPuani() kullanıyoruz.
            // Bu sayede puan bir kez hesaplanır ve seviye ile asla çelişmez.
            int puan = log.getHesaplanmisRiskPuani();
            String seviye = log.riskSeviyesiGetir();

            // [3. ADIM: ÇOK BİÇİMLİLİK] Kararı nesneye bırakıyoruz
            // Bu metot da içeride 'getHesaplanmisRiskPuani' kullandığı için kararı sabit puana göre verir.
            if (log.aksiyonGerekliMi()) {
                System.out.println(RENK_KIRMIZI + "\n╔════════ [TEHDİT TESPİT EDİLDİ: " + seviye + "] ════════" + RENK_SIFIRLA);
                System.out.println(RENK_KIRMIZI + "║ 📍 KAYNAK : " + log.getIpAdresi() + " | RISK PUANI: " + puan + RENK_SIFIRLA);
                System.out.println(RENK_KIRMIZI + "║ 📝 MESAJ  : " + log.getMesaj() + RENK_SIFIRLA);
                System.out.print(RENK_KIRMIZI + "║ 🔍 DETAY  : " + RENK_SIFIRLA);
                log.logDetaylariniYazdir();

                otomatikAksiyonAl(log, puan); // Sabitlenen puanı gönderiyoruz

                System.out.println(RENK_KIRMIZI + "╚" + "═".repeat(60) + RENK_SIFIRLA);
            } else if (seviye.equals("DIKKAT")) {
                System.out.println(RENK_SARI + "⚠️  [İNCELENİYOR] " + log.getIpAdresi() + " -> " + log.getMesaj() + RENK_SIFIRLA);
            } else {
                System.out.println(RENK_YESIL + "✅ [GÜVENLİ] " + log.logTipiniGetir() + " işlendi." + RENK_SIFIRLA);
            }

            // Başarılı logları kaydet
            LogDeposu.logEkle(log);
            IstatistikYoneticisi.istatistikGuncelle(log);

        } catch (GecersizIpAdresiException e) {
            // [4. ADIM: MOR ÇIKTI] Hatalı IP'yi reddet ve mor uyarını bas
            System.out.println(RENK_MOR + "⚠️ " + e.getMessage() + " [Kayıt Reddedildi]" + RENK_SIFIRLA);
            IstatistikYoneticisi.reddedilenLogArtir();
        }
    }

    private void otomatikAksiyonAl(LogEntry log, int puan) {
        List<GuvenlikAksiyonu> aksiyonListesi = new ArrayList<>();
        aksiyonListesi.add(new IpEngellemeAksiyonu(log.getIpAdresi()));

        if (puan >= 85) {
            aksiyonListesi.add(new MailGondermeAksiyonu(log.getIpAdresi(), log.getMesaj()));
        }

        for (GuvenlikAksiyonu aksiyon : aksiyonListesi) {
            System.out.print(RENK_KIRMIZI + "║ " + RENK_SIFIRLA);
            aksiyon.aksiyonuUygula();
            String etiket = (aksiyon instanceof MudahaleAksiyonu) ? "BLOK" : "MAIL";
            System.out.println("[" + etiket + ": BAŞARILI (ID:" + aksiyon.getAksiyonId() + ")]");
        }
    }

    private void ipFormatiniDogrula(String ip) throws GecersizIpAdresiException {
        String[] parcalar = ip.split("\\.");
        boolean hataVar = false;

        if (parcalar.length != 4) {
            hataVar = true;
        } else {
            try {
                for (String parca : parcalar) {
                    int d = Integer.parseInt(parca);
                    if (d < 0 || d > 255) { hataVar = true; break; }
                }
            } catch (NumberFormatException e) { hataVar = true; }
        }

        if (hataVar) {
            throw new GecersizIpAdresiException(ip);
        }
    }

    @Override
    public void sistemKontroluYap() {
        System.out.println("\n🏥 --- SİSTEM SAĞLIK KONTROLÜ (HEARTBEAT) ---");
        System.out.println("   🔹 Kuyruktaki Bekleyen Paket: " + logKuyrugu.size());
        System.out.println("   🔹 IDS Servis Durumu: " + (calisiyorMu ? "AKTIF" : "PASIF"));
    }

    public void durdur() { this.calisiyorMu = false; }
}