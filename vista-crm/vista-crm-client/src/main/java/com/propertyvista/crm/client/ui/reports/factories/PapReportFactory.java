/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.factories;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.client.ui.reports.ReportFactory;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.reports.factories.pad.PadReportForm;
import com.propertyvista.domain.reports.PapReportMetadata;

public class PapReportFactory implements ReportFactory<PapReportMetadata> {

    @Override
    public CEntityForm<PapReportMetadata> getReportSettingsForm() {
        CEntityDecoratableForm<PapReportMetadata> form = new PadReportForm();
        form.initContent();
        return form;
    }

    @Override
    public Report getReport() {
        return new PapReport();
    }

}
