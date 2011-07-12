/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.portal.domain.dto.PaymentMethodGenericDTO;

public class NewPaymentMethodViewImpl extends FlowPanel implements NewPaymentMethodView {

    private final NewPaymentMethodForm form;

    private Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(NewPaymentMethodViewImpl.class);

    public NewPaymentMethodViewImpl() {
        form = new NewPaymentMethodForm();
        form.initialize();
        add(form);

        Button submitButton = new Button(i18n.tr("Save"));
        //TODO implement
        submitButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (!form.isValid()) {
                    Window.scrollTo(0, 0);
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                } else {
                    presenter.save(form.getValue());
                }
            }
        });
        add(submitButton);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(PaymentMethodGenericDTO paymentMethod) {
        form.populate(paymentMethod);

    }

}
