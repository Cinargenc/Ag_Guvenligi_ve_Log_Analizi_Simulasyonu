package tr.edu.siberguvenlik.interfaces;

/**
 * Sistemin genel saglik durumunu ve calisma periyodunu
 * denetlemek isteyen siniflar icin ortak arayuz.
 */
public interface ISistemTakibi {

    // Sistemin o anki durumunu (RAM, CPU, Baglanti vs.) kontrol eder.
    void sistemKontroluYap();
}