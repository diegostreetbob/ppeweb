package com.diegomguillen.test.repositories;

//import com.diegomguillen.domain.CellPhone;
import com.diegomguillen.domain.Location;
//import com.diegomguillen.domain.Persona;
import com.diegomguillen.domain.Sniffer;
import com.diegomguillen.domain.Vendor;
import com.diegomguillen.domain.WifiDevice;
//import com.diegomguillen.repositories.ICellPhoneRepository;
import com.diegomguillen.repositories.ILocationRepository;
import com.diegomguillen.repositories.ISnifferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import com.diegomguillen.repositories.IPersonaRepository;
import com.diegomguillen.repositories.IVendorRepository;
import com.diegomguillen.repositories.IWifiDeviceRepository;
import java.util.Optional;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.annotation.Rollback;
//import org.apache.commons.codec.*;
//import org.apache.commons.codec.digest.DigestUtils;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional //@todo colocar esta anotación en la capa de servicio para evitar el error How to fix Hibernate LazyInitializationException: failed to lazily initialize a collection of roles, could not initialize proxy - no Session
@Rollback(false)//Si no se pone deshace las transacciones
@SpringBootTest
class RepositoryTest {
    @Autowired
    private IWifiDeviceRepository devDao;
    @Autowired
    private ISnifferRepository snfDao;
    @Autowired 
    ILocationRepository locDao;
    @Autowired
    IVendorRepository venDao;
    //https://www.arquitecturajava.com/jpa-orphan-removal-y-como-usarlo/
    @PersistenceUnit
    EntityManagerFactory emf;
    //
    //@Test //Ejecutar solo una vez para poblar la base de datos
    void setup() {
        //Creación de localizaciones
        Location locMiCasa = new Location("38.02796329543113", "-1.052653533393647","Mi casa");
        Location locCasaPadres = new Location("38.02669688029615", "-1.05255392920669","Casa padres");
        locDao.save(locMiCasa);
        locDao.save(locCasaPadres);
        WifiDevice dev;
        Sniffer snf1,snf2,snf3;
        Long idSniffer1=Long.valueOf(1);
        Long idSniffer2=Long.valueOf(2);
        Long idSniffer3=Long.valueOf(3);
        //Creación de sniffers
        snf1 = new Sniffer(idSniffer1,"sniffer" ,"puerta principal",locMiCasa);
        snfDao.save(snf1);
        snf2 = new Sniffer(idSniffer2,"sniffer" ,"puerta trasera",locMiCasa);
        snfDao.save(snf2);
        snf3 = new Sniffer(idSniffer3,"sniffer" ,"puerta principal",locCasaPadres);
        snfDao.save(snf3); 
        // creación de wifidevices
        for(int i=1;i<6;i++){
            dev = new WifiDevice("FFFFF"+i, 85, false, false,snf1);
            devDao.save(dev);
          }
        for (int i = 6; i < 11; i++) {
            dev = new WifiDevice("EEEEE" + i, 56, false, false,snf2);
            devDao.save(dev);
        }
        for (int i = 11; i < 16; i++) {
            dev = new WifiDevice("EEEEE" + i, 33, false, false, snf3);
            devDao.save(dev);
        }
         //
    }
    @Test
    void createDevAndCheckHashedMac(){
        String hashedMacOfFFFFFF="824da7fe0d3a58cecfe9fde29e6cf5bec4383460b4cb7d6e61b9889e9a781763";
        Optional<Sniffer> sniffer2 = snfDao.findById(Long.valueOf(2));
        Vendor vendor=new Vendor("--","Marca sin definir");//en table vendors = Marca sin definir
        WifiDevice dev=new WifiDevice("FFFFFF", 85, false, false,sniffer2.get(),vendor,"aliasTest");
        WifiDevice createdDev=devDao.save(dev);
        assertTrue(createdDev.getMac().equalsIgnoreCase(hashedMacOfFFFFFF));
    }
    /**
     * El test consiste en:
     * Paso 2
     * --Actualizar string posición
     * Paso 3
     * --Actualizar avatarUri del dev1
     * Paso 4
     * --Eliminar dev4 del sniffer3
     * Paso 5
     * --Creamos dev4 de nuevo y lo guardamos en sniffer3
     */
    //@Test
    void test1(){
        Long idSniffer1=Long.valueOf(1);
        Long idSniffer2=Long.valueOf(2);
        Long idSniffer3=Long.valueOf(3);
        Long idDev1=Long.valueOf(1);
        Long idDev2=Long.valueOf(2);
        Long idDev3=Long.valueOf(3);
        Optional<Sniffer> s;
        Sniffer snf;
        //
        s=snfDao.findById(idSniffer2);
        snf=s.get();
        snf.setPosition("Stub posición");
        //#Paso 2
        snf.setPosition("pos actualizada");
        //traemos de nuevo de la tabla
        s=snfDao.findById(idSniffer2);
        snf=s.get();
        assertTrue(snf.getPosition().equals("pos actualizada"));
        snf.setPosition("puerta principal");
        //#Paso3
        snf.getListOfDevices().get(0).setAvatarUri("FFFFF1");
        //traemos de nuevo de la tabla
        s=snfDao.findById(idSniffer1);
        snf=s.get();
        assertTrue(snf.getListOfDevices().get(0).getAvatarUri().equals("241.jpg"));
        //#Paso 4
        long nDevs=devDao.count();
        s=snfDao.findById(idSniffer3);
        snf=s.get();
        snf.removeWifiDev(snf.getListOfDevices().get(4));
        assertTrue(devDao.count()==nDevs-1);
        //#Paso 5
        WifiDevice dev = new WifiDevice("EEEEE" + 15,  33, false, false, snf);
        snf.addWifiDev(dev);
        assertTrue(devDao.count()==nDevs);
        
    }
    /**
     * El test consiste en:
     * Paso 1:Contar todas las localizaciones
     * Paso 2:Añadir sniffer a localización
     * Paso 3:Hacemos una búsqueda por la descripción
     */
    //@Test
    void test2(){
        Long idSniffer3=Long.valueOf(4);
        Long idLoc2=Long.valueOf(2);
        Optional<Sniffer> s;
        Sniffer snf;
        Optional<Location> l;
        Location loc;
        //#Paso1
        assertTrue(locDao.count()==2);
        //#Paso2
        assertTrue(snfDao.count()==3);
        l=locDao.findById(idLoc2);
        loc=l.get();
        snf = new Sniffer(idSniffer3, "sniffer", "puerta trasera");
        loc.addSniffer(snf);
        assertTrue(snfDao.count()==4);
        loc.removeSniffer(snf);
        assertTrue(snfDao.count()==3);
        //#Paso3
        assertTrue(locDao.findBydescription("Mi casa").getDescription().equals("Mi casa"));
    }
   // @Test
    void test3(){
        Long idDev1=Long.valueOf(1);
        Optional<WifiDevice> dev=devDao.findById(idDev1);
        Optional<Vendor> ven =  venDao.findById("000002");
        dev.get().setVendor(ven.get());
        assertTrue(dev.get().getVendor().getName().equalsIgnoreCase("XEROX CORPORATION"));
    }
    //@Test
    void test4(){
        long nDevs=devDao.count();
        Optional<Sniffer> s=snfDao.findById(Long.valueOf(3));
        WifiDevice dev = new WifiDevice("11111F", 85, false, false,s.get());
        devDao.save(dev);
        assertTrue(devDao.count()==nDevs+1);
    }
    //#TEST
    //@Test
    void test6() {
        Sniffer snf = new Sniffer(Long.valueOf("2"));
        Vendor vendor = new Vendor("--","Marca sin definir");
        WifiDevice dev1=new WifiDevice("FFFFFF", 99, false, false,snf,vendor,"alias");
        WifiDevice dev2=new WifiDevice("AAAAAA", 99, false, false,snf,vendor,"alias");
        //WifiDevice dev=new WifiDevice("FFFFFF", 99, false, false,snf);
        WifiDevice resDev1=devDao.save(dev1);
        assertTrue(dev1.equals(resDev1));
        WifiDevice resDev2=devDao.save(dev2);
        assertTrue(dev2.equals(resDev2));
    }
 

}
