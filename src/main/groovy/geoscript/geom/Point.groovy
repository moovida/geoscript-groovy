package geoscript.geom

import com.vividsolutions.jts.geom.Point as JtsPoint
import com.vividsolutions.jts.geom.Coordinate

/**
 * A Point Geometry.
 * <p>You can create a Point from xy coordinates:</p>
 * <p><blockquote><pre>
 * Point p = new Point(111, -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Point extends Geometry {
	
    /**
     * Create a Point by wrapping a JTS Point
     * @param The JTS Point
     */
    Point (JtsPoint p) {
        super(p)
    }
	
    /**
     * Create a Point with an x and y coordinate
     * <p><blockquote><pre>
     * def p = new Point(111,-47)
     * </pre></blockquote></p>
     * @param x The x coordinate
     * @param y The y coordinate
     */
    Point(double x, double y) {
        super(Geometry.factory.createPoint(new Coordinate(x,y)))
    }

    /**
     * Get the X coordinate
     * @return The X coordinate
     */
    double getX() {
        return g.x
    }

    /**
     * Get the Y coordinate
     * @return The Y coordinate
     */
    double getY() {
        return g.y
    }

    /**
     * Add this Point with another to create a MultiPoint.
     * <p><blockquote><pre>
     * def p = new Point(1,2)
     * def m = p + new Point(3,4)
     *
     * MULTIPOINT (1 2, 3 4)
     * </pre></blockquote></p>
     * @param point The other Point
     * @return A new MultiPoint containing this Point and the other Point
     */
    MultiPoint plus(Point point) {
        new MultiPoint(*[this, point])
    }
}