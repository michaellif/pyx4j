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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.richtext.ExtendedRichTextToolbar.RichTextAction;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.services.policies.emailtemplates.EmailTemplateManagerService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.dto.EmailTemplatesPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.policy.policies.emailtemplates.EmailTemplateTypeDTO;
import com.propertyvista.domain.policy.policies.emailtemplates.EmailTemplateTypesDTO;

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

        panel.setWidget(++row, 0, inject(proto().templates(), new EmailTemplateEditorFolder()));

        return panel;
    }

    // TODO this cannot be used because too many tabs are too wide and unable to fit in one sceen
    @Deprecated
    private TabDescriptor createEmailTemplateTab(IObject<?> template) {
        return new TabDescriptor(new CrmScrollPanel(inject(template, new EmailTemplateEditorFolder()).asWidget()), template.getMeta().getCaption());
    }

    private static class EmailTemplateEditorFolder extends VistaBoxFolder<EmailTemplate> {
        private static final Map<EmailTemplateType, Set<String>> templateObjects = new HashMap<EmailTemplateType, Set<String>>();

        public EmailTemplateEditorFolder() {
            super(EmailTemplate.class);

            // get the template object list
            EmailTemplateManagerService service = GWT.create(EmailTemplateManagerService.class);
            service.getTemplateDataObjects(new AsyncCallback<EmailTemplateTypesDTO>() {
                @Override
                public void onFailure(Throwable caught) {
                    MessageDialog.error(i18n.tr("Server Error"), i18n.tr("Data not available.") + " " + caught.getMessage());
                }

                @Override
                public void onSuccess(EmailTemplateTypesDTO result) {
                    for (EmailTemplateTypeDTO tplType : result.types()) {
                        templateObjects.put(tplType.type().getValue(), tplType.objectNames().getValue());
                    }
                }
            });
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
                    final ListBox tplObjectList = new ListBox();
                    CRichTextArea editor = new CRichTextArea();
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().content(), editor)).build());
                    // create variable dropdown
                    editor.getWidget().getEditor().addCustomList(tplObjectList, new RichTextAction() {
                        @Override
                        public void perform(Formatter formatter, Command onComplete) {
                            formatter.insertHTML(tplObjectList.getItemText(tplObjectList.getSelectedIndex()));
                            onComplete.execute();
                        }
                    });
                    // change template object list when template type selection changes
                    get(proto().type()).addValueChangeHandler(new ValueChangeHandler<EmailTemplateType>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<EmailTemplateType> event) {
                            tplObjectList.clear();
                            for (String objName : templateObjects.get(event.getValue())) {
                                tplObjectList.addItem(objName);
                            }
                        }
                    });
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
