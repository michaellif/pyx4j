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
package com.propertyvista.portal.client.ui.residents.maintenance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.IssueClassificationChoice;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestForm extends CEntityDecoratableForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private IssueClassificationChoice<?> mainChoice;

    public MaintenanceRequestForm() {
        super(MaintenanceRequestDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;

        content.setBR(++row, 0, 1);

        // Add components
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

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject().issueElement(), choice1), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject(), choice2), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails(), choice3), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification(), choice4), 15).build());

        // Description
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());

        return content;
    }

    @Override
    public void onReset() {
        mainChoice.init();
        super.onReset();
    }
}
