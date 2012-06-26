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

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.IssueClassificationChoice;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestForm extends CrmEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private final FormFlexPanel statusPanel = new FormFlexPanel();

    private final FormFlexPanel surveyPanel = new FormFlexPanel();

    IssueClassificationChoice<?> mainChoice;

    private CComponent<?, ?> comp1, comp2, comp3, comp4;

    public MaintenanceRequestForm() {
        this(false);
    }

    public MaintenanceRequestForm(Class<MaintenanceRequestDTO> rootClass) {
        super(rootClass);
    }

    public MaintenanceRequestForm(boolean viewMode) {
        super(MaintenanceRequestDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
        selectTab(addTab(createGeneralTab(), i18n.tr("General")));

    }

    private Widget createGeneralTab() {
        FormFlexPanel topLeft = new FormFlexPanel();
        int row = -1;

        topLeft.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenant(), new CEntitySelectorHyperlink<Tenant>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().getPrimaryKey());
            }

            @Override
            protected TenantSelectorDialog getSelectorDialog() {
                return new TenantSelectorDialog(false) {

                    @Override
                    public boolean onClickOk() {
                        if (getSelectedItems().isEmpty()) {
                            return false;
                        }
                        setValue(getSelectedItems().get(0));
                        return true;
                    }
                };
            }
        }), 25).build());

        if (isEditable()) {
            // create selectors
            final IssueClassificationChoice<IssueElement> choice1 = new IssueClassificationChoice<IssueElement>(IssueElement.class) {
                @Override
                protected boolean isLeaf(IssueElement opt) {
                    return !opt.isEmpty() && opt.name().isNull();
                }
            };
            final IssueClassificationChoice<IssueRepairSubject> choice2 = new IssueClassificationChoice<IssueRepairSubject>(IssueRepairSubject.class) {
                @Override
                protected boolean isLeaf(IssueRepairSubject opt) {
                    return !opt.isEmpty() && opt.name().isNull();
                }
            };
            final IssueClassificationChoice<IssueSubjectDetails> choice3 = new IssueClassificationChoice<IssueSubjectDetails>(IssueSubjectDetails.class) {
                @Override
                protected boolean isLeaf(IssueSubjectDetails opt) {
                    return !opt.isEmpty() && opt.name().isNull();
                }
            };
            final IssueClassificationChoice<IssueClassification> choice4 = new IssueClassificationChoice<IssueClassification>(IssueClassification.class) {
                @Override
                protected boolean isLeaf(IssueClassification opt) {
                    return !opt.isEmpty() && opt.issue().isNull();
                }
            };
            mainChoice = choice1;
            choice2.assignParent(choice1, choice2.proto().issueElement());
            choice3.assignParent(choice2, choice3.proto().subject());
            choice4.assignParent(choice3, choice4.proto().subjectDetails());

            // assign components
            comp1 = choice1;
            comp2 = choice2;
            comp3 = choice3;
            comp4 = choice4;
        } else {
            // explicitly create labels instead of default combo boxes to prevent lengthy option download
            comp1 = new CEntityLabel();
            comp2 = new CEntityLabel();
            comp3 = new CEntityLabel();
            comp4 = new CEntityLabel();
        }
        topLeft.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject().issueElement(), comp1), 20).build());
        topLeft.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject(), comp2), 20).build());
        topLeft.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails(), comp3), 20).build());
        topLeft.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification(), comp4), 20).build());

        FormFlexPanel topRight = new FormFlexPanel();
        row = -1;
        topRight.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 20).build());

        row = -1;
        statusPanel.setH1(++row, 0, 1, i18n.tr("Status"));
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 10).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 10).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().scheduledTime(), new CTimeLabel()), 10).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().updated(), new CDateLabel()), 10).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().submitted(), new CDateLabel()), 10).build());

        row = -1;
        surveyPanel.setH1(++row, 0, 2, proto().surveyResponse().getMeta().getCaption());
        surveyPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().surveyResponse().rating(), new CLabel()), 10).build());
        surveyPanel.setWidget(row, 1, new DecoratorBuilder(inject(proto().surveyResponse().description(), new CLabel()), 10).build());

        // assemble main panel:
        FormFlexPanel main = new FormFlexPanel();

        main.setH1(0, 0, 2, i18n.tr("Issue Details"));
        main.getFlexCellFormatter().setColSpan(0, 0, 2);
        main.setWidget(1, 0, topLeft);
        main.setWidget(1, 1, topRight);
        main.getFlexCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);

        main.setWidget(2, 0, statusPanel);
        main.getFlexCellFormatter().setColSpan(2, 0, 2);

        main.setWidget(3, 0, surveyPanel);
        main.getFlexCellFormatter().setColSpan(3, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }

    @Override
    protected MaintenanceRequestDTO preprocessValue(MaintenanceRequestDTO value, boolean fireEvent, boolean populate) {
        if (isEditable() && mainChoice != null) {
            mainChoice.init();
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        MaintenanceRequestDTO mr = getValue();

        comp2.setVisible(!mr.issueClassification().subjectDetails().subject().name().isNull());
        comp3.setVisible(!mr.issueClassification().subjectDetails().name().isNull());
        comp4.setVisible(!mr.issueClassification().issue().isNull());

        get(proto().scheduledDate()).setVisible(mr.status().getValue() == MaintenanceRequestStatus.Scheduled);
        get(proto().scheduledTime()).setVisible(mr.status().getValue() == MaintenanceRequestStatus.Scheduled);

        get(proto().submitted()).setVisible(!mr.submitted().isNull());
        get(proto().updated()).setVisible(!mr.updated().isNull());
        get(proto().status()).setVisible(!mr.submitted().isNull());

        statusPanel.setVisible(!mr.issueClassification().isNull());
        surveyPanel.setVisible(mr.status().getValue() == MaintenanceRequestStatus.Resolved);
    }

}
