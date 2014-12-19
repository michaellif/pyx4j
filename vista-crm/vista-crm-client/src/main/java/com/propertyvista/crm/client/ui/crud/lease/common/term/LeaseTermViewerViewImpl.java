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
 */
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import java.math.BigDecimal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.selections.version.LeaseTermVersionService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Status;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseTermViewerViewImpl extends CrmViewerViewImplBase<LeaseTermDTO> implements LeaseTermViewerView {

    protected static final I18n i18n = I18n.get(LeaseTermViewerViewImpl.class);

    private Button offerAcceptButton;

    private final Button chargesButton;

    public LeaseTermViewerViewImpl() {
        setForm(new LeaseTermForm(this));
        enableVersioning(LeaseTerm.LeaseTermV.class, GWT.<LeaseTermVersionService> create(LeaseTermVersionService.class));

        chargesButton = new Button(i18n.tr("Charges"), new Command() {
            @Override
            public void execute() {
                ((LeaseTermViewerView.Presenter) getPresenter()).getChargesVisorController().show();
            }
        }, DataModelPermission.permissionRead(BillDataDTO.class));
        if (!VistaFeatures.instance().yardiIntegration()) {
            addHeaderToolbarItem(chargesButton);
        }

        if (false) {
            addHeaderToolbarItem(new Button(i18n.tr("Print"), new Command() {
                @Override
                public void execute() {
                    PrintUtils.print(getForm().getPrintableElement());
                }
            }));
        }

        if (VistaTODO.VISTA_1789_Renew_Lease) {
            addHeaderToolbarItem(offerAcceptButton = new Button(i18n.tr("Accept"), new Command() {
                @Override
                public void execute() {
                    ((LeaseTermViewerView.Presenter) getPresenter()).accept();
                }
            }));
            offerAcceptButton.addStyleName(PaneTheme.StyleName.HighlightedButton.name());
        }
    }

    @Override
    public void populate(LeaseTermDTO value) {
        super.populate(value);

        chargesButton.setVisible(!VersionedEntityUtils.isDraft(value) && value.status().getValue() == Status.Current
                && !value.lease().status().getValue().isFormer());

        if (value.lease().status().getValue() == Lease.Status.Application) {
            LeaseApplication.Status status = value.lease().leaseApplication().status().getValue();
            boolean isOnlineApplication = LeaseApplication.Status.isOnlineApplication(value.lease().leaseApplication());
            boolean noPtAppProgress = (value.masterApplicationStatus().progress().getValue(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) == 0);

            setEditingVisible(status.isDraft() && status != LeaseApplication.Status.PendingDecision && (!isOnlineApplication || noPtAppProgress));
            setFinalizationVisible(false);
        } else {
            boolean movedOutLease = !value.lease().actualMoveOut().isNull();

            setEditingVisible(!value.lease().status().getValue().isFormer() && !movedOutLease && value.status().getValue() != Status.Historic);
            setFinalizationVisible(isFinalizationVisible() && value.lease().status().getValue().isCurrent() && !movedOutLease);

            if (VistaTODO.VISTA_1789_Renew_Lease) {
                offerAcceptButton.setVisible(value.status().getValue() == Status.Offer && !((IVersionedEntity<?>) value).version().versionNumber().isNull()
                        && value.lease().nextTerm().isNull());
            }
        }
    }
}
