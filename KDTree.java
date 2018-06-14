public class KdTree
{

    // helper data type representing a node of a kd-tree
    private static class KdNode
    {
        private KdNode        left;
        private KdNode        right;
        private final boolean vertical;
        private final double  x;
        private final double  y;

        public KdNode(final double x, final double y, final KdNode l,
                final KdNode r, final boolean v)
        {
            this.x = x;
            this.y = y;
            left  = l;
            right = r;
            vertical = v;
        }
    }

    private static final RectHV CONTAINER = new RectHV(0, 0, 1, 1);
    private KdNode root;
    private int    size;

    // construct an empty tree of points
    public KdTree()
    {
        size = 0;
        root = null;
    }

    // does the tree contain the point p?
    public boolean contains(final Point2D p)
    {
        return contains(root, p.x(), p.y());
    }

    // helper: does the subtree rooted at node contain (x, y)?
    private boolean contains(KdNode node, double x, double y)
    {
        if (node == null) return false;
        if (node.x == x && node.y == y) return true;

        if (node.vertical && x < node.x || !node.vertical && y < node.y)
            return contains(node.left, x, y);
        else
            return contains(node.right, x, y);
    }

    // draw all of the points to standard draw
    public void draw()
    {
        StdDraw.setScale(0, 1);

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        CONTAINER.draw();

        draw(root, CONTAINER);
    }

    // helper: draw node point and its division line (given by rect)
    private void draw(final KdNode node, final RectHV rect)
    {
        if (node == null) return;

        // draw the point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        new Point2D(node.x, node.y).draw();

        // get the min and max points of division line
        Point2D min, max;
        if (node.vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            min = new Point2D(node.x, rect.ymin());
            max = new Point2D(node.x, rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            min = new Point2D(rect.xmin(), node.y);
            max = new Point2D(rect.xmax(), node.y);
        }

        // draw that division line
        StdDraw.setPenRadius();
        min.drawTo(max);

        // recursively draw children
        draw(node.left, leftRect(rect, node));
        draw(node.right, rightRect(rect, node));
    }

    // helper: add point p to subtree rooted at node
    private KdNode insert(final KdNode node, final Point2D p,
            final boolean vertical)
    {
        // if new node, create it
        if (node == null) {
            size++;
            return new KdNode(p.x(), p.y(), null, null, vertical);
        }

        // if already in, return it
        if (node.x == p.x() && node.y == p.y()) return node;

        // else, insert it where corresponds (left - right recursive call)
        if (node.vertical && p.x() < node.x || !node.vertical && p.y() < node.y)
            node.left = insert(node.left, p, !node.vertical);
        else
            node.right = insert(node.right, p, !node.vertical);

        return node;
    }

    // add the point p to the tree (if it is not already in the tree)
    public void insert(final Point2D p)
    {
        root = insert(root, p, true);
    }

    // is the tree empty?
    public boolean isEmpty()
    {
        return size == 0;
    }

    // helper: get the left rectangle of node inside parent's rect
    private RectHV leftRect(final RectHV rect, final KdNode node)
    {
        if (node.vertical)
            return new RectHV(rect.xmin(), rect.ymin(), node.x, rect.ymax());
        else
            return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.y);
    }

    // helper: nearest neighbor of (x,y) in subtree rooted at node
    private Point2D nearest(final KdNode node, final RectHV rect,
            final double x, final double y, final Point2D candidate)
    {
        if (node == null) return candidate;

        double dqn = 0.0;
        double drq = 0.0;
        RectHV left = null;
        RectHV rigt = null;
        final Point2D query = new Point2D(x, y);
        Point2D nearest = candidate;

        if (nearest != null) {
            dqn = query.distanceSquaredTo(nearest);
            drq = rect.distanceSquaredTo(query);
        }

        if (nearest == null || dqn > drq) {
            final Point2D point = new Point2D(node.x, node.y);
            if (nearest == null || dqn > query.distanceSquaredTo(point))
                nearest = point;

            if (node.vertical) {
                left = new RectHV(rect.xmin(), rect.ymin(), node.x, rect.ymax());
                rigt = new RectHV(node.x, rect.ymin(), rect.xmax(), rect.ymax());

                if (x < node.x) {
                    nearest = nearest(node.left, left, x, y, nearest);
                    nearest = nearest(node.right, rigt, x, y, nearest);
                } else {
                    nearest = nearest(node.right, rigt, x, y, nearest);
                    nearest = nearest(node.left, left, x, y, nearest);
                }
            } else {
                left = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.y);
                rigt = new RectHV(rect.xmin(), node.y, rect.xmax(), rect.ymax());

                if (y < node.y) {
                    nearest = nearest(node.left, left, x, y, nearest);
                    nearest = nearest(node.right, rigt, x, y, nearest);
                } else {
                    nearest = nearest(node.right, rigt, x, y, nearest);
                    nearest = nearest(node.left, left, x, y, nearest);
                }
            }
        }

        return nearest;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(final Point2D p)
    {
        return nearest(root, CONTAINER, p.x(), p.y(), null);
    }

    // helper: points in subtree rooted at node inside rect
    private void range(final KdNode node, final RectHV nrect,
            final RectHV rect, final Queue<Point2D> queue)
    {
        if (node == null) return;

        if (rect.intersects(nrect)) {
            final Point2D p = new Point2D(node.x, node.y);
            if (rect.contains(p)) queue.enqueue(p);
            range(node.left, leftRect(nrect, node), rect, queue);
            range(node.right, rightRect(nrect, node), rect, queue);
        }
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(final RectHV rect)
    {
        final Queue<Point2D> queue = new Queue<Point2D>();
        range(root, CONTAINER, rect, queue);

        return queue;
    }

    // helper: get the right rectangle of node inside parent's rect
    private RectHV rightRect(final RectHV rect, final KdNode node)
    {
        if (node.vertical)
            return new RectHV(node.x, rect.ymin(), rect.xmax(), rect.ymax());
        else
            return new RectHV(rect.xmin(), node.y, rect.xmax(), rect.ymax());
    }

    // number of points in the tree
    public int size()
    {
        return size;
    }
