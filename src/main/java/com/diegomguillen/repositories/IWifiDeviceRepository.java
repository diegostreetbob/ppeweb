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
////////////////////////////////////////////////////////////////////////////////
import com.diegomguillen.domain.Sniffer;
import com.diegomguillen.domain.WifiDevice;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
////////////////////////////////////////////////////////////////////////////////
@Repository
public interface IWifiDeviceRepository extends JpaRepository<WifiDevice, Long> {
    @Query("select d from WifiDevice d where d.foundDate between :startdate and :enddate")
    List<WifiDevice> findAllBetweenDates(@Param("startdate")Date startDate,@Param("enddate")Date endDate);
    @Query("select d from WifiDevice d where d.foundDate between :startdate and :enddate and d.sniffer = :sniffer_id")
    List<WifiDevice> findAllRequestInADay(@Param("startdate")Date startDate,@Param("enddate")Date endDate,@Param("sniffer_id")Sniffer snifferId);
    List<WifiDevice> findByFoundDateBetween(Date startDate,Date endDate);
//END///////////////////////////////////////////////////////////////////////////    
}
