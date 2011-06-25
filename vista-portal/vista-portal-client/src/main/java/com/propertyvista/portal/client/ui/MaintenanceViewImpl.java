/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;

public class MaintenanceViewImpl extends FlowPanel implements MaintenanceView {
    private MaintenanceView.Presenter presenter;

    private final MaintenanceForm form;

    private static I18n i18n = I18nFactory.getI18n(MaintenanceViewImpl.class);

    public MaintenanceViewImpl() {
        form = new MaintenanceForm();
        form.initialize();
        add(form);

        Button submitButton = new Button(i18n.tr("Submit"));
        //TODO implement
        submitButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (!form.isValid()) {
                    Window.scrollTo(0, 0);
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                Window.scrollTo(0, 0);
            }
        });
        add(submitButton);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.setPresenter(presenter);

    }

    @Override
    public void populate(MaintenanceRequestDTO problem) {
        form.populate(problem);
    }

}
