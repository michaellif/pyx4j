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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.dto.MessageDTO;

public class MessageListerViewImpl extends CrmListerViewImplBase<MessageDTO> implements MessageListerView {
    private static final I18n i18n = I18n.get(MessageListerViewImpl.class);

    public MessageListerViewImpl() {
        setLister(new MessageLister());
    }

    @Override
    public void setPresenter(ILister.Presenter<MessageDTO> presenter) {
        super.setPresenter(presenter);
        if (presenter != null && presenter.getPlace() != null) {
            AppPlace place = presenter.getPlace();
            String caption = null;
            Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
            if (placeCriteria != null) {
                MessageGroupCategory category = null;
                if (placeCriteria instanceof MessageGroupCategory) {
                    category = (MessageGroupCategory) placeCriteria;
                    if (category != null) {
                        switch (category) {
                        case Message:
                            caption = i18n.tr("Messages");
                            break;
                        case Ticket:
                            caption = i18n.tr("Tickets");
                            break;
                        case IVR:
                            caption = i18n.tr("IVRs");
                            break;
                        case Notification:
                            caption = i18n.tr("Notifications");
                            break;
                        case SMS:
                            caption = i18n.tr("SMSes");
                            break;
                        }
                    }
                } else {
                    MessageCategory mc = (MessageCategory) placeCriteria;
                    caption = mc.topic().getValue();
                }
            }
            if (caption != null) {
                setCaption(caption);
            }
        }
    }
}
