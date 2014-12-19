/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.shared.ui.communication;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService;

public interface MessageView extends IsView {

    interface Presenter {

        public MessagePortalCrudService getService();

    }

    void setPresenter(Presenter presenter);

    void populate();

}
