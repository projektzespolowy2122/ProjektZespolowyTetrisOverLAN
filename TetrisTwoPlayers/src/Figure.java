public class Figure {
    // constants for the starting coordinatesition for each piece
    private final Point[][] pointCoordinates = {{point(0, 4), point(0, 5), point(1, 4), point(1, 5)},
            {point(1, 3), point(1, 4), point(1, 5), point(1, 6)},
            {point(0, 3), point(1, 3), point(1, 4), point(1, 5)},
            {point(0, 5), point(1, 3), point(1, 4), point(1, 5)},
            {point(1, 3), point(1, 4), point(0, 4), point(0, 5)},
            {point(0, 3), point(0, 4), point(1, 4), point(1, 5)},
            {point(1, 3), point(1, 4), point(1, 5), point(0, 4)}};

    // auxiliary method to create a new Point object
    private Point point (int x, int y) {
        return new Point(x, y);
    }

    // returns a piece with a specific id
    public Active getActive (int id) {
        Point[] newPiece = new Point[4];
        for (int i = 0; i < 4; i++)
            newPiece[i] = new Point(pointCoordinates[id][i].x, pointCoordinates[id][i].y);
        return new Active(newPiece, id+1);
    }

    // generates a permutation of the seven pointCoordinates and returns it
    public int[] getPermutation () {
        int[] pointCoordinates = new int[7];
        for (int i = 0; i < 7; i++)
            pointCoordinates[i] = i;
        permute(0, pointCoordinates);
        return pointCoordinates;
    }

    // auxiliary function to permute the pointCoordinates
    private void permute (int i, int[] a) {
        if (i == 6)
            return;
        int swap = (int)(Math.random()*(6-i) + i + 1);
        int point = a[i];
        a[i] = a[swap];
        a[swap] = point;
        permute(i+1, a);
    }

    // represents the active piece
    static class Active {
        Point[] coordinates;
        int id;
        int widthX, heightX, widthY, heightY;
        int state = 0;
        Active (Point[] coordinates, int id) {
            this.coordinates = coordinates;
            this.id = id;
            if (id != 2) {
                widthX = 0; heightX = 2;
                widthY = 3; heightY = 5;
            } else {
                widthX = 0; heightX = 3;
                widthY = 3; heightY = 6;
            }
        }
    }
    // represents a point on the grid
    static class Point {
        int x, y;
        Point (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
