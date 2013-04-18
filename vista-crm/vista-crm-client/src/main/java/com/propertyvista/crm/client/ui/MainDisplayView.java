/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

public interface MainDisplayView extends IsWidget {

    public interface MainDisplayPresenter {
    }

    public void setPresenter(MainDisplayPresenter presenter);

}