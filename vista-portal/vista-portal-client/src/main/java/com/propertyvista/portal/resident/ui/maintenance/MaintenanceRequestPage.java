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
package com.propertyvista.portal.resident.ui.maintenance;

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
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceRequestPageView.MaintenanceRequestPagePresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.PrintUtils;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class MaintenanceRequestPage extends CPortalEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestPage.class);

    private final Button btnCancel;

    private final Button btnPrint;

    public MaintenanceRequestPage(MaintenanceRequestPageViewImpl view) {
        super(MaintenanceRequestDTO.class, view, "Maintenance Request", ThemeColor.contrast5);
        btnCancel = new Button(i18n.tr("Cancel Request"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Are you sure you would like to cancel this request?"), new Command() {

                    @Override
                    public void execute() {
                        ((MaintenanceRequestPagePresenter) getView().getPresenter()).cancelRequest();
                    }
                });
            }
        });

        btnPrint = new Button(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(MaintenanceRequestPage.this.asWidget());
            }
        });

        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Basic Information"));
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().requestId(), new CLabel<String>()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().reportedForOwnUnit()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().category(), new CEntityLabel<MaintenanceRequestCategory>() {
            @Override
            public String format(MaintenanceRequestCategory value) {
                if (value == null) {
                    return "";
                } else {
                    StringBuilder result = new StringBuilder();
                    MaintenanceRequestCategory category = value;
                    while (!category.parent().isNull()) {
                        if (!category.name().isNull()) {
                            result.insert(0, result.length() > 0 ? "/" : "").insert(0, category.name().getValue());
                        }
                        category = category.parent();
                    }
                    return result.toString();
                }
            }
        }), 250).build());

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().summary()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().priority(), new CEntityLabel<MaintenanceRequestPriority>()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().status().phase()), 250).build());

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().petInstructions()), 250).build());

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredDate1()), 100).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredTime1()), 100).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredDate2()), 100).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredTime2()), 100).build());

        return mainPanel;
    }

    @Override
    protected FormDecorator<MaintenanceRequestDTO, CEntityForm<MaintenanceRequestDTO>> createDecorator() {
        FormDecorator<MaintenanceRequestDTO, CEntityForm<MaintenanceRequestDTO>> decorator = super.createDecorator();

        decorator.addHeaderToolbarWidget(btnCancel);
        decorator.addHeaderToolbarWidget(btnPrint);

        return decorator;
    }

    @Override
    protected void onValueSet(boolean populate) {
        btnCancel.setVisible(getValue() != null && StatusPhase.open().contains(getValue().status().phase().getValue()));
    }
}
