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
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.media.ProofOfEmploymentDocumentFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class ProofOfEmploymentUploaderFolder extends PortalBoxFolder<ProofOfEmploymentDocumentFolder> {

    private final static I18n i18n = I18n.get(ProofOfEmploymentUploaderFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public ProofOfEmploymentUploaderFolder() {
        super(ProofOfEmploymentDocumentFolder.class, i18n.tr("Proof Of Employment"));
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy policy) {
        this.documentationPolicy = policy;

        setNoDataNotificationWidget(null);
        if (documentationPolicy != null && documentationPolicy.mandatoryProofOfIncome().isBooleanTrue()) {
            setNoDataNotificationWidget(new Label(i18n.tr("Proof of Employment should be supplied!")));
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        addValueValidator(new EditableValueValidator<IList<ProofOfEmploymentDocumentFolder>>() {
            @Override
            public ValidationError isValid(CComponent<IList<ProofOfEmploymentDocumentFolder>> component, IList<ProofOfEmploymentDocumentFolder> value) {
                if (value != null && documentationPolicy != null) {
                    if (documentationPolicy.mandatoryProofOfIncome().isBooleanTrue() && getValue().isEmpty()) {
                        return new ValidationError(component, i18n.tr("Proof of Employment should be supplied!"));
                    }
                }
                return null;
            }
        });
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ProofOfEmploymentDocumentFolder) {
            return new ProofOfEmploymentDocumentEditor();
        }
        return super.create(member);
    }

    private class ProofOfEmploymentDocumentEditor extends CEntityForm<ProofOfEmploymentDocumentFolder> {

        public ProofOfEmploymentDocumentEditor() {
            super(ProofOfEmploymentDocumentFolder.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description()), 250).build());

            content.setH3(++row, 0, 1, i18n.tr("Files"));
            content.setWidget(++row, 0, inject(proto().files(), new ProofOfEmploymentDocumentFileFolder()));

            return content;
        }
    }
}
