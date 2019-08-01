import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("Initialising...");
//        SanitizeGraph MyGraph = new SanitizeGraph("C:\\Semester 3\\Thesis\\COPY_Changed_Path_Another_PrettyCode\\graphs\\p2p-Gnutella31.txt");
        FindDistance MyGraph = new FindDistance("C:\\Semester 3\\Thesis\\XFU_BM\\GraphsForGephi\\fb-Combined\\DataFrom27thJuly\\facebook_combined_graph.txt");
//        FindDistance MyGraph = new FindDistance("C:\\Semester 3\\Thesis\\COPY_Changed_Path_Another_PrettyCode\\graphs\\MY-ca-GrQc-processed_50.txt");
        ArrayList<Integer> sourceNodeList = new ArrayList<>();
//        sourceNodeList.add(3);
//        sourceNodeList.add(47);
//        sourceNodeList.add(12);
//        sourceNodeList.add(107);
//        sourceNodeList.add(1684);
        sourceNodeList.add(1912);
        sourceNodeList.add(1917);
        sourceNodeList.add(1941);
        MyGraph.calculateDistanceFromSource(sourceNodeList);
        MyGraph.findReachableNodesFromSource(sourceNodeList);
    }
}
