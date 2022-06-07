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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
////////////////////////////////////////////////////////////////////////////////
@Entity //para usar jpa
@Table(name = "sniffers") //tal cual está en la base de datos
public class Sniffer{// implements Serializable {
    //private static final long serialVersionUID = 1L;
    //#ATRIBUTOS ///////////////////////////////////////////////////////////////
    @Id
    private Long id;
    //agregamos relación 
    @OneToMany(mappedBy="sniffer",fetch=FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<WifiDevice> listOfDevices;
    //
    @NotNull(message = "location cannot be null")
    @ManyToOne(fetch=FetchType.LAZY)
    private Location location;
    //
    @NotEmpty(message = "subsTopic, can´t be empty")
    @Column(columnDefinition = "varchar(30)")
    private String subsTopic;
    //
    @NotEmpty(message = "position, can´t be empty")
    @Column(columnDefinition = "varchar(50)")
    private String position;
    @Column(columnDefinition = "integer default 0")
    @Min(value = 0, message = "offset should not be less than 0")
    @Max(value = 3000, message = "offset should not be greater than 3000")
    private int sniffOffset;//offset para ajuste de cuenta de sniffed devs
    //#CONTRUCTORES ////////////////////////////////////////////////////////////
    //##DEFAULT
    public Sniffer(){
        this.listOfDevices = new ArrayList<>();
        
    }
    public Sniffer(Long id){
        this.id=id;  
    }
    //##OTROS
    public Sniffer(Long id, String subsTopic, String position,Location location) {
        this.id = id;
        this.position = position;
        this.subsTopic=subsTopic.toLowerCase();
        this.listOfDevices = new ArrayList<>();
        this.location = location;
    }
    public Sniffer(Long id, String subsTopic, String position) {
        this.id = id;
        this.position = position;
        this.subsTopic=subsTopic.toLowerCase();
        this.listOfDevices = new ArrayList<>();
    }
    //#OPERACIONES /////////////////////////////////////////////////////////////
    //##SETTERS & GETTERS
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<WifiDevice> getListOfDevices() {
        return listOfDevices;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public void setListOfDevices(List<WifiDevice> listOfDevices) {
        this.listOfDevices = listOfDevices;
    }
    public Boolean addWifiDev(WifiDevice dev) {
        Boolean res;
        dev.setSniffer(this);
        res = this.listOfDevices.add(dev);
        return res;
    }
    public Boolean removeWifiDev(WifiDevice dev){
        Boolean res;
        dev.setSniffer(null);
        res = this.listOfDevices.remove(dev);
        return res;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getSubsTopic() {
        return subsTopic;
    }
    public void setSubsTopic(String subsTopic) {
        this.subsTopic = subsTopic;
    }
    public int getSniffOffset() {
        return sniffOffset;
    }
    public void setSniffOffset(int sniffOffset) {
        this.sniffOffset = sniffOffset;
    }
//END //////////////////////////////////////////////////////////////////////////
}
