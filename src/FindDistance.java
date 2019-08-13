import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class FindDistance {

    private int n = 0;                                                  //Number of nodes
    private int m = 0;                                                  //Number of edges
    private ArrayList<ArrayList<Integer>> myGraph;                      //contains the graph
    private ArrayList<ArrayList<Integer>> myTransposedGraph;            //contains the transposedGraph
    private ArrayList<Integer> distances = new ArrayList<>();           //This will store the distance of each node from the source
    private String pathToGraphFile = "";
    private String pathToHBNFile = "";
    private String pathToAllActNodesFile = "";

    /*
     * Given a source node s, the idea is to find all the nodes that are reachable from s, and at the same time, also calculate
     * the distance of each of those nodes from s.
     * Prints the results in a text file: distanceFromSource_<mySourceNode>_<myGraphName>.txt
     * Entries are separated by a :
     * 23: 3 //means that the vertex 23 was at a distance of 3 from the source
     * 3: 0  //means that vertex 3 was the source
     * 4: -1 //means that vertex 4 was not reachable from the source
     * */
    public FindDistance(String pathToGraph, String pathToHBN, String pathToAllActNodes) throws Exception{

        System.out.println("Reading Graph from file");
        pathToGraphFile = pathToGraph;
        pathToHBNFile = pathToHBN;
        pathToAllActNodesFile = pathToAllActNodes;
        File file = new File(pathToGraph);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        st = br.readLine();
        String[] parts = st.trim().split("\\s+");//any whitespace will do
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

        //Moving Initializations to Constructor because we made the distances variable Global
        for(int i = 0; i < n; i++){
            distances.add(-1);
        }

        //Creating the transpose of the input graph
        myTransposedGraph = new ArrayList<>();
        for(int i = 0; i < n; i++){
            myTransposedGraph.add(new ArrayList<>());
        }
        for(int fromVertex = 0; fromVertex < myGraph.size(); fromVertex++){
            for(int j = 0; j < myGraph.get(fromVertex).size(); j++){
                int toVertex = myGraph.get(fromVertex).get(j);
                myTransposedGraph.get(toVertex).add(fromVertex);
            }
        }
        assertTransposeIsCorrect();
    }

    /*
    * Datastructures Used:
    *
    * 1) myGraph: ArrayList<ArrayList<Integer>>

    * 2) distances: ArrayList<Integer>
    * for every vertex v, it stores the distance of v from sourceNodeVertex
    * 23: 3 means that the vertex 23 was at a distance of 3 from the source
    * 3: 0  means that vertex 3 was the source
    * 4: -1 means that vertex 4 was not reachable from the source
    *
    * 3) bfsList: ArrayList<ArrayList<Integer>>
    * Used for doing the BFS while storing the layer of each node as well. Size of list is n.
    *               0
    *              / \
    *             1   2
    *            /\
    *           3  4
    * list[0] will contain 0
    * list[1] will contain 1,2
    * list[2] will contain 3,4
    * All the subsequent lists will be empty
    *
    * 4) shortestDistFromNode: ArrayList<Integer>
    * For every vertex v, it stores the source vertex w from which v was at the shortest distance
    * If v was unreachable from every sourceVertex, it stores -1
    *
    * 5) srcNodesThatRchdThisNode: ArrayList<ArrayList<Integer>>
    * For every vertex v, it stores all the source nodes that had a path to v
    *
     * */
    public void calculateDistanceFromSource(ArrayList<Integer> sourceNodeList) throws Exception{

        //Initializations
        ArrayList<ArrayList<Integer>> srcNodeClosestToThisNode = new ArrayList<>();
        ArrayList<ArrayList<Integer>> srcNodesThatRchdThisNode = new ArrayList<>();
        HashSet<Integer> highBeliefNodes = new HashSet<>();                                                             //Stores the top 100 high belief Nodes
        ArrayList<Integer> incomingHBNCount = new ArrayList<>();                                                        //For every node - this stores the number of highBeliefNodes that have an incoming edge to this node
        ArrayList<Integer> outgoingHBNCount = new ArrayList<>();                                                        //For every node - this stores the number of outgoing edges to highBeliefNodes
        HashSet<Integer> allActivatedNodes = new HashSet<>();                                                           //Stores the list of all activated nodes in the graph
        ArrayList<Integer> incomingAllActNodes = new ArrayList<>();                                                     //For every node - this stores the number of activated Nodes that have an incoming edge to this node
        ArrayList<Integer> outgoingAllActNodes = new ArrayList<>();                                                     //For every node - this stores the number of outgoing edges to activated nodes

        //Initializations
        for(int i = 0; i < n; i++){
            srcNodeClosestToThisNode.add(new ArrayList<>());
            incomingHBNCount.add(0);
            outgoingHBNCount.add(0);
            incomingAllActNodes.add(0);
            outgoingAllActNodes.add(0);
            srcNodesThatRchdThisNode.add(new ArrayList<>());
        }

        //Calculating Distances for each Node
        for(int i = 0; i < sourceNodeList.size(); i++){
            int sourceNode = sourceNodeList.get(i);
            calculateDistanceFromSource(sourceNode, srcNodeClosestToThisNode, srcNodesThatRchdThisNode);
        }

        System.out.println("Minimum Distances Calculated");

        readingHbnFromFile(highBeliefNodes);
        readingAllActNodesFromFile(allActivatedNodes);
        System.out.println("Populating DataStructures");
        //Populating the incomingHBNCount and incomingAllActNodes datastructure
        for(int fromVertex = 0; fromVertex < myTransposedGraph.size(); fromVertex++){
            for(int j = 0; j < myTransposedGraph.get(fromVertex).size(); j++){
                int toVertex = myTransposedGraph.get(fromVertex).get(j);
                if(highBeliefNodes.contains(toVertex)){
                    int currValue = incomingHBNCount.get(fromVertex);
                    incomingHBNCount.set(fromVertex, ++currValue);
                }
                if(allActivatedNodes.contains(toVertex)){
                    int currValue = incomingAllActNodes.get(fromVertex);
                    incomingAllActNodes.set(fromVertex, ++currValue);
                }
            }
        }
        //Populating the outgoingHBNCount datastructure
        for(int fromVertex = 0; fromVertex < myGraph.size(); fromVertex++){
            for(int j = 0; j < myGraph.get(fromVertex).size(); j++){
                int toVertex = myGraph.get(fromVertex).get(j);
                if(highBeliefNodes.contains(toVertex)){
                    int currValue = outgoingHBNCount.get(fromVertex);
                    outgoingHBNCount.set(fromVertex, ++currValue);
                }
                if(allActivatedNodes.contains(toVertex)){
                    int currValue = outgoingAllActNodes.get(fromVertex);
                    outgoingAllActNodes.set(fromVertex, ++currValue);
                }
            }
        }

        printResultsToFile(sourceNodeList, srcNodeClosestToThisNode, srcNodesThatRchdThisNode,
                incomingHBNCount, outgoingHBNCount, incomingAllActNodes, outgoingAllActNodes);
    }

    //This method reads the top 100 HighBeliefNodes from the input file
    private void readingHbnFromFile(HashSet<Integer> highBeliefNodes) throws Exception{

        System.out.println("Reading High Belief Nodes From File");
        File file = new File(pathToHBNFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        String[] parts;
        while ((st = br.readLine()) != null){
            parts = st.trim().split("\\s+");
            int highBeliefNode = Integer.parseInt(parts[0]);
            highBeliefNodes.add(highBeliefNode);
        }
        assert(highBeliefNodes.size() == 100);
    }

    //This method reads the activated nodes from the input file
    private void readingAllActNodesFromFile(HashSet<Integer> allActivatedNodes) throws Exception{

        System.out.println("Reading All Activated Nodes From File");
        File file = new File(pathToAllActNodesFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        String[] parts;
        while ((st = br.readLine()) != null){
            parts = st.trim().split("\\s+");
            int activatedNode = Integer.parseInt(parts[0]);
            allActivatedNodes.add(activatedNode);
        }
        assert (allActivatedNodes.size() > 0);
    }

    private void calculateDistanceFromSource(int sourceNode, ArrayList<ArrayList<Integer>> srcNodeClosestToThisNode, ArrayList<ArrayList<Integer>> srcNodesThatRchdThisNode) throws Exception {

        //Initializations
        System.out.println("Calculating Distances From:" + sourceNode);

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
                    int prevDistFromSource = distances.get(bfsList.get(i).get(j));
                    int currNode = bfsList.get(i).get(j);
                    srcNodesThatRchdThisNode.get(currNode).add(sourceNode);//Because the srcNode could reach the currNode, we add the srcNode to the List[currNode]
                    if(prevDistFromSource > i || prevDistFromSource == -1){
                        srcNodeClosestToThisNode.get(currNode).clear();//Because the currNode was closer to this srcNode compared to the prev srcNode, we first clear the previously saved closest nodes to this node
                        srcNodeClosestToThisNode.get(currNode).add(sourceNode);//Because the currNode was closer to this srcNode compared to the prev srcNode, we update the srcNodeClosestToThisNode DS
                        distances.set(currNode, i);
                    } else if (prevDistFromSource == i ){
                        srcNodeClosestToThisNode.get(currNode).add(sourceNode);//Because the currNode is at the same dist from this srcNode compared to the prev srcNode, we add sourceNode to the list of nodes in srcNodeClosestToThisNode[currNode]
                    }
                }
            }else break;
        }

        //Printing out the info
//        for(int i = 0; i < distances.size(); i++){
//            System.out.println(i + ": " + distances.get(i));
//        }
    }

    //Counts the number of reachable nodes from the sourceNodeList
    //Can Print the reachable nodes as well, but only to the console
    public void findReachableNodesFromSource (ArrayList<Integer> sourceNodeList) throws Exception{

        ArrayList<Boolean> visited = new ArrayList<>();
        for(int i = 0; i < n; i++){
            visited.add(false);
        }
        for(int i = 0; i < sourceNodeList.size(); i++){
            if(!visited.get(sourceNodeList.get(i))){
                int sourceNode = sourceNodeList.get(i);
                findReachableNodesFromSource(sourceNode, visited);
            }
        }

        //Printing out the info
        int numOfRchblNodes = 0;
        for(int i = 0; i < visited.size(); i++){
            if(visited.get(i)){
//                System.out.println(i);
                numOfRchblNodes++;
            }
        }
        System.out.println("Number of reachable Nodes from: " + sourceNodeList + " = " + numOfRchblNodes);
        System.out.println("Completed Finding Reachable Nodes Successfully!");
    }

    /*
    * This method prints out the nodes that are reachable from the given sourceNode vertex
    * */
    private void findReachableNodesFromSource (int sourceNode, ArrayList<Boolean> visited) throws Exception{

        //Initializations
        System.out.println("Calculating Reachable Nodes from Source");
        Queue<Integer> bfsQueue = new LinkedList<>();

        //Starting BFS over the input graph.
        visited.set(sourceNode, true);
        bfsQueue.add(sourceNode);

        int u = 0;
        int v = 0;
        while(!bfsQueue.isEmpty()){
            u = bfsQueue.poll();
            for(int j = 0; j < myGraph.get(u).size(); j++){
                v = myGraph.get(u).get(j);
                if (!visited.get(v)){
                    visited.set(v, true);
                    bfsQueue.add(v);
                }
            }
        }
    }

    /*
     * Given a source node s, the idea is to find all the nodes that are reachable from s, and at the same time, also calculate
     * the distance of each of those nodes from s.
     * Prints the results in a text file: distanceFromSource_<mySourceNode>_<myGraphName>.txt
     * Entries are separated by a :
     * 23: 3 //means that the vertex 23 was at a distance of 3 from the source
     * 3: 0  //means that vertex 3 was the source
     * 4: -1 //means that vertex 4 was not reachable from the source
     * */
    private void printResultsToFile(ArrayList<Integer> sourceNodeList,
                                    ArrayList<ArrayList<Integer>> shortestDistFromNode,
                                    ArrayList<ArrayList<Integer>> srcNodesWithPath,
                                    ArrayList<Integer> incomingHighBeliefNodes,
                                    ArrayList<Integer> outgoingHighBeliefNodes,
                                    ArrayList<Integer> incomingAllActNodes,
                                    ArrayList<Integer> outgoingAllActNodes) throws Exception {

        System.out.println("Printing values to File");
        String[] completePath = pathToGraphFile.trim().split("\\\\");
        String fileName = completePath[completePath.length - 1];
        fileName = "distanceFromSource_" +  fileName;

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
            writer.write("Distance of nodes from the sourceList: " + sourceNodeList + "\n");
            writer.write("Node\tdistFromSrc\tclosestSrcNode\tsrcNodesThatRchdThisNode\tOutDegree\tnumOfOutgoingActNodes\tnumOfOutgoingHBN" +
                    "\tInDegree\tnumOfIncomingActNodes\tnumOfIncomingHBN" + "\n");
            for(int i = 0; i < distances.size(); i++){
                writer.write(i +"\t"+ distances.get(i) +"\t"+ shortestDistFromNode.get(i) +"\t"+ srcNodesWithPath.get(i) +
                        "\t"+ myGraph.get(i).size() +"\t"+ outgoingAllActNodes.get(i) +"\t"+ outgoingHighBeliefNodes.get(i) +
                        "\t"+ myTransposedGraph.get(i).size() +"\t"+ incomingAllActNodes.get(i) +"\t"+ incomingHighBeliefNodes.get(i) +
                        "\n");
            }
        }
        System.out.println("Task Completed Successfully!");
    }

    private void assertTransposeIsCorrect(){
        assert (n == myTransposedGraph.size());
        int numOfEdgesInTranspose = 0;
        for(int fromVertex = 0; fromVertex < myTransposedGraph.size(); fromVertex++){
            for(int j = 0; j < myTransposedGraph.get(fromVertex).size(); j++){
                int toVertex = myTransposedGraph.get(fromVertex).get(j);
                assert (myGraph.get(toVertex).contains(fromVertex));
                numOfEdgesInTranspose++;
            }
        }
        assert (numOfEdgesInTranspose == m);
    }
}
