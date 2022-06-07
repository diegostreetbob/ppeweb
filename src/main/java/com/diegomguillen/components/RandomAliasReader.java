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
package com.diegomguillen.components;
////////////////////////////////////////////////////////////////////////////////
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
////////////////////////////////////////////////////////////////////////////////
@Slf4j
@Component
public class RandomAliasReader {
    //#ATRIBUTOS ///////////////////////////////////////////////////////////////
    //TODO ver porque no se inyecta con @value del .properties
    //@Value("${randomNames.path}")
    //private String path;
    private ArrayList<String> alias;
    private int aliasFileSize=0;
    //#CONTRUCTORES ////////////////////////////////////////////////////////////
    public RandomAliasReader(){
       alias=loadAliasList();   
    }
    //#OPERACIONES /////////////////////////////////////////////////////////////
    public String getAlias(String mac){
        int aliasItem=Integer.parseInt(mac,16);//covertimos a int
        aliasItem=aliasItem%(aliasFileSize-1);//valores entre 0 y 5494
        return alias.get(aliasItem);
    }
    /**
     * Para usar en solo test, recorriendo toda la lista para verificar que no
     * nos salimos del rango
     * @param itemId
     * @return 
     */
    public String getAlias(int itemId) {
        return alias.get(itemId % (aliasFileSize - 1));
    }
    //##SETTERS & GETTERS
    public int getAliasFileSize() {
        return aliasFileSize;
    }
    //##OTRAS
    private ArrayList<String> loadAliasList(){
        ArrayList<String> aliasList=new ArrayList<>();
        try{
            InputStream inputStream = getClass().getResourceAsStream("/names.csv");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                CSVReader csvReader = new CSVReader(reader);
                String[] nextRecord;
                //guardamos los nombres en la lista, cambiamos la primera
                //letra a mayúsculas
                try {
                    while ((nextRecord = csvReader.readNext()) != null) {
                        String readedAlias = nextRecord[1].substring(0, 1).toUpperCase() + nextRecord[1].substring(1);
                        aliasList.add(readedAlias);
                    }
                } catch (CsvValidationException ex) {
                    Logger.getLogger(RandomAliasReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                csvReader.close();
            }        }catch(IOException e){
            Logger.getLogger(RandomAliasReader.class.getName()).log(Level.SEVERE, null, e);
        }
        aliasFileSize=aliasList.size();
        log.info("Alias list loaded with {} alias",aliasList.size());
        return aliasList;
    }    
    public Boolean lookForFile(){
        InputStream inputStream = getClass().getResourceAsStream("/names.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        return true;
    }    
//END //////////////////////////////////////////////////////////////////////
}
