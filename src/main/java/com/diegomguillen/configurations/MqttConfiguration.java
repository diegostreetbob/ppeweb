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
package com.diegomguillen.configurations;
////////////////////////////////////////////////////////////////////////////////
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
////////////////////////////////////////////////////////////////////////////////
@Configuration
@PropertySource("classpath:mqttConfig.properties")
public class MqttConfiguration {
    //publisher
    //Valores de application.properties
    @Value("${mqtt.broker.publisher.connOpts.setCleanSession}")
    private boolean setCleanSession;
    @Value("${mqtt.broker.publisher.connOpts.setConnectionTimeout}")
    private int setConnectionTimeout;
    @Value("${mqtt.broker.publisher.connOpts.keepAliveInterval}")
    private int keepAliveInterval; 
    //subscriber
    //Valores de application.properties
    @Value("${mqtt.broker.subscriber.connOpts.setCleanSession}")
    private boolean SubsSetCleanSession;
    @Value("${mqtt.broker.subscriber.connOpts.setConnectionTimeout}")
    private int SubsSetConnectionTimeout;
    @Value("${mqtt.broker.subscriber.connOpts.keepAliveInterval}")
    private int SubsKeepAliveInterval; 
    @Value("${mqtt.saludo.arranque}")
    private String saludoArranque;
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    @Value("${mqtt.broker.port}")
    private int brokerPort;
    @Value("${mqtt.broker.subscriber.topic}")
    private String topic;
    @Value("${mqtt.broker.subscriber.clientId}")
    private String clientId;
    @Value("${mqtt.broker.subscriber.connOpts.setAutomaticReconnect}")
    private boolean setAutomaticReconnect;
    //Subscriber
    @Bean("mqttSubscriberConnectOptions")
    public MqttConnectOptions mqttSubscriberConnectOptions() {
        //Opciones de configuracón
        MqttConnectOptions conOpt= new MqttConnectOptions();
        conOpt.setCleanSession(SubsSetCleanSession);
        conOpt.setConnectionTimeout(SubsSetConnectionTimeout);
        conOpt.setKeepAliveInterval(SubsKeepAliveInterval);
        conOpt.setAutomaticReconnect(setAutomaticReconnect);
        return conOpt;
    }
    //Subscriber
    @Bean("mqttSubscriber")
    public IMqttClient mqttSubscriber() throws MqttException {
        //String rndID=""+ (int) (Math.random() * 100000000);
        MemoryPersistence persistence = new MemoryPersistence();
        IMqttClient mqttClient = new MqttClient("tcp://" + brokerUrl + ":" + brokerPort, clientId, persistence);
        return mqttClient;
    }
//END///////////////////////////////////////////////////////////////////////////
}
