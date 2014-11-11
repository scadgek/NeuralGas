/**
 * Created by Scadgek on 11/3/2014.
 */
public class Connection {
    private Node nodeFrom;
    private Node nodeTo;
    private int age;

    public Connection() {
        age = 0;
    }

    public Node getNodeFrom() {
        return nodeFrom;
    }

    public void setNodeFrom(Node nodeFrom) {
        this.nodeFrom = nodeFrom;
    }

    public Node getNodeTo() {
        return nodeTo;
    }

    public void setNodeTo(Node nodeTo) {
        this.nodeTo = nodeTo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
