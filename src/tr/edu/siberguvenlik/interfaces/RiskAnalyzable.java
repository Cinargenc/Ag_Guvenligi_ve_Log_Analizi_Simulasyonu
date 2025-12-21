package tr.edu.siberguvenlik.interfaces;

/**
 * Log verilerinin tehlike seviyesini analiz etmek ve
 * sisteme aksiyon aldirmak icin kullanilan arayuz.
 */
public interface RiskAnalyzable {

    // Logun icerdigi tehdide gore 0-100 arasi bir risk puani hesaplar.
    int riskPuaniHesapla();

    // Risk puanina gore okunabilir bir etiket (DUSUK, ORTA , YUKSEK RISK , KRITIK) dondurur.
    String riskSeviyesiGetir();

    // Sistemin bu loga karsi otomatik aksiyon (orn: IP engelleme) alip almayacagini belirler.
    boolean aksiyonGerekliMi();
}