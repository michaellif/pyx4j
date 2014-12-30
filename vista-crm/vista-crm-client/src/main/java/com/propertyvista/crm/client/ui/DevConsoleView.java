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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.IsView;

import com.propertyvista.shared.rpc.DevConsoleDataTO;

public interface DevConsoleView extends IsWidget, IsView {

    public interface DevConsolePresenter {
    }

    public void setData(DevConsoleDataTO data);
}
