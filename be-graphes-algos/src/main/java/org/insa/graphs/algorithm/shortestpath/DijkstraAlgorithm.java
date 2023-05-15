package org.insa.graphs.algorithm.shortestpath;

import java.util.*;
import org.insa.graphs.model.*;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;


public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data)
    {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun()
    {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;

        //INITIALISATION
        int nombreSommets = data.getGraph().getNodes().size();
        BinaryHeap<Label> tas = new BinaryHeap<Label>();
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
        while (!labelDestination.getMarque() && !tas.isEmpty())
        {
            Label labelX = tas.findMin();
            labelX.setMarque(true);
            notifyNodeMarked(labelX.getSommetCourant());
            tas.remove(labelX);
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
                            }
                            else
                            {
                                labelY.setCoutRealise(labelX.getCoutRealise() + longXY);
                                labelY.setFather(labelX.getSommetCourant().getSuccessors().get(i));
                                tas.insert(labelY);
                            }
                        }

                        /*
                        boolean coutRealiseMAJ = false;
                        boolean remove = true;
                        if (labelY.getCoutRealise() > labelX.getCoutRealise() + longXY)
                        {
                            coutRealiseMAJ = true;
                        }
                        if (labelY.getCoutRealise() == Double.POSITIVE_INFINITY)
                        {
                            remove = false;
                            notifyNodeReached(labelY.getSommetCourant());
                        }
                        if (labelY == labelDestination)
                        {
                            notifyDestinationReached(data.getDestination());
                        }
                        if (coutRealiseMAJ)
                        {
                            if (remove)
                            {
                                tas.remove(labelY);
                            }
                            labelY.setCoutRealise(Math.min(labelY.getCoutRealise(), labelX.getCoutRealise() + longXY));
                            tas.insert(labelY);
                            labelY.setFather(labelX.getSommetCourant().getSuccessors().get(i));
                        }
                        */
                    }
                }
            }
        }
        // Destination has no predecessor, the solution is infeasible...
        if (Labels.get(data.getDestination().getId()).getFather() == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());
            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = Labels.get(data.getDestination().getId()).getFather();
            while (arc != null) {
                arcs.add(arc);
                arc = Labels.get(arc.getOrigin().getId()).getFather();
            }
            // Reverse the path...
            Collections.reverse(arcs);
            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(data.getGraph(), arcs));
        }
        return solution;
    }

}
