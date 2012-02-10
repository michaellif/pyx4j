/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.emailtemplates;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.policy.dto.EmailTemplatesPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;

public class EmailTemplatesPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<EmailTemplatesPolicyDTO> {

    private final static I18n i18n = I18n.get(EmailTemplatesPolicyEditorForm.class);

    public EmailTemplatesPolicyEditorForm() {
        this(false);
    }

    public EmailTemplatesPolicyEditorForm(boolean viewMode) {
        super(EmailTemplatesPolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createEmailTemplatesPanel(), i18n.tr("Templates"))
        );//@formatter:on
    }

    private Widget createEmailTemplatesPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;
        //for (EmailTemplate template : templates()) {
        //   panel.setH1(++row, 0, 1, template.getMeta().getCaption());
        panel.setWidget(++row, 0, inject(proto().templates(), new EmailTemplateEditorFolder()));
        //}

        return panel;
    }

    // TODO this cannot be used because too many tabs are too wide and unable to fit in one sceen
    @Deprecated
    private TabDescriptor createEmailTemplateTab(IObject<?> template) {
        return new TabDescriptor(new CrmScrollPanel(inject(template, new EmailTemplateEditorFolder()).asWidget()), template.getMeta().getCaption());
    }

    private static class EmailTemplateEditorFolder extends VistaBoxFolder<EmailTemplate> {

        public EmailTemplateEditorFolder() {
            super(EmailTemplate.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof EmailTemplate) {
                return new EmailTemplateEditor();
            } else {
                return super.create(member);
            }
        }

        private static class EmailTemplateEditor extends CEntityDecoratableEditor<EmailTemplate> {

            public EmailTemplateEditor() {
                super(EmailTemplate.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                //content.setH1(++row, 0, 1, proto().type().getMeta().getCaption());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type())).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().subject())).build());
                if (isEditable()) {
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().content())).build());
                } else {
                    CLabel body = new CLabel();
                    body.setAllowHtml(true);
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().content(), body), 60).build());
                }
                return content;
            }
        }

    }

}
