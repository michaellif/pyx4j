/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestViewerViewImpl extends CrmViewerViewImplBase<MaintenanceRequestDTO> implements MaintenanceRequestViewerView {

    private static final I18n i18n = I18n.get(MaintenanceRequestViewerViewImpl.class);

    private final Button scheduleAction;

    private final Button resolveAction;

    private final Button cancelAction;

    public MaintenanceRequestViewerViewImpl() {
        super(CrmSiteMap.Tenants.MaintenanceRequest.class, new MaintenanceRequestForm(true));

        scheduleAction = new Button(i18n.tr("Schedule..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ScheduleBox() {
                    @Override
                    public boolean onClickOk() {
                        ((MaintenanceRequestViewerView.Presenter) presenter).scheduleAction(getValue());
                        return true;
                    }
                }.show();
            }
        });
        addHeaderToolbarTwoItem(scheduleAction.asWidget());

        resolveAction = new Button(i18n.tr("Resolve"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((MaintenanceRequestViewerView.Presenter) presenter).resolveAction();
            }
        });
        addHeaderToolbarTwoItem(resolveAction.asWidget());

        cancelAction = new Button(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Cancel"), i18n.tr("Do you really want to cancel the request?"), new Command() {
                    @Override
                    public void execute() {
                        ((MaintenanceRequestViewerView.Presenter) presenter).cancelAction();
                    }
                });
            }
        });
        addHeaderToolbarTwoItem(cancelAction.asWidget());
    }

    @Override
    public void reset() {
        scheduleAction.setVisible(false);
        resolveAction.setVisible(false);
        cancelAction.setVisible(false);
        super.reset();
    }

    @Override
    public void populate(MaintenanceRequestDTO value) {
        super.populate(value);

        getEditButton().setVisible(value.status().getValue() == MaintenanceRequestStatus.Submitted);

        scheduleAction.setVisible(value.status().getValue() == MaintenanceRequestStatus.Submitted);
        resolveAction.setVisible(value.status().getValue() == MaintenanceRequestStatus.Scheduled);
        cancelAction.setVisible(value.status().getValue() != MaintenanceRequestStatus.Cancelled
                && value.status().getValue() != MaintenanceRequestStatus.Resolved);
    }

    // Internals:

    private abstract class ScheduleBox extends OkCancelDialog {

        private CEntityDecoratableForm<ScheduleDataDTO> content;

        public ScheduleBox() {
            super(i18n.tr("Schedule"));
            setBody(createBody());
            setSize("350px", "100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CEntityDecoratableForm<ScheduleDataDTO>(ScheduleDataDTO.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel main = new FormFlexPanel();

                    main.setWidget(1, 0, new DecoratorBuilder(inject(proto().date()), 10).build());
                    main.setWidget(2, 0, new DecoratorBuilder(inject(proto().time()), 10).build());

                    return main;
                }

                @Override
                public void addValidations() {
                    super.addValidations();

                }
            };

            content.initContent();
            content.populate(EntityFactory.create(ScheduleDataDTO.class));
            return content.asWidget();
        }

        public ScheduleDataDTO getValue() {
            return content.getValue();
        }
    }
}
