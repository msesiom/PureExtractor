/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pureextractor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 *
 * @author MORENOM1
 */
public class PureExtractor 
{    
    

    public static void main(String args[])
    {
        try 
        {
            Extract extractor = new Extract("https://harvard.pure.elsevier.com", "514", "f859302c-8e52-4ad8-ae44-bf6f459bc95e");
            extractor.getFingerprints();
            //Tiempo de ponerlo en formato
            System.out.println("Creating Excel File");
            Workbook wb0 = new XSSFWorkbook();
            wb0.createSheet("Fingerprints");
            Sheet sheet0 = wb0.getSheetAt(0);
            Authors autores = extractor.getAuthors();
            Cell cell;
            Row row;
            int cont = 0;
            //Crea filas necesarias
            for(int i=0; i<autores.getHigherNConcepts()+2; i++)
            {
                row = sheet0.createRow(i);
            }
            for(Author au : autores.getArray())
            {
                int contFilas = 2;
                row = sheet0.getRow(0);
                cell = row.createCell(cont);
                cell.setCellValue(au.getNombre() + " " + au.getApellido());
                row = sheet0.getRow(1);
                cell = row.createCell(cont);
                cell.setCellValue(au.getPureId());
                for(Concept concept : au.getFingerprint())
                {
                    row = sheet0.getRow(contFilas);
                    cell = row.createCell(cont);
                    cell.setCellValue(concept.getUuid() + "|" + concept.getName() + "|" + concept.getThesauri() + "|" + concept.getRank() + "|" + concept.getWRank());
                    contFilas++;
                }
                cont++;
            }
            
            try (OutputStream fileOut = new FileOutputStream("Fingerprints.xlsx")) {
                wb0.write(fileOut);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PureExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
            wb0.close();
            System.out.println("Process finished");
        } catch (IOException ex) {
            Logger.getLogger(PureExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
