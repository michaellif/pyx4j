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
/**
 * <pre>
 * package provides classes to facilitate search based on geographic location.
 * This is done by partitioning the globe into cells on different resolution levels.
 * Resolution level 1 corresponds to 16 "squares" for the whole globe
 * Resolution level 2 corresponds to 64 squares (obtained by partitioning each big square into 4 smaller squares).
 * Maximal resolution level is 8 (constant GeoCell.MAX_GEOCELL_RESOLUTION)
 * Each cell has an id (hexadecimal string), the length of which is equal to resolution.
 * All children of a given cell inherit its id as a prefix, e.g. cell A070 belongs to a bigger cell
 * A07, which belongs to even bigger cell A0, etc. See details in GeoCell.java
 * Therefore, each point on the globe belongs to a "Russian doll" of 8 cells of progressively bigger sizes.
 * Class GeoPoint represents a point. You can extract ids of all cells where this point belongs by calling
 * method getCells(). When you store locations in database, you have to store all cell ids along with coordinates
 * to make search possible.
 * We assume that database search is done either by geographic "box" (specified by NE and SW corners),
 * or by geographic circle (specified by point and radius). Box is represented by class GeoBox, circle- by
 * class GeoCircle.
 * To find all locations inside GeoBox, call static method getBestCoveringSet (in GeoCell class).
 * It will return the set of cell ids. For search, match this set against the set of cell ids stored along with location.
 * If any id from the former set matches any id from the latter set, the point *MAY* belong to the box.
 * However, because the covering set of cells can be "bigger" than you box, you have to check each candidate
 * location via method box.contains(candidatePoint) to filter out locations outside of actual box.
 * Treatment of GeoCircle is similar to GeoBox.
 * </pre>
 */
package com.pyx4j.geo;