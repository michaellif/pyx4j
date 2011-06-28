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
package com.propertyvista.portal.client.ui.residents;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.portal.client.ui.decorations.CriteriaWidgetDecorator;
import com.propertyvista.portal.client.ui.decorations.PortalHeaderDecorator;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;

public class MaintenanceForm extends CEntityForm<MaintenanceRequestDTO> implements MaintenanceView {

    private static I18n i18n = I18nFactory.getI18n(MaintenanceForm.class);

    private MaintenanceView.Presenter presenter;

    public MaintenanceForm() {
        super(MaintenanceRequestDTO.class);
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
        systemStatus.asWidget().getElement().getStyle().setMarginRight(1.5, Unit.EM);

        CHyperlink supportHistory = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.showSupportHistory();
            }
        });
        supportHistory.setValue(i18n.tr("Support History"));

        PortalHeaderDecorator header = new PortalHeaderDecorator(i18n.tr("Report a Problem"), "100%");
        header.addToTheRight(systemStatus);
        header.addToTheRight(supportHistory);

        container.add(header);
        CriteriaWidgetDecorator decorator = new CriteriaWidgetDecorator(inject(proto().maintenanceType()), "100%");
        decorator.setWidth("100%");
        container.add(decorator);
        decorator = new CriteriaWidgetDecorator(inject(proto().problemDescription()), "100%");
        decorator.setWidth("100%");
        container.add(decorator);
        return container;

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}
