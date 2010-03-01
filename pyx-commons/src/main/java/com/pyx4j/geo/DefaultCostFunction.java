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

public class DefaultCostFunction implements IAreaCostFunction {
    // corresponds to maximal resolution such that total number of cells doesn't exceed 16
    @Override
    public double getCost(int numCells, int resolution) {
        // TODO Auto-generated method stub
        int n = GeoCell.GEOCELL_GRID_SIZE;
        if (numCells > GeoCell.MAX_FEASIBLE_BBOX_SEARCH_CELLS / 2)
            return 1.0e10;
        return 0;

    }

}
