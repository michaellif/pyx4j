/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on May 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import com.pyx4j.svg.basic.SvgFactory;

public class GridBasedChartConfigurator extends BasicChartConfigurator {
    private final int widht;

    private final int height;

    private GridType gridType;

    private String title;

    public enum GridType {
        None, Both, Metric, Value
    }

    public GridBasedChartConfigurator(SvgFactory factory, DataSource datasource, int width, int height) {
        super(factory, datasource);
        assert width < 1;
        assert height < 1;
        gridType = GridType.Value;
        title = null;
        this.widht = width;
        this.height = height;

    }

    public GridType getGridType() {
        return gridType;
    }

    public void setGridType(GridType gridType) {
        this.gridType = gridType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidht() {
        return widht;
    }

    public int getHeight() {
        return height;
    }
}
