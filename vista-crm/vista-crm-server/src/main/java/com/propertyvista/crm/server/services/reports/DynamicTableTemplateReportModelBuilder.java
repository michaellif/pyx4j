/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.report.JasperReportModel;

public class DynamicTableTemplateReportModelBuilder {

    private final List<IEntity> data;

    private final Map<String, Object> parameters;

    private String template = null;

    public DynamicTableTemplateReportModelBuilder() {
        this.data = new LinkedList<IEntity>();
        this.parameters = new HashMap<String, Object>();
    }

    public DynamicTableTemplateReportModelBuilder param(String paramName, Object paramValue) {
        parameters.put(paramName, paramValue);
        return this;
    }

    public DynamicTableTemplateReportModelBuilder data(Iterator<? extends IEntity> reportDataIterator) {
        while (reportDataIterator.hasNext()) {
            this.data.add(reportDataIterator.next());
        }
        return this;
    }

    public DynamicTableTemplateReportModelBuilder template(String template) {
        this.template = template;
        return this;
    }

    public JasperReportModel build() {
        return new JasperReportModel(template, data, parameters, template);
    }
}
