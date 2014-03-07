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
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.communication;

import java.util.List;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister.ItemSelectionHandler;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;

import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class CommunicationMessageViewImpl extends SimplePanel implements CommunicationMessageView {

    private final static I18n i18n = I18n.get(CommunicationMessageViewImpl.class);

    private final CommunicationMessageLister lister;

    public CommunicationMessageViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        lister = new CommunicationMessageLister();
        CommunicationMessageGadget gadget = new CommunicationMessageGadget(lister);
        setWidget(gadget);

    }

    @Override
    public void setPresenter(Presenter presenter) {
    }

    @Override
    public void populate(List<CommunicationMessageDTO> messageChoices) {
        lister.setDataSource(new CommunicationMessageDataSource(messageChoices));
        lister.obtain(0);
    }

    class CommunicationMessageGadget extends AbstractGadget<CommunicationMessageViewImpl> {

        CommunicationMessageGadget(CommunicationMessageLister lister) {
            super(CommunicationMessageViewImpl.this, null, i18n.tr("Select a Communication Thread"), ThemeColor.foreground, 0.3);

            lister.setWidth("100%");

            lister.showColumnSelector(false);
            lister.addItemSelectionHandler(new ItemSelectionHandler<CommunicationMessageDTO>() {
                @Override
                public void onSelect(CommunicationMessageDTO selectedItem) {
                    AppSite.getPlaceController().goTo(
                            new ResidentPortalSiteMap.CommunicationMessage.CommunicationMessagePage(selectedItem.thread().getPrimaryKey()));

                }
            });
            setContent(lister);
        }

    }

    private static class CommunicationMessageLister extends EntityDataTablePanel<CommunicationMessageDTO> {

        public CommunicationMessageLister() {
            super(CommunicationMessageDTO.class, false, false);
            setSelectable(true);
            getDataTablePanel().setFilteringEnabled(false);
            setColumnDescriptors(new MemberColumnDescriptor.Builder(proto().isRead()).build(), new MemberColumnDescriptor.Builder(proto().subject()).build(),
                    new MemberColumnDescriptor.Builder(proto().date()).build());
        }
    }

    private static class CommunicationMessageDataSource extends ListerDataSource<CommunicationMessageDTO> {

        public CommunicationMessageDataSource(List<CommunicationMessageDTO> choices) {
            super(CommunicationMessageDTO.class, new InMemeoryListService<CommunicationMessageDTO>(choices));
        }

    }
}
