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

package com.diegomguillen.test.components;

import com.diegomguillen.components.RandomAliasReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RandomAliasReaderTest {
    int a=0;
    @Autowired
    RandomAliasReader rndReader;
    //RandomAliasReader rndReader = new RandomAliasReader(); 
    @Test
    void test1(){
        //tamaño de la lista cargada del csv
        Assertions.assertTrue(rndReader.getAliasFileSize()==5495);
        //buscamos alias para la mac 000000
        Assertions.assertTrue(rndReader.getAlias("000000").equals("Filipe"));
        //buscamos alias para la mac FFFFFF
        Assertions.assertTrue(rndReader.getAlias("FFFFFF").equals("Shawana"));
        log.info("alias{}",rndReader.getAlias("FFFFFF"));
    }
    @Test
    void test2(){
        int minSize=2;
        //desde 0 a 16777215+1 0=mac 000000 16777215=mac FFFFFF
        for(int i=0;i<16777216;i++){
            int size=rndReader.getAlias(i).length();
            if(size<minSize){
                minSize=size;
            }
        }
        Assertions.assertTrue(minSize==2);
    }
    @Test
    void test3() {
        Assertions.assertTrue(rndReader.lookForFile() == true);
    }
    

}
