import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Scadgek on 11/4/2014.
 */
public class Network {
    private List<Node> nodes;
    private List<Connection> connections;
    private PrintWriter pw;

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    private Config config;

    public Network(Config config) throws FileNotFoundException {
        this.config = config;

        nodes = new ArrayList<>();

        nodes.add(new Node() {
            {
                setWeights(new double[]{new Random().nextDouble(), new Random().nextDouble(), new Random().nextDouble()});
                setLocalError(0);
            }
        });

        nodes.add(new Node() {
            {
                setWeights(new double[]{new Random().nextDouble(), new Random().nextDouble(), new Random().nextDouble()});
                setLocalError(0);
            }
        });

        connections = new ArrayList<>();

        pw = new PrintWriter("resources/output.txt");

        for (Node node : nodes)
        {
            pw.println(node.getWeights()[0] + " " + node.getWeights()[1] + " " + node.getWeights()[2] + " black");
        }
        pw.println("*");
    }

    public void learn(Dataset dataset) {
        int iteration = 0;
        while (dataset.hasNext()) {
            //get next learning row
            double[] x = dataset.next();

            //find closest nodes
            double minDist = Double.MAX_VALUE;
            Node closestNode = nodes.iterator().next();
            Node nextClosestNode = nodes.iterator().next();
            for (Node node : nodes) {
                if (node.distanceTo(x) < minDist) {
                    nextClosestNode = closestNode;
                    closestNode = node;
                }
            }

            //update local error of the "winner"
            closestNode.setLocalError(closestNode.getLocalError() + closestNode.distanceTo(x));

            //update weights for "winner"
            closestNode.getWeights()[0] += config.getEpsW() * (closestNode.getWeights()[0] - x[0]);
            closestNode.getWeights()[1] += config.getEpsW() * (closestNode.getWeights()[1] - x[1]);
            closestNode.getWeights()[2] += config.getEpsW() * (closestNode.getWeights()[2] - x[2]);

            //update weights for "winner"'s neighbours
            for (Connection connection : connections) {
                Node toBeUpdated = null;
                if (connection.getNodeFrom() == closestNode) {
                    toBeUpdated = connection.getNodeFrom();
                } else if (connection.getNodeTo() == closestNode) {
                    toBeUpdated = connection.getNodeTo();
                }

                if (toBeUpdated != null) {
                    toBeUpdated.getWeights()[0] += config.getEpsN() * (toBeUpdated.getWeights()[0] - x[0]);
                    toBeUpdated.getWeights()[1] += config.getEpsN() * (toBeUpdated.getWeights()[1] - x[1]);
                    toBeUpdated.getWeights()[2] += config.getEpsN() * (toBeUpdated.getWeights()[2] - x[2]);

                    connection.setAge(connection.getAge() + 1);
                }
            }

            //set connection between "winners" to zero
            boolean found = false;
            for (Connection connection : connections) {
                if (connection.getNodeTo() == closestNode && connection.getNodeFrom() == nextClosestNode || connection.getNodeFrom() == closestNode && connection.getNodeTo() == nextClosestNode) {
                    found = true;
                    connection.setAge(0);
                    break;
                }
            }

            //if connection didn't exist - create it
            if (!found) {
                Connection connection = new Connection();
                connection.setNodeTo(nextClosestNode);
                connection.setNodeFrom(closestNode);
                connections.add(connection);
            }

            //remove connections with age > maxAge
            int i = 0;
            while (i < connections.size()) {
                if (connections.get(i).getAge() > config.getMaxAge()) {
                    connections.remove(i);
                } else i++;
            }

            //if iteration is lambda
            if (iteration != 0 && iteration % config.getLambda()==0) {

                //find node with maximal local error
                double maxError = -Double.MAX_VALUE;
                Node maxErrorNode = null;
                for (Node node : nodes) {
                    if (node.getLocalError() > maxError) {
                        maxErrorNode = node;
                        break;
                    }
                }

                //find max error neighbour of the mentioned node
                maxError = -Double.MAX_VALUE;
                Node maxErrorNeighbour = null;
                for (Connection connection : connections) {

                    if (connection.getNodeTo() == maxErrorNode) {
                        if (connection.getNodeFrom().getLocalError() > maxError) {
                            maxErrorNeighbour = connection.getNodeFrom();
                        }
                    } else if (connection.getNodeFrom() == maxErrorNode) {
                        if (connection.getNodeTo().getLocalError() > maxError) {
                            maxErrorNeighbour = connection.getNodeTo();
                        }
                    }
                }

                //remove connection between them
                for (int k = 0; k < connections.size(); k++) {
                    if (connections.get(k).getNodeTo() == maxErrorNeighbour && connections.get(k).getNodeFrom() == maxErrorNode) {
                        connections.remove(k);
                        break;
                    }

                    if (connections.get(k).getNodeFrom() == maxErrorNeighbour && connections.get(k).getNodeTo() == maxErrorNode) {
                        connections.remove(k);
                        break;
                    }
                }

                if (maxErrorNeighbour == null || maxErrorNode == null)
                {
                    System.out.println("SOMETHING WENT WRONG");
                    break;
                }

                //create node between errorest nodes
                Node newNode = new Node();
                newNode.setLocalError(maxErrorNode.getLocalError());
                double x1 = (maxErrorNode.getWeights()[0] + maxErrorNeighbour.getWeights()[0]) / 2;
                double x2 = (maxErrorNode.getWeights()[1] + maxErrorNeighbour.getWeights()[1]) / 2;
                double x3 = (maxErrorNode.getWeights()[2] + maxErrorNeighbour.getWeights()[2]) / 2;
                newNode.setWeights(new double[]{x1, x2, x3});

                nodes.add(newNode);

                //create connections between them
                Connection conn1 = new Connection();
                conn1.setNodeFrom(maxErrorNode);
                conn1.setNodeTo(newNode);

                connections.add(conn1);

                Connection conn2 = new Connection();
                conn2.setNodeFrom(newNode);
                conn2.setNodeTo(maxErrorNeighbour);

                connections.add(conn2);

                //update errors of the error nodes
                maxErrorNode.setLocalError(maxErrorNode.getLocalError() * config.getAlpha());
                maxErrorNeighbour.setLocalError(maxErrorNeighbour.getLocalError() * config.getAlpha());
            }

            //update errors of the nodes
            for (Node node : nodes) {
                node.setLocalError(node.getLocalError() - node.getLocalError() * config.getBeta());
            }

            //save positions
            for (Node node : nodes)
            {
                pw.println(node.getWeights()[0] + " " + node.getWeights()[1] + " " + node.getWeights()[2] + " black");
            }
            pw.println("*");


            System.out.println("Iteration: " + iteration++);
        }

        pw.close();
    }
}
