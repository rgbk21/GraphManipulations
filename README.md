# GraphManipulations
Code base to help with Graph operations

<div><b>Class SanitizeGraph:</b></div>
<div>
<b>Input:</b>
This class maps the vertices of the imput graph to a vertex set from 0 to n-1, where n is the number of vertices in the input graph. Ouptut graph is named: remapped_YourGraphName.txt. It also removes all self-loops and multiple edges between the nodes.
</div>
<div>
	<b>SanitizeGraph(String path):</b><i>path</i> is the complete path of the graph file as input. 
	<br>Input graph should be in the following format: the first line should be the number of nodes and the number of edges spearated by whitespace. Each subsequent line should represent an edge where the Source and Target are separated by whitespace.
	eg.<br>
	4 5<br>
	0 1<br>
	0 3<br>
	1 2<br>
	1 3<br>
	3 2<br>
</div>

<br>

<div>
<b>Class FindDistance:</b><br>
This class accomplishes 2 things. Consider a set of nodes S. <br>
1) For every vertex in the graph, it calculates the minimum distance of that vertex from S. <br>
2) It counts the number of vertices reachable from S.
</div>
<div><b>FindDistance(String path):</b><i>path</i> is the absolute path of the input graph file</div>
<div><b>calculateDistanceFromSource(ArrayList &lt Integer &gt sourceNodeList):</b><i>sourceNodeList</i> is an ArrayList containing the vertices that will be considered as the source from which the distance to every vertex will be calculated (S). The output is in the form of a text file that contains 4 columns. <br>
1) Node: The node id <br>
2) distFromSrc: The min distance of the node from the Set of nodes S. Value of -1 means the node was not reachable. <br>
3) closestSrcNode: The sourceNode from the set S that had the minimum distance to this node. Value of -1 means the node was not reachable. <br>
4) srcNodesRchd: The list of the nodes from the set S that were able to reach this node. <br>
</div>
<div><b>findReachableNodesFromSource (ArrayList &lt Integer &gt sourceNodeList):</b>
This method counts the nodes that are reachable from the Set S. It can also print out the nodes that are reachable from the Set S

</div>
