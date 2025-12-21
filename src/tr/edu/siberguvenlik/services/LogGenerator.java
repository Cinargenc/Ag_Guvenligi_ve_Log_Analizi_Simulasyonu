package tr.edu.siberguvenlik.services;

import tr.edu.siberguvenlik.hierarchy1.LogEntry;
import java.util.concurrent.BlockingQueue;

/**
 * [PRODUCER - URETICI THREAD]
 * Bu sinifin gorevi, sonsuz bir dongu icinde LogFactory'den yeni loglar talep etmek
 * ve bu loglari islenmek uzere "logKuyrugu"na birakmaktir.
 */
public class LogGenerator implements Runnable {

    // Thread-Safe (Is parcacigi guvenli) Kuyruk
    // Veritabani veya ara bellek (Buffer) gorevi gorur.
    private final BlockingQueue<LogEntry> logKuyrugu;

    // Thread'i guvenli durdurmak icin 'volatile' bayrak
    private volatile boolean calisiyorMu = true;

    // Yapici Metot: Kuyrugu disaridan (Main'den) alir
    public LogGenerator(BlockingQueue<LogEntry> logKuyrugu) {
        this.logKuyrugu = logKuyrugu;
    }


    private String rastgeleIpUret() {
        java.util.Random rand = new java.util.Random();

        // %8 ihtimalle hatalı IP üret
        if (rand.nextInt(100) < 8) {
            return "192.168.1.257"; // Geçersiz IP
        }

        // %92 ihtimalle normal IP
        return "192.168.1." + rand.nextInt(255);
    }

    @Override
    public void run() {
        System.out.println(">> [GENERATOR] Log Uretici Thread Baslatildi...");
        while (calisiyorMu) {
            try {
                Thread.sleep(800 + new java.util.Random().nextInt(700));

                String uretilenIp = rastgeleIpUret();

                // [GONDERIM] uretilen IP fabrikaya parametre olarak gider
                LogEntry yeniLog = LogFactory.rastgeleLogUret(uretilenIp);

                if (yeniLog != null) {
                    logKuyrugu.offer(yeniLog);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Thread'i disaridan (Main sinifindan) durdurmak icin kullanilir
    public void durdur() {
        this.calisiyorMu = false;
    }
}