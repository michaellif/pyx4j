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
package com.pyx4j.test.geo;

import java.util.List;

import junit.framework.TestCase;

import com.pyx4j.geo.GeoCell;
import com.pyx4j.geo.GeoPoint;

public class GeoTest extends TestCase {
    // to retrieve coordinates from google map, ensure that your point is in the center
    // (right click brings up the option to place it in the center),
    // then copy the following command into browser bar:
    // javascript:void(prompt('',gApplication.getMap().getCenter()));
    // it will show a dialog with coordinates
    public static TestLocation HOME = new TestLocation("home:", 43.782791236438456, -79.43077683448792);

    public static TestLocation HD = new TestLocation("hilda/drewry", 43.785579687188275, -79.42411422729492);

    public static TestLocation YD = new TestLocation("yonge/drewry", 43.7870590624149, -79.4173389673233);

    public static TestLocation BD = new TestLocation("bathurst/drewry:", 43.7810793730743, -79.44485306739807);

    public static TestLocation GF = new TestLocation("grantbrook/finch", 43.77612941093768, -79.4316565990448);

    public static TestLocation SC = new TestLocation("steeles/cactus", 43.7943005018426, -79.43651676177979);

    public static TestLocation BC = new TestLocation("bathurst/crestwood", 43.79519884384469, -79.44647312164307);

    public static TestLocation YS = new TestLocation("yonge/steeles", 43.79776988597209, -79.42003726959229);

    public static TestLocation YF = new TestLocation("yonge/finch", 43.779630835019404, -79.4156813621521);

    public static TestLocation BF = new TestLocation("bathurst/finch", 43.77382858481568, -79.44297552108765);

    public static TestLocation[] mapPoint = { HOME, HD, YD, BD, GF, SC, BC, YS, YF, BF };

    public boolean isInside(GeoPoint point, List<String> cells) {
        for (String cell : cells) {
            if (GeoCell.containsPoint(cell, HOME.point)) {
                return true;
            }
        }
        return false;
    }

    public void testBox() {
        /*
         * GeoBox box = new GeoBox(YS.point, BF.point); List<String> list =
         * GeoCell.getBestCoveringSet(box, new DefaultCostFunction());
         * System.out.println("number of cells:" + list.size());
         * System.out.println(list.toString()); boolean inside = isInside(HOME.point,
         * list); System.out.println("inside=" + inside);
         * System.out.println("box contains=" + box.contains(HD.point));
         * 
         * GeoCircle circle = new GeoCircle(HOME.point, 700.0);
         * System.out.println("circle contains " + circle.contains(HD.point));
         * System.out.println("distance to hilda=" + GeoUtils.distance(HOME.point,
         * HD.point));
         */
        /*
         * list = GeoCell.getBestCoveringSet(circle, new DefaultCostFunction());
         * System.out.println("number of cells:" + list.size());
         * System.out.println(list.toString());
         */

    }

}

class TestLocation {
    GeoPoint point;

    String address;

    TestLocation(String address, double lat, double lng) {
        this.point = new GeoPoint(lat, lng);
        this.address = address;
    }
}
