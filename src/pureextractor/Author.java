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
public class Author {
    
    private String pureId, scopusId, orcid, nombre, apellido, orgPureId, orgNombre, orgScopusId, orgPureId2, orgNombre2, orgScopusId2;
    //This is for an author with more than one scopus profile. It points to main Scopus Author ID
    private String scopusId0, rawScopusId;
    private ArrayList<Concept> fingerprint;

    public Author()
    {
        this.pureId = "";
        this.nombre = "";
        this.apellido = "";
        this.orcid = "";
        this.scopusId = "";
        this.orgPureId = "";
        this.orgNombre = "";
        this.orgScopusId = "";
        this.scopusId0 = "";
        this.rawScopusId = "";
        this.fingerprint = new ArrayList<>();
    }
    
    public Author(String pureId)
    {
        this.pureId = pureId;
        this.nombre = "";
        this.apellido = "";
        this.orcid = "";
        this.scopusId = "";
        this.orgPureId = "";
        this.orgNombre = "";
        this.orgScopusId = "";
        this.scopusId0 = "";
        this.rawScopusId = "";
        this.fingerprint = new ArrayList<>();
    }
    
    public boolean has2Org()
    {
        if (orgPureId2.isEmpty())
            return false;
        else
            return true;
    }

    public String getPureId()
    {
        return this.pureId;
    }

    public void setPureId(String pureId)
    {
        this.pureId = pureId;
    }

    public String getScopusId()
    {
        return this.scopusId;
    }

    public void setScopusId(String scopusId)
    {
        this.scopusId = scopusId;
    }

    public String getScopusId0()
    {
        return this.scopusId0;
    }

    public void setScopusId0(String scopusId0)
    {
        this.scopusId0 = scopusId0;
    }

    public String getRawScopusId()
    {
        return this.rawScopusId;
    }

    public void setRawScopusId(String rawScopusId)
    {
        this.rawScopusId = rawScopusId;
    }

    public String getNombre()
    {
        return this.nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getApellido()
    {
        return this.apellido;
    }

    public void setApellido(String apellido)
    {
        this.apellido = apellido;
    }

    public String getOrcid()
    {
        return this.orcid;
    }

    public void setOrcid(String orcid)
    {
        this.orcid = orcid;
    }

    public String getOrgPureId()
    {
        return this.orgPureId;
    }

    public void setOrgPureId(String orgPureId)
    {
        this.orgPureId = orgPureId;
    }

    public String getOrgNombre()
    {
        return this.orgNombre;
    }

    public void setOrgNombre(String orgNombre)
    {
        this.orgNombre = orgNombre;
    }

    public String getOrgScopusId()
    {
        return this.orgScopusId;
    }

    public void setOrgScopusId(String orgScopusId)
    {
        this.orgScopusId = orgScopusId;
    }

    public String getOrgPureId2()
    {
        return this.orgPureId2;
    }

    public void setOrgPureId2(String orgPureId2)
    {
        this.orgPureId2 = orgPureId2;
    }

    public String getOrgNombre2()
    {
        return this.orgNombre2;
    }

    public void setOrgNombre2(String orgNombre2)
    {
        this.orgNombre2 = orgNombre2;
    }

    public String getOrgScopusId2()
    {
        return this.orgScopusId2;
    }

    public void setOrgScopusId2(String orgScopusId2)
    {
        this.orgScopusId2 = orgScopusId2;
    }
    
    public void addConcept(Concept concepto)
    {
        this.fingerprint.add(concepto);
    }
    
    public int getNConcepts()
    {
        return this.fingerprint.size();
    }
    
    public void setConceptNames(int timeout, String baseURL, String apiVersion, String apiKey)
    {
        RequestConfig config = RequestConfig.custom()
          .setConnectTimeout(timeout * 1000)
          .setConnectionRequestTimeout(timeout * 1000)
          .setSocketTimeout(timeout * 1000).build();
        HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        //El nombre no viene y hay que pedir cada concepto
        for(Concept concepto : this.fingerprint)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(baseURL);
            sb.append("/ws/api/");
            sb.append(apiVersion);
            sb.append("/concepts/");
            sb.append(concepto.getUuid());
            System.out.println("Extracting Concept: " + concepto.getUuid());
            HttpGet request = new HttpGet(sb.toString());
            request.addHeader("Accept", "application/json");
            request.addHeader("api-key", apiKey);
            try 
            {  
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode()==200)
                {
                    String result = EntityUtils.toString(response.getEntity());
                    Object obj = new JSONParser().parse(result);
                    JSONObject jsonObj = (JSONObject)obj;
                    JSONArray ja = (JSONArray)jsonObj.get("terms");
                    Iterator itr = ja.iterator();
                    while(itr.hasNext())
                    {
                        JSONObject name = (JSONObject) itr.next();
                        concepto.setName(name.get("value").toString());
                    }
                    JSONObject thesauriObj = (JSONObject)jsonObj.get("thesauri");
                    JSONArray jaThesauri = (JSONArray)thesauriObj.get("names");
                    Iterator itrThesauri = jaThesauri.iterator();
                    while(itrThesauri.hasNext())
                    {
                        JSONObject nameT = (JSONObject) itrThesauri.next();
                        concepto.setThesauri(nameT.get("value").toString());
                    }
                }
            } catch (IOException ex) 
            {
                Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) 
            {
                Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public ArrayList<Concept> getFingerprint()
    {
        return this.fingerprint;
    }
}
