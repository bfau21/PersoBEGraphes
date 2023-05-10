package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.* ;
public class Label implements Comparable<Label>{
    protected final Node sommetCourant;
    protected boolean marque;
    protected double coutRealise;
    protected Arc father;

    public Label(Node sommetCourant, boolean marque, double coutRealise, Arc father)
    {
        this.sommetCourant = sommetCourant;
        this.marque = marque;
        this.coutRealise = coutRealise;
        this.father = father;
    }
    public Node getSommetCourant()
    {
        return this.sommetCourant;
    }
    public boolean getMarque()
    {
        return this.marque;
    }
    public double getCoutRealise()
    {
        return this.coutRealise;
    }
    public Arc getFather()
    {
        return this.father;
    }
    public double getCost()
    {
        return this.coutRealise;
    }
    public void setMarque(boolean marque)
    {
        this.marque = marque;
    }
    public void setCoutRealise(double coutRealise)
    {
        this.coutRealise = coutRealise;
    }
    public void setFather(Arc father)
    {
        this.father = father;
    }
    public int compareTo(Label lab)
    {
        if(this.coutRealise-lab.coutRealise<0)
        {
            return -1;
        }
        else if(this.coutRealise-lab.coutRealise>0)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
