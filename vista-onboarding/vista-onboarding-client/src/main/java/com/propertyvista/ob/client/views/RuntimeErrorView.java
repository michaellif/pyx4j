/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.site.Notification;

public interface RuntimeErrorView extends IsWidget {

    interface Presenter {

        void acknowledgeError();

    }

    void setErrorMessage(Notification errorMessage);

    void setPresenter(Presenter presenter);

}
