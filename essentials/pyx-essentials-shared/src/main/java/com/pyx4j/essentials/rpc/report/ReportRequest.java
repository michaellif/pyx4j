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
 * Created on 2010-05-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.shared.DownloadFormat;

@SuppressWarnings("serial")
public class ReportRequest implements Serializable {

    private DownloadFormat downloadFormat;

    private EntityQueryCriteria<?> criteria;

    private int timezoneOffset;

    private HashMap<String, Serializable> parameters;

    public ReportRequest() {

    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public DownloadFormat getDownloadFormat() {
        return downloadFormat;
    }

    public void setDownloadFormat(DownloadFormat downloadFormat) {
        this.downloadFormat = downloadFormat;
    }

    public EntityQueryCriteria<?> getCriteria() {
        return criteria;
    }

    public void setCriteria(EntityQueryCriteria<?> criteria) {
        this.criteria = criteria;
    }

    public Map<String, Serializable> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, Serializable> parameters) {
        this.parameters = parameters;
    }
}
