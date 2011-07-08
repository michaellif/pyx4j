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
 * Created on 2011-03-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.report;

import java.util.List;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;

public class JasperReportModel {

    private final String designName;

    private final List<? extends IEntity> data;

    private final Map<String, Object> parameters;

    private final String jrXml; // in memory Jasper report XML.

    public JasperReportModel(String designName, List<? extends IEntity> data, Map<String, Object> parameters) {
        this(designName, data, parameters, null);
    }

    public JasperReportModel(String designName, List<? extends IEntity> data, Map<String, Object> parameters, String jrXml) {
        this.designName = designName;
        this.data = data;
        this.parameters = parameters;
        this.jrXml = jrXml;
    }

    public String getDesignName() {
        return designName;
    }

    public List<? extends IEntity> getData() {
        return data;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getJrXml() {
        return jrXml;
    }
}
