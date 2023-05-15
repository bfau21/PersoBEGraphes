package org.insa.graphs.algorithm.shortestpath;

import java.lang.Math;
import java.util.*;
import org.insa.graphs.model.*;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;

public class Marathon extends ShortestPathAlgorithm
{
    public Marathon(ShortestPathData data)
    {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun()
    {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution1 = null;
        

        Node origine = data.getOrigin();
        float distanceParcourue = 0;
        float distance = 42195;
        Node sommetCourant = origine;
        ArrayList<Node> sommetsParcourus = new ArrayList<Node>();
        sommetsParcourus.add(origine);
        Path pathDijkstra1 = null;
        Path pathDijkstra3 = null;
        boolean doitRentrer = false;
        while (distanceParcourue != distance)
        {
            if (!doitRentrer)
            {
                //trouver tous les points de l'isochrone à 2km
                //INITIALISATION
                int nombreSommets = data.getGraph().getNodes().size();
                BinaryHeap<Label> tas = new BinaryHeap<Label>();
                ArrayList<Node> sommetsIsochrones = new ArrayList<>();
                ArrayList<Label> Labels = new ArrayList<Label>();
                for (int i = 0; i < nombreSommets; i++)
                {
                    Labels.add(new Label(data.getGraph().getNodes().get(i), false, Double.POSITIVE_INFINITY, null));
                }
                Node nodeOrigin = data.getOrigin();
                int idOrigin = nodeOrigin.getId();
                Labels.get(idOrigin).setCoutRealise(0);
                tas.insert(Labels.get(idOrigin));
                notifyOriginProcessed(nodeOrigin);
                //ITERATIONS
                int idDestination = data.getDestination().getId();
                Label labelDestination = Labels.get(idDestination);
                Label labelX = tas.findMin();
                while (!labelDestination.getMarque() && !tas.isEmpty() && labelX.getCoutRealise() < 2)
                {
                    labelX = tas.findMin();
                    labelX.setMarque(true);
                    notifyNodeMarked(labelX.getSommetCourant());
                    tas.remove(labelX);
                    sommetsIsochrones.add(labelX.getSommetCourant());
                    for (int i = 0; i < labelX.getSommetCourant().getNumberOfSuccessors(); i++)
                    {
                        if(data.isAllowed(labelX.getSommetCourant().getSuccessors().get(i))){
                            int idLabelY = labelX.getSommetCourant().getSuccessors().get(i).getDestination().getId();
                            Label labelY = Labels.get(idLabelY);
                            float longXY = labelX.getSommetCourant().getSuccessors().get(i).getLength();
                            //float timeXY = labelX.getSommetCourant().getSuccessors().get(i).getTravelTime();
                            if (!labelY.getMarque())
                            {
                                boolean remove = true;
                                if (labelY == labelDestination)
                                {
                                    notifyDestinationReached(data.getDestination());
                                }
                                if (labelY.getCoutRealise() == Double.POSITIVE_INFINITY)
                                {
                                    remove = false;
                                    notifyNodeReached(labelY.getSommetCourant());
                                }
                                if (labelY.getCoutRealise() > labelX.getCoutRealise() + longXY)
                                {
                                    if (remove)
                                    {
                                        tas.remove(labelY);
                                        labelY.setCoutRealise(labelX.getCoutRealise() + longXY);
                                        labelY.setFather(labelX.getSommetCourant().getSuccessors().get(i));
                                        tas.insert(labelY);
                                        sommetsIsochrones.add(labelY.getSommetCourant());
                                    }
                                    else
                                    {
                                        labelY.setCoutRealise(labelX.getCoutRealise() + longXY);
                                        labelY.setFather(labelX.getSommetCourant().getSuccessors().get(i));
                                        tas.insert(labelY);
                                    }
                                }
                            }
                        }
                    }
                }
                boolean doitRefaire = true;
                while (doitRefaire)
                {
                    int nbAleatoire = (int) Math.random()*(sommetsIsochrones.size()-1);
                    Node sommetChoisi = sommetsIsochrones.get(nbAleatoire);
                    //Disjktra entre les 2 sommets
                    ShortestPathData dataDijkstra1 = new ShortestPathData(data.getGraph(), sommetCourant, sommetChoisi, ArcInspectorFactory.getAllFilters().get(0));
                    DijkstraAlgorithm Dijkstra1 = new DijkstraAlgorithm(dataDijkstra1);
                    pathDijkstra1 = Dijkstra1.run().getPath();
                    float distanceDijkstra1 = Dijkstra1.run().getPath().getLength();
                    if (distanceDijkstra1 < 4)
                    {
                        distanceParcourue += distanceDijkstra1;
                        sommetsParcourus.add(sommetChoisi);
                        sommetCourant = sommetChoisi;
                        //faire Dijkstra2
                        ShortestPathData dataDijkstra2 = new ShortestPathData(data.getGraph(), sommetChoisi, origine, ArcInspectorFactory.getAllFilters().get(0));
                        DijkstraAlgorithm Dijkstra2 = new DijkstraAlgorithm(dataDijkstra2);
                        float distanceDijkstra2 = Dijkstra2.run().getPath().getLength();
                        if (distanceDijkstra2 > (distance - distanceParcourue)-0.5 && distanceDijkstra2 < (42195 - distanceParcourue)+0.5)
                        {
                            doitRentrer = true;
                        }
                        doitRefaire = false;
                    }
                }
            }
            else
            {
                //faire Dijkstra3 de sommetCourant à origine et s'arreter quand dist = 42195

                //INITIALISATION
                int nombreSommetsD3 = data.getGraph().getNodes().size();
                BinaryHeap<Label> tasD3 = new BinaryHeap<Label>();
                ArrayList<Node> sommetsD3 = new ArrayList<>();
                ArrayList<Label> LabelsD3 = new ArrayList<Label>();
                for (int i = 0; i < nombreSommetsD3; i++)
                {
                    LabelsD3.add(new Label(data.getGraph().getNodes().get(i), false, Double.POSITIVE_INFINITY, null));
                }
                Node nodeOriginD3 = data.getOrigin();
                int idOriginD3 = nodeOriginD3.getId();
                LabelsD3.get(idOriginD3).setCoutRealise(0);
                tasD3.insert(LabelsD3.get(idOriginD3));
                notifyOriginProcessed(nodeOriginD3);
                //ITERATIONS
                int idDestinationD3 = data.getDestination().getId();
                Label labelDestinationD3 = LabelsD3.get(idDestinationD3);
                Label labelXD3 = tasD3.findMin();
                while (!labelDestinationD3.getMarque() && !tasD3.isEmpty() && labelXD3.getCoutRealise() != distance - distanceParcourue)
                {
                    labelXD3 = tasD3.findMin();
                    labelXD3.setMarque(true);
                    notifyNodeMarked(labelXD3.getSommetCourant());
                    tasD3.remove(labelXD3);
                    sommetsD3.add(labelXD3.getSommetCourant());
                    for (int i = 0; i < labelXD3.getSommetCourant().getNumberOfSuccessors(); i++)
                    {
                        if(data.isAllowed(labelXD3.getSommetCourant().getSuccessors().get(i))){
                            int idLabelYD3 = labelXD3.getSommetCourant().getSuccessors().get(i).getDestination().getId();
                            Label labelYD3 = LabelsD3.get(idLabelYD3);
                            float longXYD3 = labelXD3.getSommetCourant().getSuccessors().get(i).getLength();
                            //float timeXY = labelX.getSommetCourant().getSuccessors().get(i).getTravelTime();
                            if (!labelYD3.getMarque())
                            {
                                boolean removeD3 = true;
                                if (labelYD3 == labelDestinationD3)
                                {
                                    notifyDestinationReached(data.getDestination());
                                }
                                if (labelYD3.getCoutRealise() == Double.POSITIVE_INFINITY)
                                {
                                    removeD3 = false;
                                    notifyNodeReached(labelYD3.getSommetCourant());
                                }
                                if (labelYD3.getCoutRealise() > labelXD3.getCoutRealise() + longXYD3)
                                {
                                    if (removeD3)
                                    {
                                        tasD3.remove(labelYD3);
                                        labelYD3.setCoutRealise(labelXD3.getCoutRealise() + longXYD3);
                                        labelYD3.setFather(labelXD3.getSommetCourant().getSuccessors().get(i));
                                        tasD3.insert(labelYD3);
                                        sommetsD3.add(labelYD3.getSommetCourant());
                                    }
                                    else
                                    {
                                        labelYD3.setCoutRealise(labelXD3.getCoutRealise() + longXYD3);
                                        labelYD3.setFather(labelXD3.getSommetCourant().getSuccessors().get(i));
                                        tasD3.insert(labelYD3);
                                    }
                                }
                            }
                        }
                    }
                }
                Node sommetDestination = sommetsD3.get(sommetsD3.size()-1);
                ArrayList<Arc> arcsD3 = new ArrayList<>();
                Arc arcD3 = LabelsD3.get(sommetDestination.getId()).getFather();
                while (arcD3 != null) {
                    arcsD3.add(arcD3);
                    arcD3 = LabelsD3.get(arcD3.getOrigin().getId()).getFather();
                    }
                pathDijkstra3 = new Path(data.getGraph(), arcsD3);
                float distanceDijkstra3 = pathDijkstra3.getLength();
                distanceParcourue += distanceDijkstra3;
                sommetsParcourus.add(sommetDestination);
                sommetCourant = sommetDestination;
            }
        }
        solution1 = new ShortestPathSolution(data, Status.FEASIBLE, pathDijkstra1);
        ShortestPathSolution solution2 = new ShortestPathSolution(data, Status.FEASIBLE, pathDijkstra3);
        return solution1;
    }
}
