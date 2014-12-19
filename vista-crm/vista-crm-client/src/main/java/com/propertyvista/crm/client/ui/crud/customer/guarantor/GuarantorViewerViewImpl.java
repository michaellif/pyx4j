/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.customer.guarantor;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.widgets.client.Button.SecureMenuItem;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.customer.ac.GuarantorChangePassword;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class GuarantorViewerViewImpl extends CrmViewerViewImplBase<GuarantorDTO> implements GuarantorViewerView {

    private final static I18n i18n = I18n.get(GuarantorViewerViewImpl.class);

    private final MenuItem screeningView;

    private final MenuItem passwordAction;

    public GuarantorViewerViewImpl() {
        setForm(new GuarantorForm(this));

        // Views:
        screeningView = new SecureMenuItem(i18n.tr("Screening"), new Command() {
            @Override
            public void execute() {
                ((GuarantorViewerView.Presenter) getPresenter()).viewScreening();
            }
        }, DataModelPermission.permissionRead(LeaseParticipantScreeningTO.class));
        addView(screeningView);

        // Actions:
        passwordAction = new SecureMenuItem(i18n.tr("Change Password"), new Command() {
            @Override
            public void execute() {
                ((GuarantorViewerView.Presenter) getPresenter()).changePassword(getForm().getValue().customer().user().getPrimaryKey(), getForm()
                        .getValue().customer().person().getStringView());
            }
        }, new ActionPermission(GuarantorChangePassword.class));
        addAction(passwordAction);
    }

    @Override
    public void reset() {
        setActionVisible(screeningView, false);
        setActionVisible(passwordAction, false);

        super.reset();
    }

    @Override
    public void populate(GuarantorDTO value) {
        super.populate(value);

        setViewVisible(screeningView, value.screening().getPrimaryKey() != null);

        // Disable password change button for guarantors with no associated user principal
        if (value != null & !value.customer().user().isNull()) {
            setActionVisible(passwordAction, true);
        } else {
            setActionVisible(passwordAction, false);
        }
    }
}