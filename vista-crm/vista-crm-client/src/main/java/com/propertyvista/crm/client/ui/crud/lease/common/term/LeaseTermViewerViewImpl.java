/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.gwt.commons.Print;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.version.LeaseTermVersionService;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Status;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermViewerViewImpl extends CrmViewerViewImplBase<LeaseTermDTO> implements LeaseTermViewerView {

    protected static final I18n i18n = I18n.get(LeaseTermViewerViewImpl.class);

    private Button offerAcceptButton;

    public LeaseTermViewerViewImpl() {
        super(CrmSiteMap.Tenants.LeaseTerm.class, new LeaseTermForm(true));
        enableVersioning(LeaseTerm.LeaseTermV.class, GWT.<LeaseTermVersionService> create(LeaseTermVersionService.class));

        addHeaderToolbarItem(new Button(i18n.tr("Charges"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseTermViewerView.Presenter) getPresenter()).getChargesVisorController().show(LeaseTermViewerViewImpl.this);
            }
        }));

        addHeaderToolbarItem(new Button(i18n.tr("Print"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Print.it(getForm().toStringForPrint());
            }
        }));

        addHeaderToolbarItem(offerAcceptButton = new Button(i18n.tr("Accept"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseTermViewerView.Presenter) getPresenter()).accept();
            }
        }));
    }

    @Override
    public void populate(LeaseTermDTO value) {
        super.populate(value);

        setEditingVisible(!value.lease().status().getValue().isFormer() && value.status().getValue() != Status.AcceptedOffer);

        offerAcceptButton.setVisible(value.status().getValue() == Status.Offer && !((IVersionedEntity<?>) value).version().versionNumber().isNull()
                && value.lease().futureTerm().isNull());
    }
}
