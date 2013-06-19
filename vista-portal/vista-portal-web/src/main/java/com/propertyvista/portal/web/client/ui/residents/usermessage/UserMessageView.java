/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.usermessage;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.site.UserMessage;

public interface UserMessageView extends IsWidget {

    interface Presenter {

        void acceptMessage();

    }

    void setPresenter(Presenter presenter);

    void populate(UserMessage userMessage);

}
