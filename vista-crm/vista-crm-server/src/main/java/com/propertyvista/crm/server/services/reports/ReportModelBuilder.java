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

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ReportModelBuilder<M extends GadgetMetadata> {

    private static final String REPORT_DESIGN_NAME_CLASSPATH_PREFIX = "reports";

    private final List<IEntity> data;

    private final Map<String, Object> parameters;

    private String template = null;

    private final Class<M> gadgetMetadataClass;

    public ReportModelBuilder(Class<M> gadgetMedataClass) {
        this.data = new LinkedList<IEntity>();
        this.parameters = new HashMap<String, Object>();
        this.gadgetMetadataClass = gadgetMedataClass;
    }

    public ReportModelBuilder<M> param(String paramName, Object paramValue) {
        parameters.put(paramName, paramValue);
        return this;
    }

    public ReportModelBuilder<M> reportData(Iterator<? extends IEntity> reportDataIterator) {
        while (reportDataIterator.hasNext()) {
            this.data.add(reportDataIterator.next());
        }
        return this;
    }

    public ReportModelBuilder<M> template(String template) {
        this.template = template;
        return this;
    }

    public JasperReportModel build() {
        if (this.template == null) {
            return new JasperReportModel(designName(), data, parameters);
        } else {
            return new JasperReportModel(template, data, parameters, template);
        }
    }

    private String designName() {
        return REPORT_DESIGN_NAME_CLASSPATH_PREFIX + "." + gadgetMetadataClass.getSimpleName();
    }

}
