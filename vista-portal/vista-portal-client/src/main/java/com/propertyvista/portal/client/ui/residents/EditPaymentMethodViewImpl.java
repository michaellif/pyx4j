/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;

public class EditPaymentMethodViewImpl extends FlowPanel implements EditPaymentMethodView {

    private Presenter presenter;

    private static I18n i18n = I18n.get(EditPaymentMethodViewImpl.class);

    private final EditPaymentMethodForm form;

    public EditPaymentMethodViewImpl() {
        form = new EditPaymentMethodForm();
        form.initContent();
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
        submitButton.getElement().getStyle().setMarginRight(1d, Unit.EM);
        add(DecorationUtils.inline(submitButton));

        CHyperlink cancel = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.cancel();
            }
        });
        cancel.setValue(i18n.tr("Cancel"));
        add(cancel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(PaymentMethodDTO paymentMethod) {
        form.populate(paymentMethod);

    }

}
