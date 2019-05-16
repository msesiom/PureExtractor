/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pureextractor;

/**
 *
 * @author morenom1
 */
public class Concept {
    
    private String uuid, name, thesauri;
    private double rank, wRank;
    
    public Concept(String uuid)
    {
        this.uuid = uuid;
        this.name = "";
        this.rank = 0.0;
        this.wRank = 0.0;
    }
    
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
    
    public String getUuid()
    {
        return this.uuid;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setThesauri(String thesauri)
    {
        this.thesauri = thesauri;
    }
    
    public String getThesauri()
    {
        return this.thesauri;
    }
    
    public void setRank(double rank)
    {
        this.rank = rank;
    }
    
    public double getRank()
    {
        return this.rank;
    }
    
    public void setWRank(double wRank)
    {
        this.wRank = wRank;
    }
    
    public double getWRank()
    {
        return this.wRank;
    }
}
