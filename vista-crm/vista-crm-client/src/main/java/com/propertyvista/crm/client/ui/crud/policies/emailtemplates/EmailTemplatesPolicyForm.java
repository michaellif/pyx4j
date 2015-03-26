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
 */
package com.propertyvista.crm.client.ui.crud.policies.emailtemplates;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.richtext.RichTextTemplateAction;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.client.ui.gadgets.components.TemplateInsertSelector;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.dto.EmailTemplatesPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;

public class EmailTemplatesPolicyForm extends PolicyDTOTabPanelBasedForm<EmailTemplatesPolicyDTO> {

    private final static I18n i18n = I18n.get(EmailTemplatesPolicyForm.class);

    public EmailTemplatesPolicyForm(IPrimeFormView<EmailTemplatesPolicyDTO, ?> view) {
        super(EmailTemplatesPolicyDTO.class, view);

        addTab(createEmailTemplatesPanel(), i18n.tr("Templates"));

        addTab(createEmailTemplatesHeaderFooterPanel(), i18n.tr("Header and Footer"));
    }

    private IsWidget createEmailTemplatesHeaderFooterPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().header()).decorate();
        formPanel.append(Location.Dual, proto().footer()).decorate();

        return formPanel;
    }

    private IsWidget createEmailTemplatesPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().templates(), new EmailTemplateEditorFolder());

        return formPanel;
    }

    private static class EmailTemplateEditorFolder extends VistaBoxFolder<EmailTemplate> {

        public EmailTemplateEditorFolder() {
            super(EmailTemplate.class);
        }

        @Override
        protected CForm<EmailTemplate> createItemForm(IObject<?> member) {
            return new EmailTemplateEditor();
        }

        private static class EmailTemplateEditor extends CForm<EmailTemplate> {

            public EmailTemplateEditor() {
                super(EmailTemplate.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                //content.setH1(++row, 0, 1, proto().type().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().templateType()).decorate();
                formPanel.append(Location.Dual, proto().subject()).decorate();
                formPanel.append(Location.Dual, proto().useHeader()).decorate();
                formPanel.append(Location.Dual, proto().useFooter()).decorate();
                CRichTextArea editor = new CRichTextArea();
                if (false) {
                    // Currently SiteImageResourceProvider does nothing and does not work properly.
                    // So we allow to insert images by http URL into mail template
                    editor.setImageProvider(new SiteImageResourceProvider());
                }
                formPanel.append(Location.Dual, proto().content(), editor).decorate();
                if (isEditable()) {
                    final CComponent<?, EmailTemplateType, ?, ?> comp = get(proto().templateType());
                    final TemplateInsertSelector vm = new TemplateInsertSelector();
                    editor.getNativeComponent().getEditor().setTemplateAction(new RichTextTemplateAction() {
                        @Override
                        public void perform(final Formatter formatter, final Command onComplete, final UIObject target) {
                            if (vm.isShowing()) {
                                vm.hide();
                            } else {
                                vm.setSelectionHandler(new Command() {
                                    @Override
                                    public void execute() {
                                        formatter.insertHTML(vm.getSelectedValue());
                                        onComplete.execute();
                                    }
                                });
                                vm.setItems(vm.getTemplateObjects(comp.getValue()));
                                vm.showBelow(target);
                            }
                        }
                    });
                    // change template object list when template type selection changes
                    comp.addValueChangeHandler(new ValueChangeHandler<EmailTemplateType>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<EmailTemplateType> event) {
                            vm.setItems(vm.getTemplateObjects(event.getValue()));
                        }
                    });
                    comp.addPropertyChangeHandler(new PropertyChangeHandler() {
                        @Override
                        public void onPropertyChange(PropertyChangeEvent event) {
                            if (event.isEventOfType(PropertyChangeEvent.PropertyName.repopulated)) {
                                vm.setItems(vm.getTemplateObjects(comp.getValue()));
                            }
                        }
                    });
                }
                return formPanel;
            }
        }
    }
}
