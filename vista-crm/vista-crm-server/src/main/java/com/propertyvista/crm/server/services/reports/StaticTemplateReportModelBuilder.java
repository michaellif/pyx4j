/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.report.JasperReportModel;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class StaticTemplateReportModelBuilder {

    private static final String REPORT_DESIGN_NAME_CLASSPATH_PREFIX = "reports";

    private final Class<? extends GadgetMetadata> gadgetMetadataClass;

    private final HashMap<String, Object> parameters;

    private final LinkedList<IEntity> data;

    public <M extends GadgetMetadata> StaticTemplateReportModelBuilder(Class<M> gadgetMedataClass) {
        this.gadgetMetadataClass = gadgetMedataClass;
        this.parameters = new HashMap<String, Object>();
        this.data = new LinkedList<IEntity>();
    }

    public StaticTemplateReportModelBuilder param(String paramName, Object paramValue) {
        parameters.put(paramName, paramValue);
        return this;
    }

    public StaticTemplateReportModelBuilder data(Iterator<? extends IEntity> dataIterator) {
        while (dataIterator.hasNext()) {
            data.add(dataIterator.next());
        }
        return this;
    }

    public JasperReportModel build() {
        return new JasperReportModel(designName(), data, parameters);
    }

    private String designName() {
        return REPORT_DESIGN_NAME_CLASSPATH_PREFIX + "." + gadgetMetadataClass.getSimpleName();
    }

}
