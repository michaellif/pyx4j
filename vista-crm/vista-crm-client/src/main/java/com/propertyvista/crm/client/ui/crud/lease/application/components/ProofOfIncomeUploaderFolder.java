/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.media.ProofOfIncomeDocumentFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;

public class ProofOfIncomeUploaderFolder extends VistaBoxFolder<ProofOfIncomeDocumentFolder> {

    private final static I18n i18n = I18n.get(ProofOfIncomeUploaderFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public ProofOfIncomeUploaderFolder() {
        super(ProofOfIncomeDocumentFolder.class, i18n.tr("Proof Of Income"));
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy policy) {
        this.documentationPolicy = policy;

        setNoDataNotificationWidget(null);
        if (documentationPolicy != null && documentationPolicy.mandatoryProofOfIncome().getValue(false)) {
            setNoDataNotificationWidget(new Label(i18n.tr("Proof of Income should be supplied!")));
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        addComponentValidator(new AbstractComponentValidator<IList<ProofOfIncomeDocumentFolder>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && documentationPolicy != null) {
                    if (documentationPolicy.mandatoryProofOfIncome().getValue(false) && getValue().isEmpty()) {
                        return new FieldValidationError(getComponent(), i18n.tr("Proof of Income should be supplied!"));
                    }
                }
                return null;
            }
        });
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ProofOfIncomeDocumentFolder) {
            return new ProofOfIncomeDocumentEditor();
        }
        return super.create(member);
    }

    private class ProofOfIncomeDocumentEditor extends CEntityForm<ProofOfIncomeDocumentFolder> {

        public ProofOfIncomeDocumentEditor() {
            super(ProofOfIncomeDocumentFolder.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), 50, true).build());
            content.setH3(++row, 0, 2, i18n.tr("Files"));
            content.setWidget(++row, 0, 2, inject(proto().files(), new ProofOfIncomeDocumentFileFolder()));

            return content;
        }
    }
}
