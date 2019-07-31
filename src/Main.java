
public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("Initialising...");
//        SanitizeGraph MyGraph = new SanitizeGraph("C:\\Semester 3\\Thesis\\COPY_Changed_Path_Another_PrettyCode\\graphs\\p2p-Gnutella31.txt");
        FindDistance MyGraph = new FindDistance("C:\\Semester 3\\Thesis\\COPY_Changed_Path_Another_PrettyCode\\graphs\\MY-ca-GrQc-processed_50.txt");
        MyGraph.calculateDistanceFromSource(3);
    }
}
