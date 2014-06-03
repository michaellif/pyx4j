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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister.ItemSelectionHandler;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class MessageViewImpl extends SimplePanel implements MessageView {

    private final static I18n i18n = I18n.get(MessageViewImpl.class);

    private final CommunicationMessageLister lister;

    private Presenter presenter;

    public MessageViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        lister = new CommunicationMessageLister();
        CommunicationMessageGadget gadget = new CommunicationMessageGadget(lister);

        setWidget(gadget);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate() {
        lister.setDataSource(new ListerDataSource<MessageDTO>(MessageDTO.class, presenter.getService()));
        lister.obtain(0);
    }

    class CommunicationMessageGadget extends AbstractGadget<MessageViewImpl> {

        CommunicationMessageGadget(CommunicationMessageLister lister) {
            super(MessageViewImpl.this, null, i18n.tr("Tenant Communication"), ThemeColor.foreground, 0.3);
            lister.setWidth("100%");
            lister.setSelectable(true);
            lister.showColumnSelector(true);

            lister.addItemSelectionHandler(new ItemSelectionHandler<MessageDTO>() {
                @Override
                public void onSelect(MessageDTO selectedItem) {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Message.MessagePage(selectedItem.getPrimaryKey()));

                }
            });

            setContent(lister);
        }

    }

    private static class CommunicationMessageLister extends EntityDataTablePanel<MessageDTO> {

        public CommunicationMessageLister() {
            super(MessageDTO.class, false, false);
            getDataTablePanel().setFilteringEnabled(false);
            // No filtering work for it
            getDataTablePanel().getDataTable().setHasColumnClickSorting(false);
            setColumnDescriptors(new MemberColumnDescriptor.Builder(proto().isRead()).build(),
                    new MemberColumnDescriptor.Builder(proto().highImportance()).build(), new MemberColumnDescriptor.Builder(proto().star()).build(),
                    new MemberColumnDescriptor.Builder(proto().sender()).build(), new MemberColumnDescriptor.Builder(proto().subject()).build(),
                    new MemberColumnDescriptor.Builder(proto().date()).build());
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().date(), true), new Sort(proto().isRead(), false), new Sort(proto().highImportance(), true));
        }
    }
}
