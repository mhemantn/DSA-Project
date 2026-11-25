
import java.awt.Point;
import java.util.LinkedList;

public class Snake {

    private LinkedList<Point> body;

    // Constructor
    public Snake() {
        body = new LinkedList<>();
        reset();
    }

    // Reset snake to starting position
    public void reset() {
        body.clear();

        body.add(new Point(10, 9));
        body.add(new Point(9, 9));
        body.add(new Point(8, 9));
    }

    // Return complete snake body
    public LinkedList<Point> getBody() {
        return body;
    }

    // Return snake head
    public Point getHead() {
        return body.getFirst();
    }

    // Add new head
    public void addHead(Point point) {
        body.addFirst(point);
    }

    // Remove tail
    public void removeTail() {
        body.removeLast();
    }

    // Check if position is occupied
    public boolean contains(Point point) {
        return body.contains(point);
    }
}
