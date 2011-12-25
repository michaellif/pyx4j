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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestEditorForm extends CrmEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public MaintenanceRequestEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public MaintenanceRequestEditorForm(Class<MaintenanceRequestDTO> rootClass) {
        super(rootClass);
    }

    public MaintenanceRequestEditorForm(IEditableComponentFactory factory) {
        super(MaintenanceRequestDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.setDisableMode(isEditable());
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
        FormFlexPanel main = new FormFlexPanel();

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
            combo1.setNoSelectionText(i18n.tr("Please Select"));
            comboClear(combo2, i18n.tr("Select " + combo1.getTitle()));
            comboClear(combo3, i18n.tr("Select " + combo2.getTitle()));
            comboClear(combo4, i18n.tr("Select " + combo3.getTitle()));

            // add onChange handlers
            combo1.addValueChangeHandler(new ValueChangeHandler<IssueElement>() {
                @Override
                public void onValueChange(ValueChangeEvent<IssueElement> event) {
                    comboReset(combo2, PropertyCriterion.eq(combo2.proto().issueElement(), event.getValue()), i18n.tr("Please Select"));
                    // clear remaining selectors
                    comboClear(combo3, i18n.tr("Select " + combo2.getTitle()));
                    comboClear(combo4, i18n.tr("Select " + combo3.getTitle()));
                }
            });

            combo2.addValueChangeHandler(new ValueChangeHandler<IssueRepairSubject>() {
                @Override
                public void onValueChange(ValueChangeEvent<IssueRepairSubject> event) {
                    comboReset(combo3, PropertyCriterion.eq(combo3.proto().subject(), event.getValue()), i18n.tr("Please Select"));
                    // clear remaining selectors
                    comboClear(combo4, i18n.tr("Select " + combo3.getTitle()));
                }
            });

            combo3.addValueChangeHandler(new ValueChangeHandler<IssueSubjectDetails>() {
                @Override
                public void onValueChange(ValueChangeEvent<IssueSubjectDetails> event) {
                    comboReset(combo4, PropertyCriterion.eq(combo4.proto().subjectDetails(), event.getValue()), i18n.tr("Please Select"));
                }
            });

        }

        // start panel layout
        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(comp1, 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(comp2, 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(comp3, 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(comp4, 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 20).build());
        main.setH1(++row, 0, 2, proto().surveyResponse().getMeta().getCaption());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().surveyResponse().rating()), 20).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().tenant()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().submited()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().status()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().updated()), 10).build());
        row++;
        row++;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().surveyResponse().description()), 10).build());

        return new CrmScrollPanel(main);
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

}
