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
import com.diegomguillen.domain.WifiDevice;
import java.time.LocalDateTime;
import java.util.List;
////////////////////////////////////////////////////////////////////////////////
public interface IWifiDeviceService {
    public List<WifiDevice> findAllRequestInADay(LocalDateTime fecha, String snifferId);
    public List<Integer> getDevsPerHourInADay(LocalDateTime fecha, String snifferId,Boolean dot);
    //selecciones devs, dejarlo estár si no sale historia de usuario al respecto
    public List<Integer> getDevsDotsPerDayInAMonth(int year,int month, String snifferId,Boolean dot);
//END///////////////////////////////////////////////////////////////////////////    
}
