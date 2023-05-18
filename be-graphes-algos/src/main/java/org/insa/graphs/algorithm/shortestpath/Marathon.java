package org.insa.graphs.algorithm.shortestpath;

import java.lang.Math;
import java.util.*;
import org.insa.graphs.model.*;
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
        ShortestPathSolution solution = null;
        

        Node origine = data.getOrigin();
        Node sommetDestination = origine; //modifié ensuite
        float distanceParcourue = 0;
        float distance = 42195;
        Node sommetCourant = origine;
        ArrayList<Node> sommetsParcourus = new ArrayList<Node>();
        sommetsParcourus.add(origine);
        Path pathDijkstra1 = null;
        Path pathDijkstra3 = null;
        Path pathGlobal = null;
        boolean algoFini = false;
        boolean doitRentrer = false;
        while (!algoFini)
        {
            if (!doitRentrer)
            {
                if (distanceParcourue > distance + 2000)
                {
                    System.out.println("PB dist trop grande");
                }
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
                //int idDestination = data.getDestination().getId();
                //Label labelDestination = Labels.get(idDestination);
                Label labelX = tas.findMin();
                while (!tas.isEmpty() && labelX.getCoutRealise() < 4000)
                {
                    labelX = tas.findMin();
                    labelX.setMarque(true);
                    if (labelX.getSommetCourant() != nodeOrigin)
                    {
                        sommetsIsochrones.add(labelX.getSommetCourant());
                    }
                    notifyNodeMarked(labelX.getSommetCourant());
                    tas.remove(labelX);
                    for (int i = 0; i < labelX.getSommetCourant().getNumberOfSuccessors(); i++)
                    {
                        if(data.isAllowed(labelX.getSommetCourant().getSuccessors().get(i))){
                            int idLabelY = labelX.getSommetCourant().getSuccessors().get(i).getDestination().getId();
                            Label labelY = Labels.get(idLabelY);
                            /*
                            boolean sommetUse = false;
                            int j = 0;
                            while (j < pathGlobal.getLength())
                            {
                                if (labelY.getSommetCourant() == pathGlobal.getArcs().get(j).getOrigin())
                                {
                                    sommetUse = true;
                                }
                                j++;
                            }
                            if (sommetUse)
                            {
                                continue;
                            }
                            */
                            float longXY = labelX.getSommetCourant().getSuccessors().get(i).getLength();
                            //float timeXY = labelX.getSommetCourant().getSuccessors().get(i).getTravelTime();
                            if (!labelY.getMarque())
                            {
                                boolean remove = true;
                                /*
                                if (labelY == labelDestination)
                                {
                                    notifyDestinationReached(data.getDestination());
                                }
                                */
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
                //Disjktra entre les 2 sommets
                boolean sommetChoisiOK = false;
                Node sommetChoisi = null;
                ShortestPathData dataDijkstra1 = null;
                DijkstraAlgorithm Dijkstra1 = null;
                while (!sommetChoisiOK)
                {
                    int nbAleatoire = (int) (Math.random()*(sommetsIsochrones.size()-1));
                    sommetChoisi = sommetsIsochrones.get(nbAleatoire);
                    dataDijkstra1 = new ShortestPathData(data.getGraph(), sommetCourant, sommetChoisi, ArcInspectorFactory.getAllFilters().get(0));
                    Dijkstra1 = new DijkstraAlgorithm(dataDijkstra1);
                    pathDijkstra1 = Dijkstra1.run().getPath();
                    if (pathDijkstra1 != null && pathDijkstra1.getLength() > 3000)
                    {
                        sommetChoisiOK = true;
                    }
                }
                if (pathGlobal == null)
                {
                    pathGlobal = pathDijkstra1;
                }
                else
                {
                    //System.out.println("G = " + pathGlobal.getArcs().get(pathGlobal.getArcs().size()-1).getDestination().getId());
                    //System.out.println("D1 = " + pathDijkstra1.getArcs().get(0).getOrigin().getId());
                    pathGlobal = Path.concatenate(pathGlobal, pathDijkstra1);
                }
                float distanceDijkstra1 = pathDijkstra1.getLength();
                distanceParcourue += distanceDijkstra1;
                sommetsParcourus.add(sommetChoisi);
                sommetCourant = sommetChoisi;
                ShortestPathData dataDijkstra2 = new ShortestPathData(data.getGraph(), sommetChoisi, origine, ArcInspectorFactory.getAllFilters().get(0));
                DijkstraAlgorithm Dijkstra2 = new DijkstraAlgorithm(dataDijkstra2);
                float distanceDijkstra2 = Dijkstra2.run().getPath().getLength();
                if (distanceDijkstra2 > (distance - distanceParcourue)-2000 && distanceDijkstra2 < (42195 - distanceParcourue)+2000)
                {
                    doitRentrer = true;
                }
            }
            else
            {
                algoFini = true;
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
                Node nodeOriginD3 = sommetCourant;
                int idOriginD3 = nodeOriginD3.getId();
                System.out.println(idOriginD3);
                LabelsD3.get(idOriginD3).setCoutRealise(0);
                tasD3.insert(LabelsD3.get(idOriginD3));
                notifyOriginProcessed(nodeOriginD3);
                //ITERATIONS
                int idDestinationD3 = origine.getId();
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
                sommetDestination = sommetsD3.get(sommetsD3.size()-1);
                ArrayList<Arc> arcsD3 = new ArrayList<>();
                Arc arcD3 = LabelsD3.get(sommetDestination.getId()).getFather();
                while (arcD3 != null) {
                    arcsD3.add(arcD3);
                    arcD3 = LabelsD3.get(arcD3.getOrigin().getId()).getFather();
                    }
                Collections.reverse(arcsD3);
                pathDijkstra3 = new Path(data.getGraph(), arcsD3);
                pathGlobal = Path.concatenate(pathGlobal, pathDijkstra3);
                float distanceDijkstra3 = pathDijkstra3.getLength();
                distanceParcourue += distanceDijkstra3;
                sommetsParcourus.add(sommetDestination);
                sommetCourant = sommetDestination;
            }
        }
        solution = new ShortestPathSolution(data, Status.FEASIBLE, pathGlobal);
        return solution;
    }
}
