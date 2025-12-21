package tr.edu.siberguvenlik.exceptions;

/**
 * Sistem tarafindan taninmayan veya desteklenmeyen bir log turu
 * islenmeye calisildiginda firlatilan ozel hata sinifi.
 */
public class HataliLogTipiException extends Exception {

    // Hata sebebini belirten mesaji alip Exception sinifina iletir.
    public HataliLogTipiException(String hataMesaji) {
        super(hataMesaji);
    }
}