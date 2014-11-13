/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 31, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.activity.AbstractVisorController;
import com.pyx4j.site.client.backoffice.ui.prime.form.IViewerView;
import com.pyx4j.site.client.ui.visor.AbstractVisorPaneView;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.MessageDTO;

public class MessageReportDialog extends AbstractVisorController {
    private static final I18n i18n = I18n.get(MessageReportDialog.class);

    private final VisorMessageLister lister;

    private final AbstractVisorPaneView entityListVisorView;

    public MessageReportDialog(IViewerView<?> parentView, List<LeaseParticipant<?>> recipientScope) {
        super(parentView);

        lister = new VisorMessageLister(parentView.getPresenter(), recipientScope);

        // add control buttons

        entityListVisorView = new AbstractVisorPaneView(this) {
            {
                // initialize
                setCaption(i18n.tr("Communication Report"));
                setContentPane(new ScrollPanel(lister.asWidget()));
                getElement().getStyle().setProperty("padding", "6px");
            }
        };
        // add OK button control

        lister.getDataTable().addItemSelectionHandler(new ItemSelectionHandler() {
            @Override
            public void onChange() {
                if (lister.getDataTable().getDataTableModel().isAnyRowSelected()) {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message();
                    place.setType(Type.viewer);
                    AppSite.getPlaceController().goTo(place.formViewerPlace(getSelectedItem().getPrimaryKey()));
                }
            }
        });
    }

    protected Collection<MessageDTO> getSelectedItems() {
        return lister.getSelectedItems();
    }

    protected MessageDTO getSelectedItem() {
        if (getSelectedItems().size() == 1) {
            return getSelectedItems().iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public void show() {
        lister.populate();
        getParentView().showVisor(entityListVisorView);
    }
}