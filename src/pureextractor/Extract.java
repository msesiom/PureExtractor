/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pureextractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author morenom1
 */
public class Extract {
    
    private final int timeout = 10;
    private RequestConfig config;
    private String apiKey, apiVersion, baseURL;
    private Authors autores;
    
    public Extract(String baseURL, String apiVersion, String apiKey)
    {
        this.baseURL = baseURL;
        this.apiVersion = apiVersion;
        this.apiKey = apiKey;
        this.autores = new Authors(baseURL, apiVersion, apiKey);
        config = RequestConfig.custom()
          .setConnectTimeout(timeout * 1000)
          .setConnectionRequestTimeout(timeout * 1000)
          .setSocketTimeout(timeout * 1000).build();
    }
    
    public void getFingerprints()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL);
        sb.append("/ws/api/");
        sb.append(apiVersion);
        sb.append("/persons");
        HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        String nextURL = sb.toString();
        try 
        {         
            do
            {
                HttpGet request = new HttpGet(nextURL);
                request.addHeader("Accept", "application/json");
                request.addHeader("api-key", apiKey);
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode()==200)
                {
                    System.out.println("Extracting persons from Pure: ");
                    System.out.println(baseURL);
                    String result = EntityUtils.toString(response.getEntity());
                    Object obj = new JSONParser().parse(result);
                    JSONObject jsonObj = (JSONObject)obj;
                    JSONArray jsonSiguientes = (JSONArray) jsonObj.get("navigationLinks");
                    Iterator itrLinks = jsonSiguientes.iterator();
                    while(itrLinks.hasNext())
                    {
                        Map link = (Map) itrLinks.next();
                        String tipo = link.get("ref").toString();
                        if(tipo.equalsIgnoreCase("next"))
                        {
                            nextURL = link.get("href").toString();
                        }
                        else
                        {
                            nextURL = "";
                        }
                    }
                    JSONArray ja = (JSONArray)jsonObj.get("items");
    //                long contPersonas = (long)jsonObj.get("count");
                    Iterator itr = ja.iterator();
                    while(itr.hasNext())
                    {
                        Author autor = new Author();
                        Map entry = (Map) itr.next();
                        JSONObject name = (JSONObject)entry.get("name");
                        autor.setNombre(name.get("firstName").toString());
                        autor.setApellido(name.get("lastName").toString());
                        autor.setPureId(entry.get("pureId").toString());
                        if(entry.containsKey("ids"))
                        {
                            JSONArray ids = (JSONArray)entry.get("ids");
                            Iterator itrIds = ids.iterator();
                            while(itrIds.hasNext())
                            {
                                JSONObject id = (JSONObject)itrIds.next();
                                if(id.get("typeUri").toString().equalsIgnoreCase("/dk/atira/pure/person/personsources/scopusauthor"))
                                {
                                    autor.setScopusId(id.get("value").toString());
                                }
                            }
                        }
                        autores.add(autor);
                    }
                }
                
            }
            while(!nextURL.equalsIgnoreCase(""));
            //Todos los Autores, ahora ir por el Fingerprint
            System.out.println("Extracting Fingerprints...");
            for(Author au : autores.getArray())
            {
                sb = new StringBuilder();
                sb.append(baseURL);
                sb.append("/ws/api/");
                sb.append(apiVersion);
                sb.append("/persons/");
                sb.append(au.getPureId());
                System.out.println("Extracting person: " + au.getPureId());
                sb.append("/fingerprints");
                HttpGet request = new HttpGet(sb.toString());
                request.addHeader("Accept", "application/json");
                request.addHeader("api-key", apiKey);
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode()==200)
                {
                    String result = EntityUtils.toString(response.getEntity());
                    Object obj = new JSONParser().parse(result);
                    JSONObject jsonObj = (JSONObject)obj;
                    JSONArray ja = (JSONArray)jsonObj.get("items");
                    Iterator itr = ja.iterator();
                    while(itr.hasNext())
                    {
                        Map entry = (Map) itr.next();
                        JSONArray jsonConcepts = (JSONArray)entry.get("concepts");
                        Iterator itrConcepts = jsonConcepts.iterator();
                        while(itrConcepts.hasNext())
                        {
                            JSONObject concept = (JSONObject) itrConcepts.next();
                            Concept concepto = new Concept(concept.get("uuid").toString());
                            concepto.setRank(Double.valueOf(concept.get("rank").toString()));
                            concepto.setWRank(Double.valueOf(concept.get("weightedRank").toString()));
                            au.addConcept(concepto);
                        }
                    }
                    
                }
            }
            System.out.println("Extracting Concepts and Thesauri details...");
            autores.setConceptNames();
        } catch (IOException ex) 
        {
            Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) 
        {
            Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Authors getAuthors()
    {
        return this.autores;
    }
    
}
