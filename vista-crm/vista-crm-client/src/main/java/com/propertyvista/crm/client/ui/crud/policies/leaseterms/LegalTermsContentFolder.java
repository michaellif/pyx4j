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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;

public class LegalTermsContentFolder extends VistaBoxFolder<LegalTermsContent> {

    private static final I18n i18n = I18n.get(LegalTermsContentFolder.class);

    private final CEntityForm<LegalTermsDescriptor> parentForm;

    public LegalTermsContentFolder(CEntityForm<LegalTermsDescriptor> parentForm) {
        super(LegalTermsContent.class, parentForm.isEditable());
        this.parentForm = parentForm;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LegalTermsContent) {
            return new LegalTermsContentEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();
        this.addValueValidator(new EditableValueValidator<IList<LegalTermsContent>>() {
            @Override
            public ValidationError isValid(CComponent<IList<LegalTermsContent>> component, IList<LegalTermsContent> value) {
                if (value == null || value.isEmpty()) {
                    return new ValidationError(component, i18n.tr("At least one content item is necessary"));
                } else {
                    return null;
                }
            }
        });
    }

    private static class LegalTermsContentEditor extends CEntityDecoratableForm<LegalTermsContent> {

        public LegalTermsContentEditor() {
            super(LegalTermsContent.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel();

            int row = -1;
            contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().locale()), true).build());
            contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().localizedCaption()), true).build());

            CComponent<?> termsContentComp = null;
            if (isEditable()) {
                CRichTextArea editor = new CRichTextArea();
                editor.setImageProvider(new SiteImageResourceProvider());
                termsContentComp = inject(proto().content(), editor);
            } else {
                termsContentComp = inject(proto().content(), new CLabel<String>());
            }
            contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(termsContentComp, true).build());

            return contentPanel;
        }

    }
}
