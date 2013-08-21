/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.eftvariance;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.site.client.ui.reports.ReportWidget;

public class EftVarianceReportWidget extends Composite implements ReportWidget {

    private HTML reportHtml;

    public EftVarianceReportWidget() {
        initWidget(reportHtml = new HTML());
    }

    @Override
    public void setData(Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getMemento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMemento(Object memento) {
        // TODO Auto-generated method stub       
    }

}
