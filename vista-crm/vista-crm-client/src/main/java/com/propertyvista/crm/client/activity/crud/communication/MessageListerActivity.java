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
import com.propertyvista.crm.client.ui.crud.communication.listers.MessageListerView;
import com.propertyvista.crm.client.ui.crud.communication.listers.MessageListerViewAllMessage;
import com.propertyvista.crm.client.ui.crud.communication.listers.MessageListerViewDispatchQueue;
import com.propertyvista.crm.client.ui.crud.communication.listers.MessageListerViewMessageCategory;
import com.propertyvista.crm.client.ui.crud.communication.listers.MessageListerViewTicketCategory;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.dto.MessageDTO.ViewScope;

public class MessageListerActivity extends AbstractPrimeListerActivity<MessageDTO> {

    public MessageListerActivity(AppPlace place) {
        super(MessageDTO.class, place, getView(place));
    }

    private static IPrimeListerView<MessageDTO> getView(AppPlace place) {
        if (place == null) {
            return CrmSite.getViewFactory().getView(MessageListerView.class);
        }

        List<String> args = place.getArg(MessageDTO.ViewScope.class.getSimpleName());
        if (args == null || args.size() < 1) {
            return CrmSite.getViewFactory().getView(MessageListerView.class);
        }

        String scope = args.get(0);
        if (scope == null) {
            return CrmSite.getViewFactory().getView(MessageListerView.class);
        }

        switch (ViewScope.valueOf(scope)) {
        case DispatchQueue:
            return CrmSite.getViewFactory().getView(MessageListerViewDispatchQueue.class);
        case TicketCategory:
            return CrmSite.getViewFactory().getView(MessageListerViewTicketCategory.class);
        case MessageCategory:
            return CrmSite.getViewFactory().getView(MessageListerViewMessageCategory.class);
        default:
            return CrmSite.getViewFactory().getView(MessageListerViewAllMessage.class);
        }
    }
}
