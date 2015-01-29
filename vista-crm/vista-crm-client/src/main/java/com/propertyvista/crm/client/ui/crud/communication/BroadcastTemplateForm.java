/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 16, 2015
 * @author michaellif
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.richtext.RichTextTemplateAction;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.gadgets.components.TemplateInsertSelector;
import com.propertyvista.domain.communication.BroadcastTemplate;
import com.propertyvista.domain.communication.BroadcastTemplate.AudienceType;
import com.propertyvista.domain.communication.DeliveryHandle.MessageType;
import com.propertyvista.domain.communication.EmailTemplateType;

public class BroadcastTemplateForm extends CrmEntityForm<BroadcastTemplate> {

    private static final I18n i18n = I18n.get(BroadcastTemplateForm.class);

    private ContentEditor contentEditor;

    public BroadcastTemplateForm(IPrimeFormView<BroadcastTemplate, ?> view) {
        super(BroadcastTemplate.class, view);

        setTabBarVisible(false);
        selectTab(addTab(createInfoTab(), i18n.tr("Broadcast Template")));

    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Right, proto().category()).decorate();
        formPanel.append(Location.Left, proto().allowedReply()).decorate();
        formPanel.append(Location.Right, proto().highImportance()).decorate();
        formPanel.append(Location.Left, proto().audienceType()).decorate();
        formPanel.append(Location.Left, proto().messageType()).decorate();
        formPanel.append(Location.Dual, proto().subject()).decorate();
        formPanel.append(Location.Dual, proto().content(), contentEditor = new ContentEditor()).decorate();
        get(proto().audienceType()).setEditable(false);
        get(proto().name()).setEditable(false);
        return formPanel;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().messageType()).setEditable(!AudienceType.Employee.equals(getValue().audienceType().getValue()));
        if (!AudienceType.Employee.equals(getValue().audienceType().getValue())) {
            ((CComboBox<MessageType>) get(proto().messageType())).removeOption(MessageType.CommercialActivity);
            ((CComboBox<MessageType>) get(proto().messageType())).removeOption(MessageType.Organizational);
        }

        contentEditor.setAudienceType(getValue().audienceType().getValue());
    }

    static class ContentEditor extends CRichTextArea {

        private TemplateInsertSelector vm;

        public ContentEditor() {

            if (isEditable()) {
                if (vm == null) {
                    vm = new TemplateInsertSelector();
                }
                getNativeComponent().getEditor().setTemplateAction(new RichTextTemplateAction() {
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
                            vm.showBelow(target);
                        }
                    }
                });
            }

        }

        public void setAudienceType(AudienceType type) {
            if (vm != null) {
                vm.setItems(vm.getTemplateObjects(toTemplateType(type)));
            }
        }
    }

    private static EmailTemplateType toTemplateType(AudienceType type) {
        switch (type) {
        case Customer:
            return EmailTemplateType.MessageBroadcastCustomer;
        case Employee:
            return EmailTemplateType.MessageBroadcastEmployee;
        case Prospect:
            return EmailTemplateType.MessageBroadcastProspect;
        case Guarantor:
            return EmailTemplateType.MessageBroadcastGuarantor;
        case Tenant:
            return EmailTemplateType.MessageBroadcastTenant;
        default:
            throw new Error("Type not supported: " + (type == null ? "null" : type));
        }
    }
}
