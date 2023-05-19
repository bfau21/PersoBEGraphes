package org.insa.graphs.algorithm.shortestpath;

import java.lang.Math;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

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
        

        Node sommetOrigine = data.getOrigin();
        Node sommetCourant = sommetOrigine;
        Node sommetPrecedant = null;
        Node sommetChoisi = null;
        float distanceParcourue = 0;
        float distanceMarathon = 42195;
        Path pathDijkstra1 = null;
        Path pathDijkstra2 = null;
        Path pathDijkstra3 = null;
        Path pathGlobal = null;
        boolean algoFini = false;

        while (!algoFini)
        {
            if (sommetPrecedant == null)
            {
                int i = 0;
                boolean sommetChoisiOK = false;
                while (!sommetChoisiOK)
                {
                    if (Point.distance(data.getGraph().getNodes().get(sommetCourant.getId()).getPoint(), data.getGraph().getNodes().get((sommetCourant.getId()+i)%661916).getPoint()) >= 1000 && Point.distance(data.getGraph().getNodes().get(sommetCourant.getId()).getPoint(), data.getGraph().getNodes().get((sommetCourant.getId()+i)%661916).getPoint()) <= 1200)
                    {
                        sommetChoisiOK = true;
                    }
                    i++;
                }
                sommetChoisi = data.getGraph().getNodes().get((sommetCourant.getId()+i-1)%661916);
                sommetPrecedant = sommetCourant;
            }
            else
            {
                int i = 0;
                boolean sommetChoisiOK = false;
                while (!sommetChoisiOK)
                {
                    if (Point.distance(data.getGraph().getNodes().get(sommetCourant.getId()).getPoint(), data.getGraph().getNodes().get((sommetCourant.getId()+i)%661916).getPoint()) >= 1000 && Point.distance(data.getGraph().getNodes().get(sommetCourant.getId()).getPoint(), data.getGraph().getNodes().get((sommetCourant.getId()+i)%661916).getPoint()) <= 1200 && Point.distance(data.getGraph().getNodes().get(sommetPrecedant.getId()).getPoint(), data.getGraph().getNodes().get((sommetCourant.getId()+i)%661916).getPoint()) >= 1000)
                    {
                        sommetChoisiOK = true;
                    }
                    i++;
                }
                sommetChoisi = data.getGraph().getNodes().get((sommetCourant.getId()+i-1)%661916);
                sommetPrecedant = sommetCourant;
            }
            ShortestPathData dataDijkstra1 = null;
            DijkstraAlgorithm Dijkstra1 = null;
            dataDijkstra1 = new ShortestPathData(data.getGraph(), sommetCourant, sommetChoisi, ArcInspectorFactory.getAllFilters().get(0));
            Dijkstra1 = new DijkstraAlgorithm(dataDijkstra1);
            pathDijkstra1 = Dijkstra1.run().getPath();
            distanceParcourue += pathDijkstra1.getLength();
            if (pathGlobal == null)
            {
                pathGlobal = pathDijkstra1;
            }
            else
            {
                pathGlobal = Path.concatenate(pathGlobal, pathDijkstra1);
            }
            sommetCourant = sommetChoisi;
            ShortestPathData dataDijkstra2 = new ShortestPathData(data.getGraph(), sommetChoisi, sommetOrigine, ArcInspectorFactory.getAllFilters().get(0));
            DijkstraAlgorithm Dijkstra2 = new DijkstraAlgorithm(dataDijkstra2);
            float distanceDijkstra2 = Dijkstra2.run().getPath().getLength();
            System.out.println("dijk = " + distanceDijkstra2);
            System.out.println("dist rest = " + (distanceMarathon - distanceParcourue));
            if (distanceDijkstra2 > (distanceMarathon - distanceParcourue))
            {
                algoFini = true;
                pathDijkstra2 = Dijkstra2.run().getPath();
                int i = 0;
                ArrayList<Arc> arcs = new ArrayList<>();
                while (distanceParcourue < distanceMarathon)
                {
                    arcs.add(pathDijkstra2.getArcs().get(i));
                    distanceParcourue += pathDijkstra2.getArcs().get(i).getLength();
                    i++;
                }
                pathDijkstra3 = new Path(data.getGraph(), arcs);
                pathGlobal = Path.concatenate(pathGlobal, pathDijkstra3);
            }
        }
        solution = new ShortestPathSolution(data, Status.FEASIBLE, pathGlobal);
        return solution;
    }
}
