import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

/*
* This code takes an input as a graph that contains as the first line, the number of nodes and the number of edges.
* Every subsequent line of the graph contains the fromVertex and the toVertex spearataed by some whitespace.
* The output of the code is a graph with all the nodes mapped from 0 to n-1 called remapped_YourGraphName.txt in the same format described above.
* It also removes all self-loops and multiple edges between the nodes.
* */

public class SanitizeGraph {

    private int n = 0;                                                  //Number of nodes
    private int m = 0;                                                  //Number of edges
    private ArrayList<ArrayList<Integer>> remappedGraph;                //contains the remappedGraph
    private Hashtable<Integer, Integer> vertexMap = new Hashtable<>();  //Maps all the vertices to 0..n-1

    public SanitizeGraph(String path) throws Exception {

        System.out.println("Reading Graph from file");
        int vertexCount = 0;//Counts the number of unique vertices seen so far
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        st = br.readLine();
        String[] parts = st.trim().split("\\s+");                   //requires the nodes to be separated by space and not tab
        n = Integer.parseInt(parts[0]);
        m = Integer.parseInt(parts[1]);

        remappedGraph = new ArrayList<>(n);
        for(int i = 0; i < n; i++){
            remappedGraph.add(new ArrayList<>());
        }

        while ((st = br.readLine()) != null){
            parts = st.trim().split("\\s+");
            int fromVertex = Integer.parseInt(parts[0]);
            int toVertex = Integer.parseInt(parts[1]);
//            System.out.println("fromVertex: " + fromVertex);
//            System.out.println("toVertex: " + toVertex);
            if(!vertexMap.containsKey(fromVertex)){
                vertexMap.put(fromVertex, vertexCount);
                fromVertex = vertexCount;
                vertexCount++;
            }else{
                fromVertex = vertexMap.get(fromVertex);
            }
            if(!vertexMap.containsKey(toVertex)){
                vertexMap.put(toVertex, vertexCount);
                toVertex = vertexCount;
                vertexCount++;
            }else{
                toVertex = vertexMap.get(toVertex);
            }
            if(fromVertex != toVertex && !remappedGraph.get(fromVertex).contains(toVertex)){//Remove selfLoops and multiple edges
                remappedGraph.get(fromVertex).add(toVertex);
            }
        }
        validateGraphIsCorrect(path, vertexCount);
        System.out.println("Graph generated was correct");
        printGraphToFile(path);
    }

    public void validateGraphIsCorrect(String path, int vertexCount) throws Exception  {

        System.out.println("Validating if remapped graph is correct");
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        st = br.readLine();
        String[] parts = st.trim().split("\\s+");
        int n = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        assert (n == vertexCount);//Assert that the number of nodes in the remapped graph are correct
        int numOfEdges = 0;
        for(int i = 0; i < remappedGraph.size(); i++){
            for(int j = 0; j < remappedGraph.get(i).size(); j++){
                numOfEdges++;
            }
        }
        assert (numOfEdges == m);//Assert that the number of edges in the remapped graph are correct

        while ((st = br.readLine()) != null){
            parts = st.trim().split("\\s+");
            int fromVertex = vertexMap.get(Integer.parseInt(parts[0]));
            int toVertex = vertexMap.get(Integer.parseInt(parts[1]));
            if(fromVertex != toVertex){
                assert (remappedGraph.get(fromVertex).contains(toVertex));
            }
        }
    }

    public void printGraphToFile(String path) throws Exception{
        System.out.println("Printing graph to File");
        String[] completePath = path.trim().split("\\\\");
        String fileName = completePath[completePath.length - 1];
        fileName = "remapped_" + fileName;

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
            writer.write(n + " " + m + "\n");
            for(int i = 0; i < remappedGraph.size(); i++){
                if(!remappedGraph.get(i).isEmpty()){
                    for(int j = 0; j < remappedGraph.get(i).size(); j++){
                        writer.write(i + " " + remappedGraph.get(i).get(j) + "\n");
                    }
                }
            }
        }
        System.out.println("Task Completed Successfully!");
    }
}

