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
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.TickProducer;

public class GridBasedChartConfigurator extends BasicChartConfigurator {

    private final int width;

    private final int height;

    private GridType gridType;

    private String verticalAxisTitle;

    private String horisontalAxisTitle;

    private boolean zeroBased;

    public enum GridType {
        None, Both, Metric, Value
    }

    private TickProducer valueTickProducer;

    public GridBasedChartConfigurator(SvgFactory factory, DataSource datasource, int width, int height) {
        super(factory, datasource);
        //TODO validation
        //  assert width < 1;
        //  assert height < 1;
        gridType = GridType.Value;
        this.width = width;
        this.height = height;
        zeroBased = false;

    }

    public GridType getGridType() {
        return gridType;
    }

    public void setGridType(GridType gridType) {
        this.gridType = gridType;
    }

    public String getVerticalAxisTitle() {
        return verticalAxisTitle;
    }

    public void setVerticalAxisTitle(String verticalAxisTitle) {
        this.verticalAxisTitle = verticalAxisTitle;
    }

    public String getHorisontalAxisTitle() {
        return horisontalAxisTitle;
    }

    public void setHorisontalAxisTitle(String horisontalAxisTitle) {
        this.horisontalAxisTitle = horisontalAxisTitle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isZeroBased() {
        return zeroBased;
    }

    public void setZeroBased(boolean zeroBased) {
        this.zeroBased = zeroBased;
    }

    public TickProducer getValueTickProducer() {
        if (valueTickProducer == null) {
            return new BasicTickProducer();
        }
        return valueTickProducer;
    }

    public void setValueTickProducer(TickProducer valueTickProducer) {
        this.valueTickProducer = valueTickProducer;
    }
}
