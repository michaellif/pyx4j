/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.policy.policies.specials.LegalTermsContent;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;

public class LegalTermsContentFolder extends VistaBoxFolder<LegalTermsContent> {

    private static final I18n i18n = I18n.get(LegalTermsContentFolder.class);

    private final CEntityEditor<LegalTermsDescriptor> parentForm;

    public LegalTermsContentFolder(CEntityEditor<LegalTermsDescriptor> parentForm) {
        super(LegalTermsContent.class, parentForm.isEditable());
        this.parentForm = parentForm;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LegalTermsContent) {
            return new LegalTermsContentEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    protected void createNewEntity(LegalTermsContent newEntity, AsyncCallback<LegalTermsContent> callback) {
        newEntity.descriptor().set(parentForm.getValue());
        super.createNewEntity(newEntity, callback);
    }

    @Override
    public void addValidations() {
        super.addValidations();
        this.addValueValidator(new EditableValueValidator<IList<LegalTermsContent>>() {
            @Override
            public ValidationFailure isValid(CComponent<IList<LegalTermsContent>, ?> component, IList<LegalTermsContent> value) {
                return !value.isEmpty() ? null : new ValidationFailure(i18n.tr("At least one content item is necessary"));
            }
        });
    }

    private static class LegalTermsContentEditor extends CEntityDecoratableEditor<LegalTermsContent> {

        public LegalTermsContentEditor() {
            super(LegalTermsContent.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel contentPanel = new FormFlexPanel();

            int row = -1;
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().locale()), 10).labelWidth(10).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().localizedCaption()), 10).labelWidth(10).build());

            CComponent<?, ?> termsContentComp = null;
            if (isEditable()) {
                termsContentComp = inject(proto().content());
            } else {
                CLabel termsContentLabel = new CLabel();
                termsContentLabel.setAllowHtml(true);
                termsContentComp = inject(proto().content(), termsContentLabel);
            }
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(termsContentComp, 45).labelWidth(10).build());

            return contentPanel;
        }

    }
}
