import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
* Given a source node s, the idea is to find all the nodes that are reachable from s, and at the same time, also calculate
* the distance of each of those nodes from s.
* Prints the results in a text file: distanceFromSource_<mySourceNode>_<myGraphName>.txt
* Entries are separated by a :
* 23: 3 //means that the vertex 23 was at a distance of 3 from the source
* 3: 0  //means that vertex 3 was the source
* 4: -1 //means that vertex 4 was not reachable from the source
* */

public class FindDistance {

    private int n = 0;                                                  //Number of nodes
    private int m = 0;                                                  //Number of edges
    private ArrayList<ArrayList<Integer>> myGraph;                      //contains the remappedGraph
    private ArrayList<Integer> distances;                               //This will store the distance of each node from the source
    private String pathToFile = "";

    public FindDistance(String path) throws Exception{

        System.out.println("Reading Graph from file");
        pathToFile = path;
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        st = br.readLine();
        String[] parts = st.trim().split("\\s+");                   //requires the nodes to be separated by space and not tab
        n = Integer.parseInt(parts[0]);
        m = Integer.parseInt(parts[1]);

        myGraph = new ArrayList<>(n);
        for(int i = 0; i < n; i++){
            myGraph.add(new ArrayList<>());
        }

        while ((st = br.readLine()) != null){
            parts = st.trim().split("\\s+");
            int fromVertex = Integer.parseInt(parts[0]);
            int toVertex = Integer.parseInt(parts[1]);
//            System.out.println("fromVertex: " + fromVertex);
//            System.out.println("toVertex: " + toVertex);
            myGraph.get(fromVertex).add(toVertex);
        }
    }

    public void calculateDistanceFromSource (int sourceNode) throws Exception{

        //Initializations
        System.out.println("Calculating Distances");
        distances = new ArrayList<>();
        for(int i = 0; i < n; i++){
            distances.add(-1);
        }
        ArrayList<Boolean> visited = new ArrayList<>();
        for(int i = 0; i < n; i++){
            visited.add(false);
        }
        ArrayList<ArrayList<Integer>> bfsList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            bfsList.add(new ArrayList<>());
        }

        //Starting BFS over the input graph. Using an ArrayList of ArrayList to count layers
        int dist = 0;
        visited.set(sourceNode, true);
        bfsList.get(dist).add(sourceNode);
        distances.set(sourceNode, 0);

        int u = 0;
        int v = 0;
        while(!bfsList.get(dist).isEmpty()){
            for(int i = 0; i < bfsList.get(dist).size(); i++){
                u = bfsList.get(dist).get(i);
                for(int j = 0; j < myGraph.get(u).size(); j++){
                    v = myGraph.get(u).get(j);
                    if (!visited.get(v)){
                        visited.set(v, true);
                        bfsList.get(dist+1).add(v);
                    }
                }
            }
            dist++;
        }

        //Populating the distances ArrayList
        for(int i = 0; i < bfsList.size(); i++){
            if(!bfsList.get(i).isEmpty()){
                for(int j = 0; j < bfsList.get(i).size(); j++){
                    distances.set(bfsList.get(i).get(j), i);
                }
            }else break;
        }

        //Printing out the info
//        for(int i = 0; i < distances.size(); i++){
//            System.out.println(i + ": " + distances.get(i));
//        }
        System.out.println("Distances Calculated");
        printResultsToFile(sourceNode);
    }

    public void printResultsToFile(int sourceNode) throws Exception{
        System.out.println("Printing values to File");
        String[] completePath = pathToFile.trim().split("\\\\");
        String fileName = completePath[completePath.length - 1];
        fileName = "distanceFromSource_" + sourceNode + "_"+ fileName;

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
            for(int i = 0; i < distances.size(); i++){
                writer.write(i + "\t" + distances.get(i) + "\n");
            }
        }
        System.out.println("Task Completed Successfully!");
    }

    /*
    * This method prints out the nodes that are reachable from the given sourceNode vertex
    * */
    public void findReachableNodesFromSource (int sourceNode) throws Exception{

        //Initializations
        System.out.println("Calculating Reachable Nodes from Source");
        ArrayList<Integer> reachableNodes = new ArrayList<>();
        ArrayList<Boolean> visited = new ArrayList<>();
        for(int i = 0; i < n; i++){
            visited.add(false);
        }
        Queue<Integer> bfsQueue = new LinkedList<>();

        //Starting BFS over the input graph.
        visited.set(sourceNode, true);
        bfsQueue.add(sourceNode);
        reachableNodes.add(sourceNode);

        int u = 0;
        int v = 0;
        while(!bfsQueue.isEmpty()){
            u = bfsQueue.poll();
            for(int j = 0; j < myGraph.get(u).size(); j++){
                v = myGraph.get(u).get(j);
                if (!visited.get(v)){
                    visited.set(v, true);
                    bfsQueue.add(v);
                    reachableNodes.add(v);
                }
            }
        }

        //Printing out the info
        System.out.println("Number of reachable Nodes from: " + sourceNode + " = " + reachableNodes.size());
        for(int i = 0; i < reachableNodes.size(); i++){
            System.out.println(reachableNodes.get(i));
        }
        System.out.println("Completed Finding Reachable Nodes Successfully");
    }
}
