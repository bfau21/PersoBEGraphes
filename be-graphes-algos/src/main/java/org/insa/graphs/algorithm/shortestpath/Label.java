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
    public double getTotalCost()
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
        if(this.getTotalCost()-lab.getTotalCost()<0)
        {
            return -1;
        }
        else if(this.getTotalCost()-lab.getTotalCost()>0)
        {
            return 1;
        }
        else
        {
            if (this.getTotalCost() == this.getCoutRealise())
            {
                return 0;
            }
            else
            {
                if (this.getCoutRealise() - lab.getCoutRealise() < 0)
                {
                    return 1;
                }
                else if (this.getCoutRealise() - lab.getCoutRealise() > 0)
                {
                    return -1;
                }
                else 
                {
                    return 0; //impossible car si coutR egaux alors totalCost differents. (mis pour eviter une erreur)
                }
            }
        }
    }
}
