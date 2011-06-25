/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.portal.client.ui.decorations.PortalHeaderDecorator;
import com.propertyvista.portal.domain.dto.MaintenanceRequestListDTO;

public class MaintenanceHistoryForm extends CEntityForm<MaintenanceRequestListDTO> implements MaintenanceHistoryView {

    private MaintenanceHistoryView.Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(MaintenanceHistoryForm.class);

    public MaintenanceHistoryForm() {
        super(MaintenanceRequestListDTO.class);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        CHyperlink systemStatus = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.showSystemStatus();
            }
        });
        systemStatus.setValue(i18n.tr("System Status"));
        PortalHeaderDecorator header = new PortalHeaderDecorator(i18n.tr("Support Request History"), "100%");
        header.addToTheRight(systemStatus);
        container.add(header);
        return container;
    }

}
