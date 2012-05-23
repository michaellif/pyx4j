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

import java.io.Serializable;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
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

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
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

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    private static final String optionSelect = i18n.tr("Select");

    private final FormFlexPanel surveyPanel = new FormFlexPanel();

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
    public IsWidget createContent() {
        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    @SuppressWarnings("unchecked")
    private Widget createGeneralTab() {
        // configure issue selectors
        CComponent<?, ?> comp1 = inject(proto().issueClassification().subjectDetails().subject().issueElement());
        CComponent<?, ?> comp2 = inject(proto().issueClassification().subjectDetails().subject());
        CComponent<?, ?> comp3 = inject(proto().issueClassification().subjectDetails());
        CComponent<?, ?> comp4 = inject(proto().issueClassification());

        if (isEditable() && (comp1 instanceof CEntityComboBox<?>) && (comp2 instanceof CEntityComboBox<?>) && (comp3 instanceof CEntityComboBox<?>)
                && (comp4 instanceof CEntityComboBox<?>)) {

            final CEntityComboBox<IssueElement> combo1 = (CEntityComboBox<IssueElement>) comp1;
            final CEntityComboBox<IssueRepairSubject> combo2 = (CEntityComboBox<IssueRepairSubject>) comp2;
            final CEntityComboBox<IssueSubjectDetails> combo3 = (CEntityComboBox<IssueSubjectDetails>) comp3;
            final CEntityComboBox<IssueClassification> combo4 = (CEntityComboBox<IssueClassification>) comp4;

            // clear values for dependable selectors
            combo1.setNoSelectionText(optionSelect);
            comboClear(combo2, optionSelect + " " + combo1.getTitle());
            comboClear(combo3, optionSelect + " " + combo2.getTitle());
            comboClear(combo4, optionSelect + " " + combo3.getTitle());

            // add onChange handlers
            combo1.addValueChangeHandler(new ValueChangeHandler<IssueElement>() {
                @Override
                public void onValueChange(ValueChangeEvent<IssueElement> event) {
                    comboReset(combo2, PropertyCriterion.eq(combo2.proto().issueElement(), event.getValue()), i18n.tr("Please Select"));
                    // clear remaining selectors
                    comboClear(combo3, optionSelect + " " + combo2.getTitle());
                    comboClear(combo4, optionSelect + " " + combo3.getTitle());
                }
            });

            combo2.addValueChangeHandler(new ValueChangeHandler<IssueRepairSubject>() {
                @Override
                public void onValueChange(ValueChangeEvent<IssueRepairSubject> event) {
                    comboReset(combo3, PropertyCriterion.eq(combo3.proto().subject(), event.getValue()), i18n.tr("Please Select"));
                    // clear remaining selectors
                    comboClear(combo4, optionSelect + " " + combo3.getTitle());
                }
            });

            combo3.addValueChangeHandler(new ValueChangeHandler<IssueSubjectDetails>() {
                @Override
                public void onValueChange(ValueChangeEvent<IssueSubjectDetails> event) {
                    comboReset(combo4, PropertyCriterion.eq(combo4.proto().subjectDetails(), event.getValue()), i18n.tr("Please Select"));
                }
            });
        }

        int row = -1;
        FormFlexPanel left = new FormFlexPanel();
        left.setWidget(++row, 0, new DecoratorBuilder(comp1, 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(comp2, 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(comp3, 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(comp4, 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 20).build());

        row = -1;
        FormFlexPanel right = new FormFlexPanel();
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenant(), new CEntitySelectorHyperlink<Tenant>() {
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
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 10).build());
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 10).build());
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().scheduledTime(), new CTimeLabel()), 10).build());
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().updated(), new CDateLabel()), 10).build());
        right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().submitted(), new CDateLabel()), 10).build());

        surveyPanel.setH1(0, 0, 2, proto().surveyResponse().getMeta().getCaption());
        surveyPanel.setWidget(1, 0, new DecoratorBuilder(inject(proto().surveyResponse().rating(), new CLabel()), 10).build());
        surveyPanel.setWidget(1, 1, new DecoratorBuilder(inject(proto().surveyResponse().description(), new CLabel()), 10).build());

        // assemble main panel:
        FormFlexPanel main = new FormFlexPanel();

        main.setH1(0, 0, 2, "Information");
        main.getFlexCellFormatter().setColSpan(0, 0, 2);
        main.setWidget(1, 0, left);
        main.setWidget(1, 1, right);
        main.getFlexCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);

        main.setWidget(2, 0, surveyPanel);
        main.getFlexCellFormatter().setColSpan(2, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new ScrollPanel(main);
    }

    private void comboReset(CEntityComboBox<? extends IEntity> combo, PropertyCriterion crit, String title) {
        combo.resetCriteria();
        combo.addCriterion(crit);
        combo.setNoSelectionText(title);
        combo.retriveOptions(null);
        combo.setValue(null);
    }

    private void comboClear(CEntityComboBox<? extends IEntity> combo, String title) {
        comboReset(combo, PropertyCriterion.eq(combo.proto().id(), (Serializable) null), title);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().scheduledDate()).setVisible(getValue().status().getValue() == MaintenanceRequestStatus.Scheduled);
        get(proto().scheduledTime()).setVisible(getValue().status().getValue() == MaintenanceRequestStatus.Scheduled);

        get(proto().submitted()).setVisible(!getValue().submitted().isNull());
        get(proto().updated()).setVisible(!getValue().updated().isNull());
        get(proto().status()).setVisible(!getValue().submitted().isNull());

        surveyPanel.setVisible(getValue().status().getValue() == MaintenanceRequestStatus.Resolved);
    }
}
