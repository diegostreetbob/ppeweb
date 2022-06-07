/*
 * Copyright (C) 2022 DiegoMGuillén d761017@hotmail.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.diegomguillen.test.services;

import com.diegomguillen.domain.Sniffer;
import com.diegomguillen.domain.WifiDevice;
import com.diegomguillen.repositories.IWifiDeviceRepository;
import com.diegomguillen.services.IWifiDeviceService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import java.time.ZoneId;
import java.util.HashMap;

@Slf4j
@Transactional //@todo colocar esta anotación en la capa de servicio para evitar el error How to fix Hibernate LazyInitializationException: failed to lazily initialize a collection of roles, could not initialize proxy - no Session
@Rollback(false)//Si no se pone deshace las transacciones
@SpringBootTest
public class ServicesTest {
    @Autowired
    private IWifiDeviceRepository devDao;
    @Autowired
    private IWifiDeviceService devServ;

    @Test
    public void getDevAtDay0103Sniffer3() {
        LocalDateTime startDate = LocalDateTime.of(2022, Month.MARCH, 01, 00, 00, 00);
        List<WifiDevice> result = devServ.findAllRequestInADay(startDate, "3");
        assertEquals(4, result.size());
        //printDevList(result);
    }
    @Test
    public void getDevAtDay0303Sniffer3() {
        LocalDateTime startDate = LocalDateTime.of(2022, Month.MARCH, 03, 05, 00, 00);
        List<WifiDevice> result = devServ.findAllRequestInADay(startDate, "3");
        assertEquals(234, result.size());
        //printDevList(result);
    }
    
    
    @Test
    public void getDevFromId(){
        Optional<WifiDevice> optDev=devDao.findById(Long.parseLong("1"));
        assertTrue(optDev.get().getMac().equalsIgnoreCase("FFFFF1"));
    }
    
    public void printDevList(List<WifiDevice> devList){
        for(WifiDevice dev : devList){
            log.info("{}",dev.toString());
        }
    }
    
    @Test
    public void computeDevsInADay() {
        LocalDateTime fecha = LocalDateTime.of(2022, Month.MARCH, 04, 00, 00, 00);
        List<WifiDevice> allDevsList;
        allDevsList = devDao.findAllRequestInADay(Timestamp.valueOf(fecha), Timestamp.valueOf(fecha.plusDays(1)), new Sniffer(Long.parseLong("3")));
        assertEquals(661, allDevsList.size());
        ///////////////////////////////////////////////////////////////////////
        ///Comienzo del algoritmo
        //hora,devs
        HashMap<Integer, Integer> hourDevs = new HashMap<>();
        //Inicializamos
        for (int i = 0; i < 24; i++) {
            hourDevs.put(i, 0);
        }
        //
        List<WifiDevice> freq1noFake = new ArrayList<>();
        List<WifiDevice> freq1Fake = new ArrayList<>();
        List<WifiDevice> freqMayor1MenorIgual1Fake = new ArrayList<>();
        for (WifiDevice dev : allDevsList) {
            int freq = 0;
            for (WifiDevice dev_ : allDevsList) {
                if (dev.getMac().equals(dev_.getMac())) {
                    freq++;
                }
            }
            if (freq == 1 && dev.getIsfake() == false) {
                freq1noFake.add(dev);
            } else if (freq == 1 && dev.getIsfake() == true) {
                freq1Fake.add(dev);
            } else if (freq > 1 && freq <= 11 && dev.getIsfake() == true) {
                freqMayor1MenorIgual1Fake.add(dev);
            }
        }
        assertEquals(29, freq1noFake.size());
        assertEquals(272, freq1Fake.size());
        assertEquals(30, freqMayor1MenorIgual1Fake.size());
        System.out.println("dev " + freq1Fake.get(30).toString());
        int hora = getHour(freq1Fake.get(30));//
        System.out.println("hora " + hora);
        double ratioFreq1Fake = Double.valueOf("0.22");
        System.out.println("ratioFreq1Fake:" + ratioFreq1Fake);//0.22
        System.out.println("nDevsWithfreq1Fake:" + (int) (ratioFreq1Fake * freq1Fake.size()));//59
        System.out.println("nDevsWithfreq1Fake:" + (int) Math.ceil(ratioFreq1Fake * 1));//1
        System.out.println("nDevsWithfreq1Fake:" + (int) Math.ceil(ratioFreq1Fake * 11));//3
        //limpiamos frec1Fake
        HashMap<Integer, ArrayList<WifiDevice>> mapFreq = new HashMap<>();
        //Inicializamos
        for (int i = 0; i < 24; i++) {
            mapFreq.put(i, new ArrayList<WifiDevice>());
        }
        for (WifiDevice dev : freq1Fake) {
            ArrayList<WifiDevice> list;
            Integer hour = getHour(dev);
            list = mapFreq.get(hour);
            //añadimos el elemento
            list.add(dev);
            //actualizamos mapa
            mapFreq.put(hour, list);
        }
        assertEquals(3, mapFreq.get(0).size());
        assertEquals(9, mapFreq.get(10).size());
        assertEquals(4, mapFreq.get(23).size());
        //En este punto tenemos que multiplicar por factor 0.22, ver diagrama
        //de algoritmo,
        for(Integer key:mapFreq.keySet()){
            ArrayList<WifiDevice> list;
            //obtenemos array list
            list=mapFreq.get(key);
            //obtenemos factor
            int nDevs=(int) Math.ceil(list.size()*ratioFreq1Fake);
            int nDevsAnt = hourDevs.get(key);
            //sumamos a la cantidad existente
            hourDevs.put(key, nDevs+nDevsAnt);
        }
        assertEquals(4, hourDevs.get(11));
        //limpiamos freq1noFake
        mapFreq.clear();
        for (int i = 0; i < 24; i++) {
            mapFreq.put(i, new ArrayList<>());
        }
        for (WifiDevice dev : freq1noFake) {
            ArrayList<WifiDevice> list;
            Integer hour = getHour(dev);
            list = mapFreq.get(hour);
            //añadimos el elemento
            list.add(dev);
            //actualizamos mapa
            mapFreq.put(hour, list);
        }
        //actualizamos las cantidades
        for (Integer key : mapFreq.keySet()) {
            ArrayList<WifiDevice> list;
            //obtenemos array list
            list = mapFreq.get(key);
            //obtenemos factor
            int nDevs = list.size();
            int nDevsAnt = hourDevs.get(key);
            //sumamos a la cantidad existente
            hourDevs.put(key, nDevs+nDevsAnt);
        }
        assertEquals(6, hourDevs.get(11));
        //limpiamos freqMayor1MenorIgual1Fake
        mapFreq.clear();
        for (int i = 0; i < 24; i++) {
            mapFreq.put(i, new ArrayList<>());
        }
        for (WifiDevice dev : freqMayor1MenorIgual1Fake) {
            ArrayList<WifiDevice> list;
            Integer hour = getHour(dev);
            list = mapFreq.get(hour);
            //añadimos el elemento
            list.add(dev);
            //actualizamos mapa
            mapFreq.put(hour, list);
        }
        //recorremos cada hora quitando los repetidos
        //actualizamos las cantidades
        for (Integer key : mapFreq.keySet()) {
            ArrayList<WifiDevice> list;
            //obtenemos array list
            list = mapFreq.get(key);
            //quitamos los repetidos
            int nDevs = list.size();
            if(nDevs>0){
            for (int i=0;i<list.size();i++) {
                for (int j=i+1;j<list.size();j++) {
                    if (list.get(i).getMac().equals(list.get(j).getMac())) {
                        nDevs--;
                    }
                }
            }
            int nDevsAnt = hourDevs.get(key);
            //sumamos a la cantidad existente
            hourDevs.put(key, nDevs+nDevsAnt);
            }
        }
        assertEquals(8, hourDevs.get(11));

    }
    @Test
    void testGetDevIn0403Sniffer3(){
        LocalDateTime fecha = LocalDateTime.of(2022, Month.MARCH, 4, 00, 00, 00);
        List<Integer> res;
        res=devServ.getDevsPerHourInADay(fecha, "3",false);
        assertEquals(2, res.get(0));
        assertEquals(9, res.get(9));
        assertEquals(2, res.get(23));
    }
    private int getHour(WifiDevice dev) {
        int hour = 0;
        hour = dev.getFoundDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getHour();
        return hour;
    }
    @Test
    void getDevsPerDayInAMonth032022(){
    List<Integer> res;
    res=devServ.getDevsDotsPerDayInAMonth(2022, 3, "3",false);
    assertEquals(4,res.get(0));//comprobamos los del día 1/3
    }

//END
}


