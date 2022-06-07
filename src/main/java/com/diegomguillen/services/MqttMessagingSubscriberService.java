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
////////////////////////////////////////////////////////////////////////////////
import com.diegomguillen.components.RandomAliasReader;
import com.diegomguillen.domain.Sniffer;
import com.diegomguillen.domain.Vendor;
import com.diegomguillen.domain.WifiDevice;
import com.diegomguillen.repositories.IMqttMessageRepository;
import com.diegomguillen.repositories.ISnifferRepository;
import com.diegomguillen.repositories.IVendorRepository;
import com.diegomguillen.repositories.IWifiDeviceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
////////////////////////////////////////////////////////////////////////////////
@Slf4j
@Service
public class MqttMessagingSubscriberService implements MqttCallbackExtended {
    //Inyecciones de dependencias
    @Autowired//inyectado del bean en MqttConfiguration
    IMqttClient mqttSubscriber;
    @Autowired//inyectado del bean en MqttConfiguration
    MqttConnectOptions mqttSubscriberConnectOptions;
    @Autowired//repositorio
    private IWifiDeviceRepository devDao;
    @Autowired//repositorio
    private ISnifferRepository snfDao;
    @Autowired
    private IMqttMessageRepository mqttDao;
    @Autowired
    private IVendorRepository vendorDao;
    @Autowired
    RandomAliasReader rndAliasReader;
    //Atributos
    @Value("${mqtt.broker.subscriber.topic}")
    private String TOPIC;
    //
    @Value("${mqtt.saludo.arranque}")
    private String saludoArranque;
    //
    public static int counter;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        subscribe();
    }
    public void subscribe() {
        try {
            log.info(this.getClass().getSimpleName() + ":Conectando al broker");
            //Si no esta conectado, conectar
            if (!mqttSubscriber.isConnected()) {
                mqttSubscriber.connect(mqttSubscriberConnectOptions);
                mqttSubscriber.setCallback(this);
                mqttSubscriber.subscribe(TOPIC);
            } else {
                //Si esta conectado desconectar y conectar
                mqttSubscriber.disconnect();
                mqttSubscriber.connect(mqttSubscriberConnectOptions);
                log.info(this.getClass().getSimpleName() + ":Conexion realizada");
                mqttSubscriber.setCallback(this);
                mqttSubscriber.subscribe(TOPIC);
            }
        } catch (MqttException e) {
            exceptionLog(e);
        }
    }
    /**
     * Desconecta y cierra la conexión
     */
    public void subscriberStop() {
        /*
        Si no esta conectado la cierram en caso contratio desconecta y la cierra.
         */
        try {
            if (!mqttSubscriber.isConnected()) {
                mqttSubscriber.close();
            } else {
                //Si esta conectado desconectar y conectar
                mqttSubscriber.disconnect();
                mqttSubscriber.close();
            }
        } catch (MqttException ex) {
            Logger.getLogger(MqttMessagingSubscriberService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //
    private void publish(String topic) {
        MqttMessage message = new MqttMessage("Hello world from MQTT!".getBytes());
        message.setQos(0);
        try {
            this.mqttSubscriber.publish(topic, message);
        } catch (MqttException e) {
            exceptionLog(e);
        }
    }

    @Override
    public void connectComplete(boolean arg0, String arg1) {
        try {
            //Very important to resubcribe to the topic after the connection was (re-)estabslished. 
            //Otherwise you are reconnected but you don't get any message
            this.mqttSubscriber.subscribe(this.TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //Extracción de los datos
        List<String> msgTokensList = new ArrayList<>();
        StringTokenizer stSemicolon, stSlash;
        String mac, oui, uaa, stub, alias;//mac=oui+oua
        int rssi;
        Boolean isFake;
        Long snifferId;
        stSemicolon = new StringTokenizer(message.toString(), ";");
        stSlash = new StringTokenizer(topic, "sniffer/");
        while (stSlash.hasMoreTokens()) {
            msgTokensList.add(stSlash.nextToken());//pos 0 array
        }
        while (stSemicolon.hasMoreTokens()) {
            msgTokensList.add(stSemicolon.nextToken());//pos 1~5 array
        }
        snifferId = Long.parseLong(msgTokensList.get(0));
        oui = msgTokensList.get(1).substring(0, 6);
        uaa = msgTokensList.get(1).substring(6, 12);
        rssi = Integer.parseInt(msgTokensList.get(2));
        isFake = Integer.parseInt(msgTokensList.get(5)) == 0;
        log.info("###{}:{}:{}:{}:{}", snifferId, oui, uaa, rssi, isFake);
        Sniffer snf = new Sniffer(snifferId);
        WifiDevice devFound;
        Vendor vendorStub;
        Vendor vendor;
        //si es fake intercambiamos oui por uaa
        if (isFake) {
            vendorStub = new Vendor("---", "Marca ocultada");////en table vendors = Marca ocultada
            uaa = oui;
            alias = rndAliasReader.getAlias(uaa);
            devFound = new WifiDevice(uaa, rssi, isFake, false, snf, vendorStub, alias);
            log.info(">>>{}:{}:{}:{}:{}:{}:{}", devFound.getAlias(), snifferId, devFound.getMac(), rssi, isFake, vendorStub.getName(), devFound.getFoundDate());
            mqttDao.saveMsg(devFound);
        } else {
            vendorStub = new Vendor("--", "Marca sin definir");//en table vendors = Marca sin definir
            vendor = vendorDao.findById(oui).orElse(vendorStub);
            alias = rndAliasReader.getAlias(uaa);
            devFound = new WifiDevice(uaa, rssi, isFake, false, snf, vendor, alias);
            log.info(">>>{}:{}:{}:{}:{}:{}:{}", devFound.getAlias(), snifferId, devFound.getMac(), rssi, isFake, vendor.getName(), devFound.getFoundDate());
            mqttDao.saveMsg(devFound);
        }

    }

    private void exceptionLog(MqttException e) {
        log.error(this.getClass().getSimpleName() + e.getReasonCode());
        log.error(this.getClass().getSimpleName() + e.getMessage());
        log.error(this.getClass().getSimpleName() + e.getLocalizedMessage());
        log.error(this.getClass().getSimpleName() + e.getCause());
        log.error(this.getClass().getSimpleName() + e);
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error("{}:{}", this.getClass().getSimpleName(), cause);
    }
//END///////////////////////////////////////////////////////////////////////////
}
