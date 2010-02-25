/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 23, 2010
 * @author kaushansky
 * @version $Id$
 */
package com.pyx4j.geo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the article on google (describes the problem and sugested solution)
 * http://code.google.com/apis/maps/articles/geospatial.html
 * 
 * This is port of Python implementation http://code.google.com/p/geomodel/
 * 
 * <pre>
 * Defines the notion of 'geocells' and exposes methods to operate on them.
 * A geocell is a hexadecimal string that defines a two dimensional rectangular
 * region inside the [-90,90] x [-180,180] latitude/longitude space. A geocell's
 * 'resolution' is its length. For most practical purposes, at high resolutions,
 * geocells can be treated as single points.
 * 
 * Much like geohashes (see http://en.wikipedia.org/wiki/Geohash), geocells are
 * hierarchical, in that any prefix of a geocell is considered its ancestor, with
 * geocell[:-1] being geocell's immediate parent cell.
 * 
 * To calculate the rectangle of a given geocell string, first divide the
 * [-90,90] x [-180,180] latitude/longitude space evenly into a 4x4 grid like so:
 * 
 *              +---+---+---+---+ (90, 180)
 *              | a | b | e | f |
 *              +---+---+---+---+
 *              | 8 | 9 | c | d |
 *              +---+---+---+---+
 *              | 2 | 3 | 6 | 7 |
 *              +---+---+---+---+
 *              | 0 | 1 | 4 | 5 |
 *   (-90,-180) +---+---+---+---+
 * 
 * NOTE: The point (0, 0) is at the intersection of grid cells 3, 6, 9 and c. And,
 *       for example, cell 7 should be the sub-rectangle from
 *       (-45, 90) to (0, 180).
 * 
 * Calculate the sub-rectangle for the first character of the geocell string and
 * re-divide this sub-rectangle into another 4x4 grid. For example, if the geocell
 * string is '78a', we will re-divide the sub-rectangle like so:
 * 
 *                .                   .
 *                .                   .
 *            . . +----+----+----+----+ (0, 180)
 *                | 7a | 7b | 7e | 7f |
 *                +----+----+----+----+
 *                | 78 | 79 | 7c | 7d |
 *                +----+----+----+----+
 *                | 72 | 73 | 76 | 77 |
 *                +----+----+----+----+
 *                | 70 | 71 | 74 | 75 |
 *   . . (-45,90) +----+----+----+----+
 *                .                   .
 *                .                   .
 * 
 * Continue to re-divide into sub-rectangles and 4x4 grids until the entire
 * geocell string has been exhausted. The final sub-rectangle is the rectangular
 * region for the geocell.
 * </pre>
 */

public class GeoCell {

    public final static int GEOCELL_GRID_SIZE = 4;

    private final static String GEOCELL_ALPHABET = "0123456789abcdef";

    // The maximum *practical* geocell resolution.
    public final static int MAX_GEOCELL_RESOLUTION = 8;

    // The maximum number of geocells to consider for a bounding box search.
    public final static int MAX_FEASIBLE_BBOX_SEARCH_CELLS = 300;

    // Direction enumerations.
    public final static int[] NORTHWEST = new int[] { -1, 1 };

    public final static int[] NORTH = new int[] { 0, 1 };

    public final static int[] NORTHEAST = new int[] { 1, 1 };

    public final static int[] EAST = new int[] { 1, 0 };

    public final static int[] SOUTHEAST = new int[] { 1, -1 };

    public final static int[] SOUTH = new int[] { 0, -1 };

    public final static int[] SOUTHWEST = new int[] { -1, -1 };

    public final static int[] WEST = new int[] { -1, 0 };

    public static List<String> getBestCoveringSet(GeoBox box) {
        return getBestCoveringSet(box, new DefaultCostFunction());
    }

    public static List<String> getBestCoveringSet(GeoBox box, IAreaCostFunction costFunction) {
        String cellNe = compute(box.getNorthEast(), MAX_GEOCELL_RESOLUTION);
        String cellSw = compute(box.getSouthWest(), MAX_GEOCELL_RESOLUTION);
        double minCost = Double.MAX_VALUE;
        List<String> minCostCellSet = new ArrayList<String>();
        int minResolution = commonPrefixLength(cellSw, cellNe);
        for (int curResolution = minResolution; curResolution < MAX_GEOCELL_RESOLUTION + 1; curResolution++) {
            String curNe = cellNe.substring(0, curResolution);
            String curSw = cellSw.substring(0, curResolution);
            int numCells = interpolationCount(curNe, curSw);
            if (numCells > MAX_FEASIBLE_BBOX_SEARCH_CELLS) {
                continue;
            }
            List<String> cellSet = interpolate(curNe, curSw);
            Collections.sort(cellSet);
            double cost = costFunction.getCost(cellSet.size(), curResolution);
            System.out.println("trying resolution " + curResolution + " cost=" + cost);
            if (cost <= minCost) {
                minCost = cost;
                minCostCellSet = cellSet;
            } else {
                break;
            }
        }
        return minCostCellSet;
    }

    public static List<String> getBestCoveringSet(GeoCircle circle) {
        return getBestCoveringSet(circle, new DefaultCostFunction());
    }

    public static List<String> getBestCoveringSet(GeoCircle circle, IAreaCostFunction costFunction) {
        return getBestCoveringSet(circle.getMinBox(), costFunction);
    }

    public static int commonPrefixLength(String s1, String s2) {
        int minLength = Math.min(s1.length(), s2.length());
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return i;
            }
        }
        return minLength;
    }

    public static boolean collinear(String cell1, String cell2, boolean columnTest) {
        for (int i = 0; i < Math.min(cell1.length(), cell2.length()); i++) {
            int[] xy1 = subdivXY(cell1.charAt(i));
            int[] xy2 = subdivXY(cell2.charAt(i));
            if (!columnTest && xy1[1] != xy2[1]) {
                return false;
            }
            if (columnTest && xy1[0] != xy2[0]) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static List<String> interpolate(String cellNe, String cellSw) {
        List<List<String>> cellMap = new ArrayList<List<String>>();
        cellMap.add(new ArrayList<String>());
        cellMap.get(0).add(cellSw);
        while (!collinear((String) getLast(cellMap.get(0)), cellNe, true)) {
            String cellTmp = adjacent((String) getLast(cellMap.get(0)), EAST);
            if (cellTmp == null) {
                break;
            }
            cellMap.get(0).add(cellTmp);
        }
        while (!getLast((List<String>) getLast(cellMap)).equals(cellNe)) {

            List<String> cellTmpRow = new ArrayList<String>();
            for (String g : (List<String>) getLast(cellMap)) {
                cellTmpRow.add(adjacent(g, NORTH));
            }
            if (cellTmpRow.get(0) == null) {
                break;
            }
            cellMap.add(cellTmpRow);
        }
        List<String> cellSet = new ArrayList<String>();
        for (List<String> row : cellMap) {
            for (String g : row) {
                cellSet.add(g);
            }
        }
        return cellSet;
    }

    private static Object getLast(List<?> list) {
        return list.get(list.size() - 1);
    }

    public static int interpolationCount(String cellNe, String cellSw) {
        GeoBox boxNe = computeBox(cellNe);
        GeoBox boxSw = computeBox(cellSw);
        double cellLatSpan = boxSw.getNorth() - boxSw.getSouth();
        double cellLngSpan = boxSw.getEast() - boxSw.getWest();
        int numCols = (int) ((boxNe.getEast() - boxSw.getWest()) / cellLngSpan);
        int numRows = (int) ((boxNe.getNorth() - boxSw.getSouth()) / cellLatSpan);
        return numCols * numRows;
    }

    public static String[] allAdjacents(String cell) {
        String[] adjacents = new String[8];
        adjacents[0] = adjacent(cell, NORTHWEST);
        adjacents[1] = adjacent(cell, NORTH);
        adjacents[2] = adjacent(cell, NORTHEAST);
        adjacents[3] = adjacent(cell, EAST);
        adjacents[4] = adjacent(cell, SOUTHEAST);
        adjacents[5] = adjacent(cell, SOUTH);
        adjacents[6] = adjacent(cell, SOUTHWEST);
        adjacents[7] = adjacent(cell, WEST);
        return adjacents;
    }

    public static String adjacent(String cell, int[] dir) {
        if (cell == null) {
            return null;
        }
        int dx = dir[0];
        int dy = dir[1];

        char[] cellAdjArr = cell.toCharArray(); // Split the geocell string characters into a list.
        int i = cellAdjArr.length - 1;

        while (i >= 0 && (dx != 0 || dy != 0)) {
            int[] xy = subdivXY(cellAdjArr[i]);
            int x = xy[0];
            int y = xy[1];

            // Horizontal adjacency.
            if (dx == -1) { // Asking for left.
                if (x == 0) { // At left of parent cell.
                    x = GEOCELL_GRID_SIZE - 1; // Becomes right edge of adjacent parent.
                } else {
                    x -= 1; // Adjacent, same parent.
                    dx = 0; // Done with x.
                }
            } else if (dx == 1) { // Asking for right.
                if (x == GEOCELL_GRID_SIZE - 1) { // At right of parent cell.
                    x = 0; // Becomes left edge of adjacent parent.
                } else {
                    x += 1; // Adjacent, same parent.
                    dx = 0; // Done with x.
                }
            }

            // Vertical adjacency.
            if (dy == 1) { // Asking for above.
                if (y == GEOCELL_GRID_SIZE - 1) { // At top of parent cell.
                    y = 0; // Becomes bottom edge of adjacent parent.
                } else {
                    y += 1; // Adjacent, same parent.
                    dy = 0; // Done with y.
                }
            } else if (dy == -1) { // Asking for below.
                if (y == 0) { // At bottom of parent cell.
                    y = GEOCELL_GRID_SIZE - 1; // Becomes top edge of adjacent parent.
                } else {
                    y -= 1; // Adjacent, same parent.
                    dy = 0; // Done with y.
                }
            }

            cellAdjArr[i] = subdivChar(x, y);
            i -= 1;
        }

        // If we're not done with y then it's trying to wrap vertically,
        // which is a failure.
        if (dy != 0) {
            return null;
        }

        // At this point, horizontal wrapping is done inherently.
        return new String(cellAdjArr);
    }

    public static boolean containsPoint(String cell, GeoPoint point) {
        return compute(point, cell.length()).equals(cell);
    }

    public static String compute(GeoPoint point, int resolution) {
        double north = 90.0;
        double south = -90.0;
        double east = 180.0;
        double west = -180.0;
        StringBuilder cell = new StringBuilder("");
        while (cell.length() < resolution) {
            double subcellLngSpan = (east - west) / GEOCELL_GRID_SIZE;
            double subcellLatSpan = (north - south) / GEOCELL_GRID_SIZE;

            int x = (int) Math.min(GEOCELL_GRID_SIZE * (point.getLng() - west) / (east - west), GEOCELL_GRID_SIZE - 1);
            int y = (int) Math.min(GEOCELL_GRID_SIZE * (point.getLat() - south) / (north - south), GEOCELL_GRID_SIZE - 1);

            cell.append(subdivChar(x, y));

            south += subcellLatSpan * y;
            north = south + subcellLatSpan;

            west += subcellLngSpan * x;
            east = west + subcellLngSpan;
        }
        return cell.toString();
    }

    public static GeoBox computeBox(String cell) {
        if (cell == null) {
            return null;
        }
        GeoBox box = new GeoBox(90.0, 180.0, -90.0, -180.0);
        for (int i = 0; i < cell.length(); i++) {
            double subcellLngSpan = (box.getEast() - box.getWest()) / GEOCELL_GRID_SIZE;
            double subcellLatSpan = (box.getNorth() - box.getSouth()) / GEOCELL_GRID_SIZE;

            int[] xy = subdivXY(cell.charAt(i));

            box = new GeoBox(box.getSouth() + subcellLatSpan * (xy[1] + 1), box.getWest() + subcellLngSpan * (xy[0] + 1), box.getSouth() + subcellLatSpan
                    * xy[1], box.getWest() + subcellLngSpan * xy[0]);
        }
        return box;
    }

    public static boolean isValid(String cell) {
        if (cell == null || cell.length() == 0) {
            return false;
        }
        for (int i = 0; i < cell.length(); i++) {
            if (GEOCELL_ALPHABET.indexOf(cell.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    public static String[] children(String cell) {
        String[] c = new String[GEOCELL_ALPHABET.length()];
        for (int i = 0; i < c.length; i++) {
            c[i] = cell + GEOCELL_ALPHABET.charAt(i);
        }
        return c;
    }

    private static int[] subdivXY(char c) {
        int i = GEOCELL_ALPHABET.indexOf(c);
        return new int[] { (i & 4) >> 1 | (i & 1) >> 0, (i & 8) >> 2 | (i & 2) >> 1 };
    }

    private static char subdivChar(int x, int y) {
        return GEOCELL_ALPHABET.charAt((y & 2) << 2 | (x & 2) << 1 | (y & 1) << 1 | (x & 1) << 0);
    }

}
