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
 * Created on May 7, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataSource {

    private Map<Metric, List<Double>> dataset;

    private List<String> seriesDescription;

    public DataSource() {
        dataset = new LinkedHashMap<DataSource.Metric, List<Double>>(5);
        seriesDescription = new ArrayList<String>(5);
    }

    public Map<Metric, List<Double>> getDataSet() {
        return dataset;
    }

    public void setDataset(Map<Metric, List<Double>> dataset) {
        if (dataset == null)
            this.dataset = new LinkedHashMap<DataSource.Metric, List<Double>>(5);
        else
            this.dataset = dataset;
    }

    public void addDataSet(Metric metric, List<Double> values) {
        this.dataset.put(metric, values);
    }

    public List<String> getSeriesDescription() {
        return seriesDescription;
    }

    public void setSeriesDescription(List<String> seriesDescription) {
        this.seriesDescription = seriesDescription;
    }
    
    public void addSeriesDescription(String seriesDescription) {
        this.seriesDescription.add(seriesDescription);
    }

    public class Metric {
        private final String caption;

        public Metric(String caption) {
            this.caption = caption;
        }

        public String getCaption() {
            return caption;
        }

    }

}
