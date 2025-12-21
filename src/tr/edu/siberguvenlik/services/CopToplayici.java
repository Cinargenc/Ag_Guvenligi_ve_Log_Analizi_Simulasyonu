package tr.edu.siberguvenlik.services;

import tr.edu.siberguvenlik.utils.LogDeposu;

/**
 * Arka planda calisan ve belirli araliklarla
 * gereksiz bellek kullanimini temizleyen servis (Garbage Collector).
 */
public class CopToplayici implements Runnable {

    private volatile boolean calisiyorMu = true;

    @Override
    public void run() {
        System.out.println(">> Cop Toplayici (Garbage Collector) Servisi Baslatildi...");

        while (calisiyorMu) {
            try {
                // Her 15 saniyede bir temizlik yapsin
                Thread.sleep(15000);

                System.out.println("\n------------------------------------------------");
                System.out.println("♻️  GARBAGE COLLECTOR DEVREDE...");

                // 1. Yazilimsal temizlik (Listeyi bosalt)
                int silinenLogSayisi = LogDeposu.getLogListesi().size();
                if (silinenLogSayisi > 0) {
                    LogDeposu.depoyuTemizle();
                    System.out.println("   -> " + silinenLogSayisi + " adet eski log silindi.");
                } else {
                    System.out.println("   -> Temizlenecek veri yok, RAM stabil.");
                }

                // 2. JVM'e emir ver (System.gc)
                // Gercek hayatta bu cagirilsa bile JVM hemen calismayabilir
                // ama biz simulasyon yapiyoruz.
                System.gc();
                System.out.println("   -> JVM Bellek Optimizasyonu tetiklendi.");

                // Bos hafiza miktarini goster
                long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                System.out.println("   -> Guncel Bos Hafiza: " + freeMemory + " MB");
                System.out.println("------------------------------------------------\n");

            } catch (InterruptedException e) {
                // Thread durdurulurken buraya duser
                System.out.println(">> Cop Toplayici durduruldu.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void durdur() {
        this.calisiyorMu = false;
    }
}