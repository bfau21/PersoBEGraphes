package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.* ;

public class LabelStar extends Label
{
    private double coutEstime;
    public LabelStar(Node sommetCourant, boolean marque, double coutRealise, Arc father, Node sommetDestination)
    {
        super(sommetCourant, marque, coutRealise,father);
        this.coutEstime = Point.distance(sommetCourant.getPoint(), sommetDestination.getPoint());
    }
    public double getTotalCost()
    {
        return this.coutRealise + this.coutEstime;
    }
}
