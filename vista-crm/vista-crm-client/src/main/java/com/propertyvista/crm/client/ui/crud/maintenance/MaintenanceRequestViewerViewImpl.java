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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestViewerViewImpl extends CrmViewerViewImplBase<MaintenanceRequestDTO> implements MaintenanceRequestViewerView {

    private static final I18n i18n = I18n.get(MaintenanceRequestViewerViewImpl.class);

    private MaintenanceRequestMetadata categoryMeta;

    private final MenuItem scheduleAction;

    private final MenuItem resolveAction;

    private final MenuItem rateAction;

    private final MenuItem cancelAction;

    public MaintenanceRequestViewerViewImpl() {
        setForm(new MaintenanceRequestForm(this));

        scheduleAction = new MenuItem(i18n.tr("Schedule..."), new Command() {
            @Override
            public void execute() {
                new ScheduleBox() {
                    @Override
                    public boolean onClickOk() {
                        ((MaintenanceRequestViewerView.Presenter) getPresenter()).scheduleAction(getValue().date().getValue(), getValue().time().getValue());
                        return true;
                    }
                }.show();
            }
        });
        addAction(scheduleAction);

        resolveAction = new MenuItem(i18n.tr("Resolve"), new Command() {
            @Override
            public void execute() {
                ((MaintenanceRequestViewerView.Presenter) getPresenter()).resolveAction();
            }
        });
        addAction(resolveAction);

        rateAction = new MenuItem(i18n.tr("Rate..."), new Command() {
            @Override
            public void execute() {
                new RateBox(getForm().getValue().surveyResponse()) {
                    @Override
                    public boolean onClickOk() {
                        ((MaintenanceRequestViewerView.Presenter) getPresenter()).rateAction(getValue());
                        return true;
                    }
                }.show();
            }
        });
        addAction(rateAction);

        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Cancel"), i18n.tr("Do you really want to cancel the request?"), new Command() {
                    @Override
                    public void execute() {
                        ((MaintenanceRequestViewerView.Presenter) getPresenter()).cancelAction();
                    }
                });
            }
        });
        addAction(cancelAction);
    }

    @Override
    public void setPresenter(IForm.Presenter presenter) {
        super.setPresenter(presenter);
        if (categoryMeta != null) {
            ((MaintenanceRequestForm) getForm()).setMaintenanceRequestCategoryMeta(categoryMeta);
        } else if (presenter != null) {
            ((MaintenanceRequestViewerView.Presenter) presenter).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadata>() {
                @Override
                public void onSuccess(MaintenanceRequestMetadata meta) {
                    MaintenanceRequestViewerViewImpl.this.categoryMeta = meta;
                    ((MaintenanceRequestForm) getForm()).setMaintenanceRequestCategoryMeta(meta);
                }
            });
        }
    }

    @Override
    public void reset() {
        setActionVisible(scheduleAction, false);
        setActionVisible(resolveAction, false);
        setActionVisible(rateAction, false);
        setActionVisible(cancelAction, false);
        super.reset();
    }

    @Override
    public void populate(MaintenanceRequestDTO value) {
        super.populate(value);

        StatusPhase phase = value.status().phase().getValue();
        setEditingVisible(!StatusPhase.closed().contains(phase));

        setActionVisible(scheduleAction, phase == StatusPhase.Submitted || phase == StatusPhase.Scheduled);
        setActionVisible(resolveAction, phase == StatusPhase.Scheduled);
        setActionVisible(rateAction, phase == StatusPhase.Resolved);
        setActionVisible(cancelAction, phase != StatusPhase.Cancelled && phase != StatusPhase.Resolved);
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

                    main.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().date()), 10).labelWidth(7).build());
                    main.setWidget(2, 0, new FormDecoratorBuilder(inject(proto().time()), 10).labelWidth(7).build());

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

    private abstract class RateBox extends OkCancelDialog {

        private CEntityDecoratableForm<SurveyResponse> content;

        public RateBox(SurveyResponse currentRate) {
            super(i18n.tr("Schedule"));
            setBody(createBody(currentRate));
            setHeight("100px");
        }

        protected Widget createBody(SurveyResponse currentRate) {
            getOkButton().setEnabled(true);

            content = new CEntityDecoratableForm<SurveyResponse>(SurveyResponse.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel main = new FormFlexPanel();

                    main.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().rating()), 3).labelWidth(7).build());
                    main.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().description()), 20).labelWidth(7).build());

                    // tweaking:
                    get(proto().rating()).setTooltip(i18n.tr("Set value in range from 1 to 5..."));
                    if (get(proto().rating()) instanceof CNumberField) {
                        ((CNumberField<Integer>) get(proto().rating())).setRange(1, 5);
                    }

                    return main;
                }

                @Override
                public void addValidations() {
                    super.addValidations();
                }
            };

            content.initContent();
            content.populate(currentRate);
            return content.asWidget();
        }

        public SurveyResponse getValue() {
            return content.getValue();
        }
    }
}
