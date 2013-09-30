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
package com.propertyvista.portal.web.client.ui.maintenance;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;
import com.propertyvista.portal.web.client.ui.profile.ProfilePageView.ProfilePagePresenter;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class MaintenanceRequestPage extends CPortalEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestPage.class);

    private ProfilePagePresenter presenter;

    public MaintenanceRequestPage(MaintenanceRequestPageViewImpl view) {
        super(MaintenanceRequestDTO.class, view, "Maintenance Request", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    public void setPresenter(ProfilePagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Basic Information"));
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().requestId(), new CLabel<String>()), 250).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reportedForOwnUnit()), 250).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().category(), new CEntityLabel<MaintenanceRequestCategory>() {
            @Override
            public String format(MaintenanceRequestCategory value) {
                if (value == null) {
                    return "";
                } else {
                    return value.name().getValue() + "-TODO";
                }
            }
        }), 250).build());

        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().summary()), 250).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 250).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().priority(), new CEntityLabel<MaintenanceRequestPriority>()), 250).build());

        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().petInstructions()), 250).build());

        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().preferredDate1()), 100).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().preferredTime1()), 100).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().preferredDate2()), 100).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().preferredTime2()), 100).build());

        return mainPanel;
    }

    @Override
    protected FormDecorator<MaintenanceRequestDTO, CEntityForm<MaintenanceRequestDTO>> createDecorator() {
        FormDecorator<MaintenanceRequestDTO, CEntityForm<MaintenanceRequestDTO>> decorator = super.createDecorator();

        Button btnEdit = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
//TODO
            }
        });
        decorator.addHeaderToolbarButton(btnEdit);

        return decorator;
    }
}
