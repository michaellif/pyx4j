/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 6, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.io.Serializable;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.portal.rpc.portal.dto.MaintenanceRequestDTO;

public class NewMaintenanceRequestForm extends CEntityDecoratableEditor<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(NewMaintenanceRequestForm.class);

    private static String defaultChoice = i18n.tr("Select");

    private CEntityComboBox<IssueElement> mainCombo;

    public NewMaintenanceRequestForm() {
        super(MaintenanceRequestDTO.class, new VistaEditorsComponentFactory());
        initContent();
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("NEW TICKET"));

        // Add components
        final CEntityComboBox<IssueElement> combo1 = new CEntityComboBox<IssueElement>(IssueElement.class);
        final CEntityComboBox<IssueRepairSubject> combo2 = new CEntityComboBox<IssueRepairSubject>(IssueRepairSubject.class);
        final CEntityComboBox<IssueSubjectDetails> combo3 = new CEntityComboBox<IssueSubjectDetails>(IssueSubjectDetails.class);
        final CEntityComboBox<IssueClassification> combo4 = new CEntityComboBox<IssueClassification>(IssueClassification.class);

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject().issueElement(), combo1), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject(), combo2), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails(), combo3), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification(), combo4), 15).build());

        // clear values for dependable selectors
        final String defaultRepairSubject = defaultChoice + " " + combo1.getTitle();
        final String defaultSubjectDetails = defaultChoice + " " + combo2.getTitle();
        final String defaultClassification = defaultChoice + " " + combo3.getTitle();

        combo1.setNoSelectionText(defaultChoice);
        comboClear(combo2, defaultRepairSubject);
        comboClear(combo3, defaultSubjectDetails);
        comboClear(combo4, defaultClassification);

        // add onChange handlers
        combo1.addValueChangeHandler(new ValueChangeHandler<IssueElement>() {
            @Override
            public void onValueChange(ValueChangeEvent<IssueElement> event) {
                if (event.getValue() != null) {
                    comboReset(combo2, PropertyCriterion.eq(combo2.proto().issueElement(), event.getValue()), defaultChoice);
                } else {
                    comboClear(combo2, defaultRepairSubject);
                }
                // clear remaining selectors
                comboClear(combo3, defaultSubjectDetails);
                comboClear(combo4, defaultClassification);
            }
        });

        combo2.addValueChangeHandler(new ValueChangeHandler<IssueRepairSubject>() {
            @Override
            public void onValueChange(ValueChangeEvent<IssueRepairSubject> event) {
                if (event.getValue() != null) {
                    comboReset(combo3, PropertyCriterion.eq(combo3.proto().subject(), event.getValue()), defaultChoice);
                } else {
                    comboClear(combo3, defaultSubjectDetails);
                }
                // clear remaining selectors
                comboClear(combo4, defaultClassification);
            }
        });

        combo3.addValueChangeHandler(new ValueChangeHandler<IssueSubjectDetails>() {
            @Override
            public void onValueChange(ValueChangeEvent<IssueSubjectDetails> event) {
                if (event.getValue() != null) {
                    comboReset(combo4, PropertyCriterion.eq(combo4.proto().subjectDetails(), event.getValue()), defaultChoice);
                } else {
                    comboClear(combo4, defaultClassification);
                }
            }
        });

        // Description
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());

        this.mainCombo = combo1;
        return content;
    }

//    public void reset() {
//        mainCombo.setValue(null);
//    }

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
