package tr.edu.siberguvenlik.exceptions;

/**
 * Log verileri islenirken IP adresi formati (orn: 192.168.1.1)
 * standartlara uymuyorsa firlatilan ozel hata sinifi.
 */
public class GecersizIpAdresiException extends Exception {

    // Hatali gelen IP adresini alip ust sinifa (Exception) detayli mesaj iletir.
    public GecersizIpAdresiException(String hataliIp) {
        super("KRITIK HATA: Gecersiz IP formati tespit edildi! -> " + hataliIp);
    }
}