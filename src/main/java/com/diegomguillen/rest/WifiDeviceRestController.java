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
package com.diegomguillen.rest;
////////////////////////////////////////////////////////////////////////////////
import com.diegomguillen.services.IWifiDeviceService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
////////////////////////////////////////////////////////////////////////////////
@RestController
@RequestMapping("/rest/")
public class WifiDeviceRestController {
    @Autowired
    private IWifiDeviceService devServ;
    
    @GetMapping("devsday/{year}/{month}/{day}/{snifferid}")
    public List<Integer> getDevsPerHourInADay(@PathVariable("year") int year,@PathVariable("month") int month,@PathVariable("day") int day,@PathVariable("snifferid") String snifferid){
        LocalDateTime fecha = LocalDateTime.of(year, month, day, 00, 00, 00);
        return devServ.getDevsPerHourInADay(fecha, snifferid,false);
    }
    @GetMapping("devsmonth/{year}/{month}/{snifferid}")
    public List<Integer> getDevsPerDayInAMonth(@PathVariable("year") int year,@PathVariable("month") int month,@PathVariable("snifferid") String snifferid){
        return devServ.getDevsDotsPerDayInAMonth(year, month, snifferid,false);
    }    
    @GetMapping("dotsday/{year}/{month}/{day}/{snifferid}")
    public List<Integer> getDotsPerHourInADay(@PathVariable("year") int year,@PathVariable("month") int month,@PathVariable("day") int day,@PathVariable("snifferid") String snifferid){
        LocalDateTime fecha = LocalDateTime.of(year, month, day, 00, 00, 00);
        return devServ.getDevsPerHourInADay(fecha, snifferid,true);
    }
    @GetMapping("dotsmonth/{year}/{month}/{snifferid}")
    public List<Integer> getDotsPerDayInAMonth(@PathVariable("year") int year,@PathVariable("month") int month,@PathVariable("snifferid") String snifferid){
        return devServ.getDevsDotsPerDayInAMonth(year, month, snifferid,true);
    }
//END //////////////////////////////////////////////////////////////////////////
}
