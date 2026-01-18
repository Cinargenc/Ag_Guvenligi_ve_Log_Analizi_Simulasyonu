# Network Security & Log Analysis Simulation (Ağ Güvenliği ve Log Analizi Simülasyonu)


Bu proje, Java kullanılarak geliştirilmiş kapsamlı bir **Saldırı Tespit Sistemi (IDS)** simülasyonudur. Gerçek zamanlı log akışlarını analiz eder, potansiyel siber tehditleri (Brute-Force, Port Tarama vb.) tespit eder ve otomatik güvenlik aksiyonları alır.

## 🚀 Proje Hakkında

Bu simülasyon, modern bir Ağ Operasyon Merkezi'nin (NOC) çalışma mantığını modellemek amacıyla geliştirilmiştir. Sistem, **Producer-Consumer** tasarım desenini kullanarak log üretimini ve analizini asenkron olarak yönetir.

### Öne Çıkan Özellikler

* **Gerçek Zamanlı Tehdit Tespiti:** SSH Brute-Force, Web Saldırıları ve Port Taramalarını anlık olarak yakalar.
* **Otomatik Aksiyon Mekanizması:** Tespit edilen tehdide göre (IP engelleme, Hesap kilitleme, Servis yeniden başlatma) otomatik karar verir.
* **Multithreading Mimarisi:** `BlockingQueue` yapısı ile thread-safe veri akışı sağlar.
* **Nesne Yönelimli Tasarım (OOP):** `LogEntry` ve `GuvenlikAksiyonu` kalıtım zincirleri ile genişletilebilir bir yapı sunar.
* **Tasarım Desenleri:** Nesne üretimi için **Factory Pattern** kullanılmıştır.

## 🛠️ Teknik Detaylar

Proje aşağıdaki Java konseptlerini aktif olarak kullanır:
- **Concurrency:** Multithreading & Thread Synchronization
- **Data Structures:** Queue, List, Map
- **OOP Principles:** Inheritance, Polymorphism, Encapsulation, Abstraction
- **Custom Exceptions:** Özelleştirilmiş hata yönetimi
- **File I/O:** Raporlama ve loglama işlemleri

## 💻 Kurulum ve Çalıştırma

1. Projeyi klonlayın:
   ```bash
   git clone [https://github.com/Cinargenc/Ag_Guvenligi_ve_Log_Analizi_Simulasyonu.git](https://github.com/Cinargenc/Ag_Guvenligi_ve_Log_Analizi_Simulasyonu.git)

Favori IDE'niz ile (IntelliJ IDEA önerilir) projeyi açın.

Main.java dosyasını çalıştırın.

Konsol üzerinden simüle edilen log akışını ve alınan güvenlik önlemlerini izleyebilirsiniz.
