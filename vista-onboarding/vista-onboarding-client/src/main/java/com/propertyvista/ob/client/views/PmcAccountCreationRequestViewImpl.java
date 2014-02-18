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

import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.misc.IMemento;
import com.pyx4j.site.client.ui.visor.IVisor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.ob.client.forms.PmcAccountCreationRequestForm;
import com.propertyvista.ob.client.themes.OnboardingStyles;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;

public class PmcAccountCreationRequestViewImpl extends Composite implements PmcAccountCreationRequestView {

    public static final I18n i18n = I18n.get(PmcAccountCreationRequestViewImpl.class);

    private final PmcAccountCreationRequestForm form;

    private PmcAccountCreationRequestView.Presenter presenter;

    private final HTML messageHolder;

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

            @Override
            public void onTermsOpenRequest() {
                presenter.openTerms();
            }

        };

        Label caption = new Label();
        caption.addStyleName(OnboardingStyles.OnboardingCaption.name());
        caption.getElement().getStyle().setClear(Clear.BOTH);
        caption.setText(i18n.tr("Sign Up For PropertyVista"));

        messageHolder = new HTML();
        messageHolder.setStyleName(OnboardingStyles.OnboardingMessage.name());

        form.initContent();
        form.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        form.asWidget().getElement().getStyle().setWidth(300, Unit.PX);

        HTML text = new HTML();
        text.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        text.getElement().getStyle().setWidth(350, Unit.PX);
        text.setHTML(PmcAccountCreationViewResources.INSTANCE.singUpText().getText());

        FlowPanel panel = new FlowPanel();
        panel.addStyleName(OnboardingStyles.VistaObView.name());
        panel.add(caption);
        panel.add(messageHolder);
        panel.add(text);
        panel.add(form);
        initWidget(panel);
    }

    @Override
    public void setPresenter(PmcAccountCreationRequestView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        form.setSubmitEnable(isEnabled);
    }

    @Override
    public void setMessage(String message) {
        messageHolder.setHTML(message);
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
    public void showVisor(IVisor visor) {
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
