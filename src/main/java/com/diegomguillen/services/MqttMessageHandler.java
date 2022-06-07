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
import org.eclipse.paho.client.mqttv3.MqttMessage;
////////////////////////////////////////////////////////////////////////////////
public class MqttMessageHandler extends Thread {

    private String topic;
    private MqttMessage message;

    //Contructor
    public MqttMessageHandler(String topic, MqttMessage message) {
        this.topic = topic;
        this.message = message;
    }
    //metodo a ejecutar cuando se lanza cada hilo.
    @Override
    public void run() {
        System.out.println("Thread:" + Thread.currentThread() + " Mqtt topic:" + topic + " Mqtt msg:" + message.toString());
    }
//END///////////////////////////////////////////////////////////////////////////
}
