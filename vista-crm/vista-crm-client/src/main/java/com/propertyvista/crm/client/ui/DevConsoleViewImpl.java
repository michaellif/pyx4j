/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author vlads
 */
package com.propertyvista.crm.client.ui;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.devconsole.AbstractDevConsole;
import com.pyx4j.widgets.client.Anchor;

public class DevConsoleViewImpl extends AbstractDevConsole implements DevConsoleView {

    public DevConsoleViewImpl() {
        add(new SetMocksButton());
        add(new Anchor("Resident TODO"));
        add(new Anchor("Prospect TODO"));
        add(new Anchor("Site TODO"));
    }

    @Override
    protected void setMockValues() {
        setMockValues(((CrmRootPane) AppSite.instance().getRootPane()).asWidget());
    }

}
