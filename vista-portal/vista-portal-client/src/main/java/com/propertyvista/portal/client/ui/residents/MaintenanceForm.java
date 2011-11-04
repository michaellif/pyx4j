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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;

public class MaintenanceForm extends CEntityEditor<MaintenanceRequestDTO> implements MaintenanceView {

    private static I18n i18n = I18n.get(MaintenanceForm.class);

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

        container.add(systemStatus);
        container.add(supportHistory);

        WidgetDecorator decorator = new WidgetDecorator(inject(proto().maintenanceType()));
        decorator.setWidth("100%");
        container.add(decorator);
        decorator = new WidgetDecorator(inject(proto().problemDescription()));
        decorator.setWidth("100%");
        container.add(decorator);
        return container;

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}
