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
package com.diegomguillen.repositories;

import com.diegomguillen.domain.WifiDevice;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
@EnableScheduling
@Slf4j
@Repository
public class MqttMessageRepository implements IMqttMessageRepository{
    //#ATRIBUTOS ///////////////////////////////////////////////////////////////
    @Autowired//repositorio
    private IWifiDeviceRepository devDao;
    private HashMap<Long,HashMap<String,WifiDevice>> msgMap;
    private int count=0;
    private Boolean savingToBd=false;
    //#CONTRUCTORES ////////////////////////////////////////////////////////////
    //##DEFAULT
    public MqttMessageRepository(){
        msgMap = new HashMap<>();
    }
    //#OPERACIONES /////////////////////////////////////////////////////////////  
    //##OTRAS
    @Override
    public void saveMsg(WifiDevice dev) {
        //si no se está guardando en la bd proceder.
        if (savingToBd == false) {
            count++;
            long milliseconds = 0;//
            HashMap<String, WifiDevice> devMap = new HashMap<>();
            //extraemos snifferId
            Long snifferId = dev.getSniffer().getId();
            //buscamos sniffer en msgMap
            HashMap<String, WifiDevice> resMap = msgMap.get(snifferId);
            //existia?
            if (resMap == null) {//no existia
                devMap.put(dev.getMac(), dev);
                msgMap.put(snifferId, devMap);
            } else {//existia
                //buscamos dev en resMap 
                WifiDevice resDev = resMap.get(dev.getMac());
                //existia?
                if (resDev == null) {//no existia
                    resMap.put(dev.getMac(), dev);
                    msgMap.replace(snifferId, resMap);
                } else {//existía
                    //actualizamos presentime
                    milliseconds = (dev.getFoundDate().getTime() - resDev.getFoundDate().getTime());
                    resDev.setPresenceTime(((int) (milliseconds / 1000)));
                    //actualizamos rssi a máximo obtenido
                    if(dev.getRssi()>resDev.getRssi()){
                        resDev.setRssi(dev.getRssi());
                    }
                    //guardamos y actualizamos
                    resMap.replace(dev.getMac(), resDev);
                    //log.info("msgMap for sniffer {} resMap size {}",snifferId,resMap.size());
                    msgMap.replace(snifferId, resMap);
                }
            }
            printMsgMap();
            log.info("count {}", count);
        }
    }
    /**
     * Guarda en la base de datos, borra el map, la dejamos comentada
     * para que no guarde nada, descomentar a la hora de generar el .wer final
    */
    @Scheduled(cron = "${cron.expression}")
    public void saveToBd() {
        //se bloquea a saveMsg para que no use el hash map
        savingToBd=true;
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        log.info("Guardando en la bd a las {}", df.format(date));
        //pasamos del hashmap a un arraylist
        ArrayList<WifiDevice> devLis=new ArrayList<>();
        for (Long key : msgMap.keySet()) {
             msgMap.get(key).forEach((k, v) -> devLis.add(v));
        }
        //guardamos en la base de datos
        devDao.saveAll(devLis);
        //borramos el mapa local
        msgMap.clear();
        //se desbloquea a saveMsg para que use el hash map
        savingToBd=false;
        log.info("Fín guardado en la bd");
    }
    private void printMsgMap(){
        for(Long key:msgMap.keySet()){
            log.info("sniffer:{}",key);
            msgMap.get(key).forEach((k,v)->log.info("{}:{}:pt{}:{}:{}:{}:{}",v.getAlias(),k,v.getPresenceTime(),v.getRssi(),v.getIsfake(),v.getVendor().getName(),v.getFoundDate()));
        }
    }
    @Override
    public void clearBd(){
        msgMap.clear();
    }
    @Override
    public HashMap<Long,HashMap<String,WifiDevice>> getBd() {
        return this.msgMap;
    }
//END //////////////////////////////////////////////////////////////////////////    
}


