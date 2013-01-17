/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.registration;

import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationDTO;

public class TenantRegistrationViewImpl extends Composite implements TenantRegistrationView {

    private static I18n i18n = I18n.get(TenantRegistrationForm.class);

    private final TenantRegistrationForm form;

    private Presenter presenter;

    public TenantRegistrationViewImpl() {
        FlowPanel viewPanel = new FlowPanel();

        form = new TenantRegistrationForm();
        form.initContent();
        viewPanel.add(form);

        SimplePanel buttonHolder = new SimplePanel();
        buttonHolder.setWidth("100%");
        buttonHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        Button register = new Button(i18n.tr("Register"), new Command() {
            @Override
            public void execute() {
                form.revalidate();
                if (form.isValid()) {
                    TenantRegistrationViewImpl.this.presenter.onRegister();
                }
            }
        });
        buttonHolder.setWidget(register);

        viewPanel.add(buttonHolder);

        initWidget(viewPanel);
    }

    @Override
    public void populate(List<SelfRegistrationBuildingDTO> buildings) {
        form.setBuildingOptions(buildings);
        form.populateNew();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public SelfRegistrationDTO getValue() {
        return form.getValue();
    }

    @Override
    public void showError(String message) {
        MessageDialog.error(i18n.tr("Registration Error"), message);
    }

}
