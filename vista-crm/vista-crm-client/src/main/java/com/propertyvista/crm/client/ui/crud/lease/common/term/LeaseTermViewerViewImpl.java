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
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.gwt.commons.Print;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.DefaultCrudPaneTheme;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.selections.version.LeaseTermVersionService;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Status;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseTermViewerViewImpl extends CrmViewerViewImplBase<LeaseTermDTO> implements LeaseTermViewerView {

    protected static final I18n i18n = I18n.get(LeaseTermViewerViewImpl.class);

    private Button offerAcceptButton;

    public LeaseTermViewerViewImpl() {
        setForm(new LeaseTermForm(this));
        enableVersioning(LeaseTerm.LeaseTermV.class, GWT.<LeaseTermVersionService> create(LeaseTermVersionService.class));

        if (!VistaFeatures.instance().yardiIntegration()) {
            addHeaderToolbarItem(new Button(i18n.tr("Charges"), new Command() {
                @Override
                public void execute() {
                    if (!isVisorShown()) {
                        ((LeaseTermViewerView.Presenter) getPresenter()).getChargesVisorController().show(LeaseTermViewerViewImpl.this);
                    }
                }
            }));
        }

        if (false) {
            addHeaderToolbarItem(new Button(i18n.tr("Print"), new Command() {
                @Override
                public void execute() {
//              Print.it(getForm().toStringForPrint());
                    Print.preview(getForm().toStringForPrint());
                }
            }));
        }

        addHeaderToolbarItem(offerAcceptButton = new Button(i18n.tr("Accept"), new Command() {
            @Override
            public void execute() {
                ((LeaseTermViewerView.Presenter) getPresenter()).accept();
            }
        }));
        offerAcceptButton.addStyleName(DefaultCrudPaneTheme.StyleName.HighlightedButton.name());
    }

    @Override
    public void populate(LeaseTermDTO value) {
        super.populate(value);

        setEditingVisible(!value.lease().status().getValue().isFormer() && value.status().getValue() != Status.AcceptedOffer);
        // correct finalization visibility for draft lease/applications
        setFinalizationVisible(isFinalizationVisible() && !value.lease().status().getValue().isDraft());

        offerAcceptButton.setVisible(value.status().getValue() == Status.Offer && !((IVersionedEntity<?>) value).version().versionNumber().isNull()
                && value.lease().nextTerm().isNull());
    }
}
