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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.guarantor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.customer.screening.PersonScreeningLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorViewerViewImpl extends CrmViewerViewImplBase<GuarantorDTO> implements GuarantorViewerView {

    private final static I18n i18n = I18n.get(GuarantorViewerViewImpl.class);

    private final IListerView<PersonScreening> screeningLister;

    private final Button passwordAction;

    public GuarantorViewerViewImpl() {
        super(CrmSiteMap.Tenants.Guarantor.class);

        screeningLister = new ListerInternalViewImplBase<PersonScreening>(new PersonScreeningLister());

        //set main form here:
        setForm(new GuarantorEditorForm(true));

        passwordAction = new Button(i18n.tr("Change Password"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((GuarantorViewerView.Presenter) getPresenter()).goToChangePassword(getForm().getValue().user().getPrimaryKey(), getForm().getValue()
                        .customer().person().getStringView());
            }
        });
        addHeaderToolbarTwoItem(passwordAction.asWidget());
    }

    @Override
    public IListerView<PersonScreening> getScreeningListerView() {
        return screeningLister;
    }

    @Override
    public void populate(GuarantorDTO value) {
        super.populate(value);

        // Disable password change button for guarantors with no associated user principal
        if (value != null & !value.user().isNull()) {
            passwordAction.setVisible(true);
        } else {
            passwordAction.setVisible(false);
        }
    }
}