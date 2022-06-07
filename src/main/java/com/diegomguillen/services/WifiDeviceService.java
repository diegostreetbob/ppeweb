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
////////////////////////////////////////////////////////////////////////////////
package com.diegomguillen.services;
import com.diegomguillen.domain.Sniffer;
import com.diegomguillen.domain.WifiDevice;
import com.diegomguillen.repositories.IWifiDeviceRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
////////////////////////////////////////////////////////////////////////////////
@Slf4j
@Service
public class WifiDeviceService implements IWifiDeviceService {
    //#ATRIBUTOS ///////////////////////////////////////////////////////////////
    @Autowired
    private IWifiDeviceRepository devDao;
    
    private double ratioFreq1Fake = 0.20;
    //#OPERACIONES /////////////////////////////////////////////////////////////
    @Override
    public List<WifiDevice> findAllRequestInADay(LocalDateTime fecha, String snifferId) {
        List<WifiDevice> result; 
        result= devDao.findAllRequestInADay(Timestamp.valueOf(fecha), Timestamp.valueOf(fecha.plusDays(1)), new Sniffer(Long.parseLong(snifferId)));
        return result;
    }
    @Override
    public List<Integer> getDevsPerHourInADay(LocalDateTime fecha, String snifferId,Boolean dot) {
        List<WifiDevice> allDevsList;
        List<WifiDevice> freq1noFake = new ArrayList<>();
        List<WifiDevice> freq1Fake = new ArrayList<>();
        List<WifiDevice> freqMayor1MenorIgual1Fake = new ArrayList<>();
        ArrayList<Integer> listAlls= new ArrayList<>();
        HashMap<Integer, Integer> hourDevs = new HashMap<>();//<hora,devs>
        HashMap<Integer, ArrayList<WifiDevice>> mapFreq = new HashMap<>();
        //Traemos todos los datos
        allDevsList = devDao.findAllRequestInADay(Timestamp.valueOf(fecha), Timestamp.valueOf(fecha.plusDays(1)), new Sniffer(Long.parseLong(snifferId)));
        ////Comienzo del algoritmo,Inicializamos////////////////////////////////
        //Si queremos mostrar los dot, no aplicamos el ratio a los de freq==1 y
        //que son con mac fake
        if(Boolean.TRUE.equals(dot)) ratioFreq1Fake=1.0;
        else ratioFreq1Fake=0.20;
        //
        for (int i = 0; i < 24; i++) {
            hourDevs.put(i, 0);
        }
        //Separamos los datos según frecuencia de cada dispostivo
        for (WifiDevice dev : allDevsList) {
            int freq = 0;
            for (WifiDevice dev_ : allDevsList) {
                if (dev.getMac().equals(dev_.getMac())) {
                    freq++;
                }
            }
            if (freq == 1 && !dev.getIsfake()) {
                freq1noFake.add(dev);
            } else if (freq == 1 && dev.getIsfake()) {
                freq1Fake.add(dev);
            } else if (freq > 1 && freq <= 11 && dev.getIsfake()) {
                freqMayor1MenorIgual1Fake.add(dev);
            }
        }
        //Inicializamos, limpiamos frec1Fake
        mapFreq = cleanMap(mapFreq);
        for (WifiDevice dev : freq1Fake) {
            ArrayList<WifiDevice> list;
            Integer hour = getHour(dev);
            list = mapFreq.get(hour);
            //añadimos el elemento
            list.add(dev);
            //actualizamos mapa
            mapFreq.put(hour, list);
        }
        //En este punto tenemos que multiplicar por factor 0.22, ver diagrama
        //de algoritmo, y actualizar cantidades
        hourDevs = updateCounted(mapFreq, hourDevs, ratioFreq1Fake);
        //limpiamos freq1noFake
        mapFreq = cleanMap(mapFreq);
        mapFreq = cleanData(mapFreq, freq1noFake);
        //actualizamos las cantidades
        hourDevs = updateCounted(mapFreq, hourDevs, 1.0);
        //limpiamos freqMayor1MenorIgual1Fake
        mapFreq = cleanMap(mapFreq);
        mapFreq = cleanData(mapFreq, freqMayor1MenorIgual1Fake);
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
        //pasamos del hashMap al arrayList
        for (Map.Entry<Integer, Integer> entry : hourDevs.entrySet()) {
            listAlls.add(entry.getValue());
        }
        return listAlls;
    }
    @Override
    public List<Integer> getDevsDotsPerDayInAMonth(int year,int month, String snifferId,Boolean dot) {
        LocalDateTime fecha = LocalDateTime.of(year, month, 1, 00, 00, 00);
        LocalDateTime fechaStub;
        List<Integer> devs = new ArrayList<>();
        List<Integer> res;
        for(int i=1;i<=fecha.toLocalDate().lengthOfMonth();i++){
            fechaStub = LocalDateTime.of(year, month, i, 00, 00, 00);
            res=getDevsPerHourInADay(fechaStub, snifferId, dot);
            //contamos los elementos
            int num=0;
            for(int j=0;j<res.size();j++){
               num+=res.get(j);
            }
            devs.add(num);
            
        }
        return devs;
    }
    private int getHour(WifiDevice dev) {
        int hour = 0;
        hour = dev.getFoundDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getHour();
        return hour;
    }
    private HashMap<Integer, ArrayList<WifiDevice>> cleanMap(HashMap<Integer, ArrayList<WifiDevice>> map){
        map.clear();
        for (int i = 0; i < 24; i++) {
            map.put(i, new ArrayList<>());
        } 
        return map;
    }
    
    private HashMap<Integer, ArrayList<WifiDevice>> cleanData(HashMap<Integer, ArrayList<WifiDevice>> map, List<WifiDevice> devList){
        for (WifiDevice dev : devList) {
            ArrayList<WifiDevice> list;
            Integer hour = getHour(dev);
            list = map.get(hour);
            //añadimos el elemento
            list.add(dev);
            //actualizamos mapa
            map.put(hour, list);
        }
        return map;
    }
    private HashMap<Integer, Integer> updateCounted(HashMap<Integer, ArrayList<WifiDevice>> map,HashMap<Integer, Integer> hourDevs, Double ratio){
            for (Integer key : map.keySet()) {
            ArrayList<WifiDevice> list;
            //obtenemos array list
            list = map.get(key);
            int nDevs=(int) Math.ceil(list.size()*ratio);
            int nDevsAnt = hourDevs.get(key);
            //sumamos a la cantidad existente
            hourDevs.put(key, nDevs+nDevsAnt);
        }
            return hourDevs;
    }
//END //////////////////////////////////////////////////////////////////////////
}
