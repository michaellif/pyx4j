/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.ui.crud.misc.IMemento;

import com.propertyvista.ob.client.forms.PmcAccountCreationRequestForm;
import com.propertyvista.ob.client.themes.OnboardingStyles;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;

public class PmcAccountCreationRequestViewImpl extends Composite implements PmcAccountCreationRequestView {

    private final PmcAccountCreationRequestForm form;

    private PmcAccountCreationRequestView.Presenter presenter;

    public PmcAccountCreationRequestViewImpl() {

        form = new PmcAccountCreationRequestForm(new PmcAccountCreationRequestForm.DnsCheckRequestHandler() {
            @Override
            public void checkDns(AsyncCallback<Boolean> callback, String dnsName) {
                presenter.checkDns(callback, dnsName);
            }
        }) {
            @Override
            public void onSubmit(PmcAccountCreationRequest duplicate) {
                presenter.createAccount();
            }
        };
        form.initContent();
        form.asWidget().getElement().getStyle().setProperty("marginLeft", "auto");
        form.asWidget().getElement().getStyle().setProperty("marginRight", "auto");

        SimplePanel panel = new SimplePanel();
        panel.addStyleName(OnboardingStyles.VistaObView.name());
        panel.setWidget(form);
        initWidget(panel);
    }

    @Override
    public void setPresenter(PmcAccountCreationRequestView.Presenter presenter) {
        this.presenter = presenter;
        this.form.setVisited(false);
    }

    @Override
    public PmcAccountCreationRequest getPmcAccountCreationRequest() {
        return form.getValue();
    }

    @Override
    public void setPmcAccountCreationRequest(PmcAccountCreationRequest request) {
        form.populate(request);
    }

    @Override
    public IMemento getMemento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storeState(Place place) {
        // TODO Auto-generated method stub

    }

    @Override
    public void restoreState() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showVisor(IsWidget widget, String caption) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }

}
