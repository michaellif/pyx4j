/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.field.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.field.rpc.ScreenMode.ScreenLayout;

public interface ScreenViewer extends IsWidget {

    DisplayPanel getHeaderDisplay();

    DisplayPanel getListerDisplay();

    DisplayPanel getDetailsDisplay();

    DisplayPanel getFullScreenDisplay();

    void setPresenter(Presenter presenter);

    public interface Presenter {
    }

    void setWidget(IsWidget widget);

    void setScreenLayout(ScreenLayout layout);

}
