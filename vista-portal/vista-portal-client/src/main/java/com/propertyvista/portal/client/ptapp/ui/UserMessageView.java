/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface UserMessageView extends IsWidget {

    public void setPresenter(Presenter presenter);

    void showNotes(List<String> messages);

    void showErrors(List<String> messages);

    void showFailures(List<String> messages);

    public interface Presenter {

    }

}