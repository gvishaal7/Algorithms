

package testffa;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import java.util.*;
//import static testFFa.GraphInput.LoadSimpleGraph;
//import testFFa.Vertex.*;
/**
 *
 * @author Vishaal Gopalan
 */
public class FordFulkerson {

 
    private static final ArrayList vertices = new ArrayList(); //contains all the vertices of the graph
    private static String source; //the source node
    private static String sink; //the sink node
    
    //The following function computes the max-flow of the given graph from all the possible augmented paths
    public static double computeMaxFlow(Hashtable inputGraph)
    {
        double maxflow = 0.0;
        String u,v;
        Hashtable residualGraph = new Hashtable(); //residual graph
        try
        {
            getVertices(inputGraph);
            residualGraph = inputGraph;
            Hashtable parent = new Hashtable(); //contains the parent node of each nodes
            while(augumentPath(residualGraph,parent))
            {
                double temp = Double.MAX_VALUE;
                for(v=sink;!v.equalsIgnoreCase(source);v=(String)parent.get(v))
                {
                    u = (String)parent.get(v);
                    double t1 = getResValue(u, v, residualGraph);
                    temp = Math.min(t1, temp); //bottle neck of for the given augmented path
                }
                for(v=sink;!v.equalsIgnoreCase(source);v=(String)parent.get(v))
                {
                    u =(String)parent.get(v);
                    setResidualValues(u,v,temp,residualGraph); //updates the residual graph
                }
                maxflow += temp;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return maxflow;
    }

    //the following function finds the possible augmented paths from source to the sink using BFS
    public static Boolean augumentPath(Hashtable inputGraph, Hashtable parent)
    {
        Hashtable visited = new Hashtable(); //contains the visited state of a node
        try
        {
            for(int i=0;i<vertices.size();i++)
            {
                visited.put(vertices.get(i), false); //intially setting the visited state of all the node to false
            }
            LinkedList que = new LinkedList();
            que.add(source);
            visited.replace(source, true);
            parent.put(source, -1);
            while(!que.isEmpty())
            {
                String head = (String)que.poll(); //element at the top of the que
                for(Enumeration en = inputGraph.keys();en.hasMoreElements();)
                {
                    String key = (String)en.nextElement();
                    if((boolean)visited.get(key)==false && getResValue(head,key,inputGraph) > 0)
                    {
                        que.add(key);
                        parent.put(key, head);
                        visited.put(key,true); 
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return ((boolean)visited.get(sink));
    }
    
    //the following function updates the residual graph
    public static void setResidualValues(String u, String v, double val, Hashtable inputGraph)
    {
        try
        {
            //forward edge value from the residual graph for the given edge
            Vertex vertex = (Vertex)inputGraph.get(u);
            LinkedList linkedList = vertex.incidentEdgeList;
            Iterator it = (Iterator)linkedList.iterator();
            while(it.hasNext())
            {
                Edge edge = (Edge)it.next();
                Vertex secondVertex = (Vertex)edge.getSecondEndpoint();
                String secondVertexName = (String)secondVertex.getName();
                if(secondVertexName.equalsIgnoreCase(v))
                {
                    double tempVal = (double)edge.getData();
                    tempVal -= val;
                    edge.setData(tempVal);
                }
            }
            
            //reverse edge value from the residual graph for the given edge
            vertex = (Vertex)inputGraph.get(v);
            linkedList = vertex.incidentEdgeList;
            it = (Iterator)linkedList.iterator();
            while(it.hasNext())
            {
                Edge edge = (Edge)it.next();
                Vertex secondVertex = (Vertex)edge.getSecondEndpoint();
                String secondVertexName = (String)secondVertex.getName();
                if(secondVertexName.equalsIgnoreCase(u))
                {
                    double tempVal = (double)edge.getData();
                    tempVal += val;
                    edge.setData(tempVal);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //the following function retrieves the forward edge value of the give edge from the residual graph
    public static double getResValue(String u, String v, Hashtable inputGraph)
    {
        Double value = 0.0;
        try
        {
            Vertex vertex = (Vertex)inputGraph.get(u);
            LinkedList linkedList = vertex.incidentEdgeList;
            Iterator it = (Iterator)linkedList.iterator();
            while(it.hasNext())
            {
                Edge edge = (Edge)it.next();
                Vertex secondVertex = (Vertex)edge.getSecondEndpoint();
                String secondVertexName = (String)secondVertex.getName();
                if(secondVertexName.equalsIgnoreCase(v))
                {
                    value = (Double)edge.getData();
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            value = 0.0;
        }
        return value;
    }

    
    //the following function retrieves all the vertices of the generated graph
    public static void getVertices(Hashtable inputGraph)
    {
        try
        {
            for(Enumeration en=inputGraph.keys();en.hasMoreElements();)
            {
                vertices.add((String)en.nextElement());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //the following function writes the max-flow, the time taken to execute the algorithm
    //for the generated graph from the input file and the file name to a file(logFile.csv)
    public static void logToFile(long time, String fileName,double maxFlow)
    {
        FileWriter fileWriter = null;
        BufferedWriter  bufferedWriter = null;
        PrintWriter printWriter = null;
        String fName = "logFile.csv";
        try
        {
            File file = new File(fName);
            fileWriter = new FileWriter(file,true);
            bufferedWriter = new BufferedWriter(fileWriter);
            printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(fileName+","+time+","+maxFlow);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(printWriter!=null)
                    printWriter.close();
                if(bufferedWriter!=null)
                    bufferedWriter.close();
                if(fileWriter!=null)
                    fileWriter.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    //main function of the class
    public static void main(String args[]) throws Exception
    {
        BufferedReader input = null;
        try
        {
            if(args.length > 0)
            {
                input = new BufferedReader(new InputStreamReader(System.in));
                testffa.SimpleGraph G;
                G = new testffa.SimpleGraph();
                Hashtable hash = GraphInput.LoadSimpleGraph(G, args[0]);
                boolean check = false;
                do
                {
                    System.out.println("Enter the SOURCE (case-sensitive)");
                    source = input.readLine();
                    if(hash.keySet().contains(source))
                    {
                        check = true;
                    }
                    else
                    {
                        System.out.println("Enter a Valid SOURCE");
                    }
                }while(check==false);
                check = false;
                do
                {
                    System.out.println("Enter the SINK (case-sensitive)");
                    sink = input.readLine();
                    if(hash.keySet().contains(sink) && !source.equals(sink))
                    {
                        check = true;
                    }
                    else if(source.equalsIgnoreCase(sink))
                    {
                        System.out.println("SOURCE and SINK cannot be the same");
                        System.out.println("Enter a valid SINK");
                    }
                    else
                    {
                        System.out.println("Enter a valid SINK");
                    }
                }while(check==false);
                //System.out.println("Vertices :: "+ hash.keySet());

                long start = System.currentTimeMillis();
                double maxFlow = computeMaxFlow(hash);
                long end = System.currentTimeMillis() - start;
                System.out.println("The Max Flow for the given graph is :: "+maxFlow);
                System.out.println("Time taken :: "+end);

                String fileName = args[0];
                logToFile(end,fileName,maxFlow);
            }
            else
            {
                System.out.println("Input a file!");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(input!=null)
                    input.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
