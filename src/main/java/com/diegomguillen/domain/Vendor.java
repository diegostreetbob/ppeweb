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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
////////////////////////////////////////////////////////////////////////////////
@Entity //para usar jpa
@Table(name = "vendors") //tal cual está en la base de datos
public class Vendor {
    //#ATRIBUTOS ///////////////////////////////////////////////////////////////
    @Id
    @Column(columnDefinition = "varchar(10)")
    private String id;
    @Column(columnDefinition = "varchar(120)")
    private String name;
    //#CONTRUCTORES ////////////////////////////////////////////////////////////
    //##DEFAULT
    public Vendor(){
        
    }
    //##OTROS
    public Vendor(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public Vendor(String id) {
        this.id = id;
    }
    //#OPERACIONES /////////////////////////////////////////////////////////////
    //##SETTERS & GETTERS
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
//END //////////////////////////////////////////////////////////////////////////
}
