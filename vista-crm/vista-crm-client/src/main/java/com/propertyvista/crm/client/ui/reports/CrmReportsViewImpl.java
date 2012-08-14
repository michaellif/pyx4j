/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.site.client.ui.reports.AbstractReportsView;
import com.pyx4j.site.client.ui.reports.ReportFactory;

import com.propertyvista.crm.client.ui.reports.factories.AvailabilityReportFactory;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class CrmReportsViewImpl extends AbstractReportsView implements CrmReportsView {

    private static Map<Class<? extends ReportMetadata>, ReportFactory<?>> factoryMap;

    static {
        factoryMap = new HashMap<Class<? extends ReportMetadata>, ReportFactory<?>>();

        factoryMap.put(AvailabilityReportMetadata.class, new AvailabilityReportFactory());
    }

    public CrmReportsViewImpl() {
        super(factoryMap);
    }

    @Override
    public IMemento getMemento() {
        return null;
    }

    @Override
    public void storeState(Place place) {

    }

    @Override
    public void restoreState() {

    }

}
