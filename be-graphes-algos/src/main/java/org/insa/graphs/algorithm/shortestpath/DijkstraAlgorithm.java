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

        //ITERATIONS
        int idDestination = data.getDestination().getId();
        Label labelDestination = Labels.get(idDestination);
        while (!labelDestination.getMarque() && !tas.isEmpty())
        {
            Label labelX = tas.findMin();
            labelX.setMarque(true);
            for (int i = 0; i < labelX.getSommetCourant().getNumberOfSuccessors(); i++)
            {
                int idLabelY = labelX.getSommetCourant().getSuccessors().get(i).getDestination().getId();
                Label labelY = Labels.get(idLabelY);
                float longXY = labelX.getSommetCourant().getSuccessors().get(i).getLength();
                if (!labelY.getMarque())
                {
                    boolean coutRealiseMAJ = false;
                    if (labelY.getCoutRealise() > labelX.getCoutRealise() + longXY)
                    {
                        coutRealiseMAJ = true;
                    }
                    labelY.setCoutRealise(Math.min(labelY.getCoutRealise(), labelX.getCoutRealise() + longXY));
                    if (coutRealiseMAJ)
                    {
                        tas.insert(labelY);
                        labelY.setFather(labelX.getSommetCourant().getSuccessors().get(i));
                    }
                }
            }
        }



        // Destination has no predecessor, the solution is infeasible...
        System.out .println(Labels.get(data.getDestination().getId()).getFather());

        if (Labels.get(data.getDestination().getId()).getFather() == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
            System.out .println("rentre");
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
