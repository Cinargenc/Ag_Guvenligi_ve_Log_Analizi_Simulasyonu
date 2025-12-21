package tr.edu.siberguvenlik.services;

import tr.edu.siberguvenlik.exceptions.GecersizIpAdresiException;
import tr.edu.siberguvenlik.exceptions.HataliLogTipiException;
import tr.edu.siberguvenlik.hierarchy1.*;
import tr.edu.siberguvenlik.network.FirewallLog;
import tr.edu.siberguvenlik.network.SSHLog;

import java.util.Random;

public class LogFactory {
    private static final Random RANDOM = new Random();

    public static LogEntry logUret(String logTipi, String ip) throws HataliLogTipiException, GecersizIpAdresiException {

        int zar = RANDOM.nextInt(100);

        switch (logTipi.toUpperCase()) {
            case "UYGULAMA":
                if (zar < 40) // %40 NORMAL
                    return new UygulamaLog(ip, "Servis Baslatildi", "AuthService", "INFO");
                else if (zar < 75) // %35 DIKKAT (SARI)
                    return new UygulamaLog(ip, "Veritabani Baglanti Gecikmesi", "DB_Module", "WARN");
                else {
                    // %25 YUKSEK/KRITIK RİSK için 2 farklı varyasyon
                    if (RANDOM.nextBoolean()) {
                        // Varyasyon 1: İşletim sistemi seviyesinde hata
                        return new UygulamaLog(ip, "Sistem Kaynaklari Tukendi", "Kernel", "ERROR");
                    } else {
                        // Varyasyon 2: Web sunucusu seviyesinde kritik sızma girişimi
                        return new UygulamaLog(ip, "Yetkisiz Dosya Erişimi Algılandı", "Web_Server", "CRITICAL");
                    }
                }
            case "WEB":
                if (zar < 40)
                    return new WebLog(ip, "Sayfa Yuklendi", "/home", "GET", 200);
                else if (zar < 75)
                    return new WebLog(ip, "Supheli Dizin Tarama", "/admin", "GET", 404);
                else
                    return new WebLog(ip, "SQL Injection Denemesi", "/login?id=1'", "POST", 500);
            case "SSH":
                if (zar < 50)
                    return new SSHLog(ip, "SSH Basarili Giris", "admin_user", true, 22);
                else if (zar < 60)
                    return new SSHLog(ip, "Yanlis Sifre Denemesi", "guest", false, 22);
                else if (zar < 85)
                    return new SSHLog(ip, "Brute Force Denemesi", "user123", false, 22);
                else
                    return new SSHLog(ip, "Root Yetkisi Zorlamasi", "root", false, 2222);
            case "FIREWALL":
                if (zar < 30)
                    return new FirewallLog(ip, "HTTP Izni", 80, false);
                else if (zar < 60)
                    return new FirewallLog(ip, "P2P Port Engelleme", 5555, true);
                else if (zar < 85)
                    return new FirewallLog(ip, "Kritik Port Taramasi", 445, true);
                else
                    return new FirewallLog(ip, "RDP Saldirisi Engellendi", 3389, true);
            default:
                return new WebLog(ip, "Genel Islem", "/", "GET", 200);
        }
    }

    // Dışarıdan 'ip' bekliyor
    public static LogEntry rastgeleLogUret(String ip) {
        try {
            String[] tipler = {"WEB", "SSH", "FIREWALL", "UYGULAMA"};
            return logUret(tipler[RANDOM.nextInt(tipler.length)], ip);
        } catch (Exception e) {
            return null;
        }
    }

}