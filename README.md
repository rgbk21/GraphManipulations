# GraphManipulations
Code base to help with Graph operations

<div><b>Class SanitizeGraph:</b></div>
<div>
<b>Input:</b>
Graph that contains as the first line, the number of nodes and the number of edges.
Every subsequent line of the graph contains the fromVertex and the toVertex separated by some whitespace.<br>
<b>Output:</b> Graph with all the nodes mapped from 0 to n-1 called remapped_YourGraphName.txt in the same format described above.
It also removes all self-loops and multiple edges between the nodes.
</div>

<br>

<div>
<b>Class FindDistance:</b>
</div>
<p>Given a source node s, the idea is to find all the nodes that are reachable from s, and at the same time, also calculate
the distance of each of those nodes from s. Prints the results in a text file: distanceFromSource_mySourceNode_myGraphName.txt<br>
Entries are separated by a :<br>
23: 3 //means that the vertex 23 was at a distance of 3 from the source<br>
3: 0  //means that vertex 3 was the source<br>
4: -1 //means that vertex 4 was not reachable from the source<br>
</div>
