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
package com.diegomguillen.domain;
////////////////////////////////////////////////////////////////////////////////
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.format.annotation.DateTimeFormat;
//Device es el dispostivo dectectado por el esp32, y corresponde al móvil de
//una persona o a cualquier otro equipo que disponga de wifi.
////////////////////////////////////////////////////////////////////////////////
@Entity
@Table(name = "wifidevices") //tal cual está en la base de datos
public class WifiDevice implements Serializable {
     private static final long serialVersionUID = 1L;
    //#ATRIBUTOS ///////////////////////////////////////////////////////////////
    //Agregamos mapeo de la llave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //
    @NotNull(message = "sniffer cannot be null")
    @ManyToOne(fetch=FetchType.LAZY)
    private Sniffer sniffer;
    //
    @NotNull(message = "mac cannot be null")
    @Column(columnDefinition = "varchar(64)")//long del hash sha256
    private String mac;//
    //
    @ManyToOne(optional=true,cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Vendor vendor;
    //
    @Column(columnDefinition = "varchar(50)")
    private String alias="--";//persona(simulado)a la que pertenece el disposit.
    //
    @Column(columnDefinition = "integer default 0")
    @Min(value = 0, message = "rssi should not be less than 0")
    @Max(value = 100, message = "rssi should not be greater than 100")
    private int rssi;     //señal del dispositivo
    //
    @NotNull(message = "isFake cannot be null")
    private Boolean isfake;//indica si la idMac es real o random
    //
    @NotNull(message = "isFixed cannot be null")
    @Column(columnDefinition = "boolean default false")
    private Boolean isFixed;//indica si es un dispositivo fijo del lugar
    //
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/YYYY HH:mm")
    private Date foundDate;
    //
    @Min(value = 0, message = "presenceTime should not be less than 0")
    @Max(value = 2147483647, message = "presenceTime max limit exceded")
    private int presenceTime=10;// como mínimo suponemos que se estará 10"
    //
    @Column(columnDefinition = "varchar(100)")
    private String avatarUri="--";//uri del del avatar
    //#CONTRUCTORES ////////////////////////////////////////////////////////////
    //##DEFAULT
    public WifiDevice() {
    }
    //##OTROS
    //Solo usado en test
    public WifiDevice(String mac, int rssi, Boolean isfake,Boolean isFixed, Sniffer sniffer) {
        this.mac = mac;
        this.rssi = rssi;
        this.isfake = isfake;
        this.isFixed=isFixed;
        this.sniffer=sniffer;
        this.foundDate= new Date();
        setAvatarUri(mac);
    }
    //El que se usa para guardar objetos en la bd
    public WifiDevice(String mac, int rssi, Boolean isfake,Boolean isFixed, Sniffer sniffer, Vendor vendor,String alias) {
        this.mac = DigestUtils.sha256Hex(mac);
        this.rssi = rssi;
        this.isfake = isfake;
        this.isFixed=isFixed;
        this.sniffer=sniffer;
        this.foundDate= new Date();
        this.vendor=vendor;
        this.alias=alias;
        setAvatarUri(mac);
    }    
    //#OPERACIONES /////////////////////////////////////////////////////////////
    //##SETTERS & GETTERS
    public Sniffer getSniffer() {
        return sniffer;
    }
    public void setSniffer(Sniffer sniffer) {
        this.sniffer = sniffer;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public Vendor getVendor() {
        return vendor;
    }
    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }   
    public int getRssi() {
        return rssi;
    }    
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    public Boolean getIsfake() {
        return isfake;
    }
    public void setIsfake(Boolean isfake) {
        this.isfake = isfake;
    }
    public Boolean getIsFixed() {
        return isFixed;
    }
    public void setIsFixed(Boolean isFixed) {
        this.isFixed = isFixed;
    }
    public Date getFoundDate() {
        return foundDate;
    }    
    public void setFoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }
    public int getPresenceTime() {
        return presenceTime;
    }
    public void setPresenceTime(int presenceTime) {
        this.presenceTime = presenceTime;
    }
    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String mac) {
        //Generación de la uri del avatar
        long avatarLong=Long.parseLong(mac,16);//covertimos a long
        avatarLong=avatarLong%255;//valores entre 0 y 255
        this.avatarUri=String.valueOf(avatarLong)+".jpg";
    }
    public Long getId() {
        return id;
    }
    @Override
    public String toString() {
        return "WifiDevice{" + "id=" + id + ", sniffer=" + sniffer + ", mac=" + mac + ", vendor=" + vendor + ", alias=" + alias + ", rssi=" + rssi + ", isfake=" + isfake + ", isFixed=" + isFixed + ", foundDate=" + foundDate + ", presenceTime=" + presenceTime + ", avatarUri=" + avatarUri + '}';
    }
//END //////////////////////////////////////////////////////////////////////////
}
