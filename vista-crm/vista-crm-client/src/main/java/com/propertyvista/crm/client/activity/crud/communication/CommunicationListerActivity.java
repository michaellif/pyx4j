/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.communication;

import java.util.List;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.communication.listers.CommunicationListerView;
import com.propertyvista.crm.client.ui.crud.communication.listers.CommunicationListerViewAllMessage;
import com.propertyvista.crm.client.ui.crud.communication.listers.CommunicationListerViewDispatchQueue;
import com.propertyvista.crm.client.ui.crud.communication.listers.CommunicationListerViewMessageCategory;
import com.propertyvista.crm.client.ui.crud.communication.listers.CommunicationListerViewTicketCategory;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.CommunicationThreadDTO.ViewScope;

public class CommunicationListerActivity extends AbstractPrimeListerActivity<CommunicationThreadDTO> {

    public CommunicationListerActivity(AppPlace place) {
        super(CommunicationThreadDTO.class, place, getView(place));
    }

    private static IPrimeListerView<CommunicationThreadDTO> getView(AppPlace place) {
        if (place == null) {
            return CrmSite.getViewFactory().getView(CommunicationListerView.class);
        }

        List<String> args = place.getArg(CommunicationThreadDTO.ViewScope.class.getSimpleName());
        if (args == null || args.size() < 1) {
            return CrmSite.getViewFactory().getView(CommunicationListerView.class);
        }

        String scope = args.get(0);
        if (scope == null) {
            return CrmSite.getViewFactory().getView(CommunicationListerView.class);
        }

        switch (ViewScope.valueOf(scope)) {
        case DispatchQueue:
            return CrmSite.getViewFactory().getView(CommunicationListerViewDispatchQueue.class);
        case TicketCategory:
            return CrmSite.getViewFactory().getView(CommunicationListerViewTicketCategory.class);
        case MessageCategory:
            return CrmSite.getViewFactory().getView(CommunicationListerViewMessageCategory.class);
        default:
            return CrmSite.getViewFactory().getView(CommunicationListerViewAllMessage.class);
        }
    }
}
