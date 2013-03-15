/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ILister;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunLister;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.rpc.TriggerDTO;

public class TriggerViewerViewImpl extends OperationsViewerViewImplBase<TriggerDTO> implements TriggerViewerView {

    private static final I18n i18n = I18n.get(TriggerEditorViewImpl.class);

    private final ILister<Run> runLister;

    private final Button runImmediately;

    private final Button runForDate;

    public TriggerViewerViewImpl() {
        runLister = new ListerInternalViewImplBase<Run>(new RunLister(true));

        setForm(new TriggerForm(this));

        // Add actions:
        Button refresh = new Button(i18n.tr("Refresh"), new Command() {
            @Override
            public void execute() {
                ((TriggerViewerView.Presenter) getPresenter()).refresh();
            }
        });
        addHeaderToolbarItem(refresh.asWidget());

        runImmediately = new Button(i18n.tr("Run Immediatly"), new Command() {
            @Override
            public void execute() {
                ((TriggerViewerView.Presenter) getPresenter()).runImmediately();
            }
        });
        addHeaderToolbarItem(runImmediately.asWidget());

        runForDate = new Button(i18n.tr("Run for Date..."), new Command() {
            @Override
            public void execute() {
                new ScheduleBox() {
                    @Override
                    public boolean onClickOk() {
                        ((TriggerViewerView.Presenter) getPresenter()).runForDate(getValue());
                        return true;
                    }
                }.show();

            }
        });
        addHeaderToolbarItem(runForDate.asWidget());
    }

    @Override
    public ILister<Run> getRunListerView() {
        return runLister;
    }

    @Override
    public void reset() {
        runImmediately.setVisible(false);
        runForDate.setVisible(false);

        super.reset();
    }

    @Override
    public void populate(TriggerDTO value) {
        super.populate(value);

        runImmediately.setVisible(true);
        runForDate.setVisible(((value != null) && (value.triggerType().getValue() != null) && (value.triggerType().getValue().isDailyExecutions())));
    }

    // Internals:
    private abstract class ScheduleBox extends OkCancelDialog {

        private CEntityDecoratableForm<ScheduleDataDTO> content;

        public ScheduleBox() {
            super(i18n.tr("Schedule"));
            setBody(createBody());
            setHeight("100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CEntityDecoratableForm<ScheduleDataDTO>(ScheduleDataDTO.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel main = new FormFlexPanel();

                    main.setWidget(1, 0, new DecoratorBuilder(inject(proto().date()), 10).labelWidth(7).build());
//                    main.setWidget(2, 0, new DecoratorBuilder(inject(proto().time()), 10).labelWidth(7).build());

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