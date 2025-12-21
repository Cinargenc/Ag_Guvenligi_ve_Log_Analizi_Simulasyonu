package tr.edu.siberguvenlik.hierarchy2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * [BASE CLASS - GÜVENLİK AKSİYONU HİYERARŞİSİ]
 * * Bu soyut sınıf, IDS (Saldırı Tespit Sistemi) tarafından riskli olarak
 * sınıflandırılan loglar için alınacak tüm güvenlik önlemlerinin temelini oluşturur.
 * * Temel Özellikler:
 * - Polymorphism (Çok Biçimlilik): 'aksiyonuUygula' metodu alt sınıflarda (IP Engelleme,
 * Mail Gönderme vb.) farklı şekillerde uygulanır.
 * - Takip: Her aksiyona benzersiz bir ID ve zaman damgası atanır.
 * - Durum Yönetimi: Aksiyonun aktiflik durumu (aktifMi) üzerinden sistem kontrolü sağlanır.
 * * @author Gazi Üniversitesi - Siber Güvenlik Projesi
 */
public abstract class GuvenlikAksiyonu {

    private int aksiyonId;
    private String tetikleyenIp;
    private LocalDateTime islemZamani;
    private boolean aktifMi;

    private static int sayac = 1;

    public GuvenlikAksiyonu(String tetikleyenIp) {
        this.aksiyonId = sayac++;
        this.tetikleyenIp = tetikleyenIp;
        this.islemZamani = LocalDateTime.now();
        this.aktifMi = true; // Baslangicta aksiyon aktiftir
    }

    // --- SOYUT METOT ---
    public abstract void aksiyonuUygula();

    // --- GETTER METOTLARI ---
    public int getAksiyonId() { return aksiyonId; }

    public String getTetikleyenIp() { return tetikleyenIp; }

    public LocalDateTime getIslemZamani() { return islemZamani; }

    public boolean isAktifMi() { return aktifMi; }


    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");

        return String.format("[AKS-ID:%d] [ZAMAN:%s] [DURUM:%s] Hedef:%s",
                getAksiyonId(),
                getIslemZamani().format(format),
                isAktifMi() ? "AKTIF" : "PASIF",
                getTetikleyenIp());
    }
}