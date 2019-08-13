import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("Initialising...");
//        SanitizeGraph MyGraph = new SanitizeGraph("C:\\Semester 3\\Thesis\\COPY_Changed_Path_Another_PrettyCode\\graphs\\p2p-Gnutella30.txt");

        String pathToGraph = "C:\\Semester 3\\Thesis\\XFU_BM\\GraphsForGephi\\CA-HepThSim\\with1SeedSetNode\\CA-HepThSim.txt";
        String pathToHBN = "C:\\Semester 3\\Thesis\\XFU_BM\\GraphsForGephi\\CA-HepThSim\\with1SeedSetNode\\HighBeliefNodes_Seed_1.txt";
        String pathToAllActivatedNodes = "C:\\Semester 3\\Thesis\\XFU_BM\\GraphsForGephi\\CA-HepThSim\\with1SeedSetNode\\AllActivatedNodes_Seed_1.txt";

//        String pathToGraph = "C:\\Semester 3\\Thesis\\XFU_BM\\GraphsForGephi\\soc-epinions-1\\soc-Epinions1.txt";
//        String pathToHBN = "C:\\Semester 3\\Thesis\\XFU_BM\\GraphsForGephi\\soc-epinions-1\\HighBeliefNodes_Seed_1.txt";
//        String pathToAllActivatedNodes = "C:\\Semester 3\\Thesis\\XFU_BM\\GraphsForGephi\\soc-epinions-1\\AllActivatedNodes_Seed_1.txt";

        FindDistance MyGraph = new FindDistance(pathToGraph, pathToHBN, pathToAllActivatedNodes);
        ArrayList<Integer> sourceNodeList = new ArrayList<>();
        //ca-HepthSim-Nodes
        sourceNodeList.add(54);
//        sourceNodeList.add(163);
//        sourceNodeList.add(1326);
//        sourceNodeList.add(2013);
//        sourceNodeList.add(9255);
        //my-caGrq-Nodes
//        sourceNodeList.add(3);
//        sourceNodeList.add(47);
//        sourceNodeList.add(12);
        //facebook_combined_graph Nodes
//        sourceNodeList.add(107);
//        sourceNodeList.add(1684);
//        sourceNodeList.add(1912);
//        sourceNodeList.add(1917);
//        sourceNodeList.add(1941);
        //soc-epinions-1 nodes
//        sourceNodeList.add(71399);
        MyGraph.calculateDistanceFromSource(sourceNodeList);
        MyGraph.findReachableNodesFromSource(sourceNodeList);
    }
}
