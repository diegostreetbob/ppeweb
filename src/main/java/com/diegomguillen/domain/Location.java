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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
////////////////////////////////////////////////////////////////////////////////
@Entity //para usar jpa
@Table(name = "locations") //tal cual está en la base de datos
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    //#ATRIBUTOS ///////////////////////////////////////////////////////////////
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //agregamos relación 
    @OneToMany(mappedBy="location",fetch=FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Sniffer> listOfSniffers;
    //
    @NotEmpty(message = "latitude can´t be empty")
    @Column(columnDefinition = "varchar(20)")
    private String latitude;
    //
    @NotEmpty(message = "longitude can´t be empty")
    @Column(columnDefinition = "varchar(20)")
    private String longitude;
    //
    @NotEmpty(message = "description can´t be empty")
    @Column(columnDefinition = "varchar(40)")
    private String description;
    //#CONTRUCTORES ////////////////////////////////////////////////////////////
    //##DEFAULT
    public Location(){
        this.listOfSniffers = new ArrayList<>(); 
    }
    //##OTROS
    public Location(String latitude, String longitude, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.listOfSniffers = new ArrayList<>();
    }
    //#OPERACIONES /////////////////////////////////////////////////////////////
    //##SETTERS & GETTERS
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<Sniffer> getListOfSniffers() {
        return listOfSniffers;
    }
    public void setListOfSniffers(List<Sniffer> listOfSniffers) {
        this.listOfSniffers = listOfSniffers;
    }
    public Boolean addSniffer(Sniffer sniffer) {
        Boolean res;
        sniffer.setLocation(this);
        res = this.listOfSniffers.add(sniffer);
        return res;
    }
    public Boolean removeSniffer(Sniffer sniffer){
        Boolean res;
        sniffer.setLocation(null);
        res = this.listOfSniffers.remove(sniffer);
        return res;
    }  
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    //END //////////////////////////////////////////////////////////////////////
}
