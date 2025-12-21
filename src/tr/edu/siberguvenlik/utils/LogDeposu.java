package tr.edu.siberguvenlik.utils;

import tr.edu.siberguvenlik.hierarchy1.LogEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [GENERIC SINIF]
 * Tum loglarin RAM uzerinde tutuldugu merkezi veri deposudur.
 * Hem Thread-Safe (Guvenli) hem de Generic ozellikler tasir.
 *
 * @param <T> Depolanacak veri turu (Genelde LogEntry)
 */
public class LogDeposu<T> {

    // Thread-Safe (Eszamanli erisime uygun) Liste
    // 'static' oldugu icin tum uygulama boyunca tek bir ortak liste paylasilir.
    private static final List<LogEntry> KAYITLI_LOGLAR = Collections.synchronizedList(new ArrayList<>());

    // --- STATIC METOTLAR (SIMULASYON ERISIMI ICIN) ---

    // Yeni bir log kaydini ana listeye ekler
    public static void logEkle(LogEntry log) {
        KAYITLI_LOGLAR.add(log);
    }

    /**
     * Listenin guvenli bir kopyasini dondurur.
     * Neden Kopya? -> Biz okurken arkadan yeni log eklenirse hata almamak icin (Snapshot).
     */
    public static List<LogEntry> getLogListesi() {
        // Kopyalama islemi sirasinda listeyi kilitliyoruz (Critical Section)
        synchronized (KAYITLI_LOGLAR) {
            return new ArrayList<>(KAYITLI_LOGLAR);
        }
    }

    // --- INSTANCE METOTLAR ---
    // Main sinifinda: LogDeposu<LogEntry> anaDepo = ... denildigi yer icin.

    public int depoBoyutu() {
        return KAYITLI_LOGLAR.size();
    }

    // Generic temizleme metodu
    public static void depoyuTemizle() {
        if (!getLogListesi().isEmpty()) {
            getLogListesi().clear(); // Listeyi sifirla
            System.out.println("\t♻️ [RAM TEMIZLIGI] Log Deposu bosaltildi.");
        }
    }
}