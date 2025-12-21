package tr.edu.siberguvenlik.interfaces;

/**
 * Nesnelerin JSON formatina donusturulmesi ve
 * veri butunlugunun dogrulanmasi icin gereken standart yapiyi belirler.
 */
public interface IJsonDonusturucu {

    // Nesnenin o anki durumunu JSON formatinda String olarak dondurur.
    String toJson();

    // Verilen JSON formatindaki metni alip nesne ozelliklerine isler.
    void fromJson(String jsonVerisi);

    // Verinin gecerli ve butunluklu olup olmadigini kontrol eder.
    boolean dogrula();
}