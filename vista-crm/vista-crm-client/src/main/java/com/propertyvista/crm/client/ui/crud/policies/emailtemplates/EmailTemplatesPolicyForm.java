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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
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
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.richtext.ExtendedRichTextToolbar.RichTextAction;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.rpc.services.policies.emailtemplates.EmailTemplateManagerService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.dto.EmailTemplatesPolicyDTO;
import com.propertyvista.domain.policy.dto.emailtemplates.EmailTemplateTypeDTO;
import com.propertyvista.domain.policy.dto.emailtemplates.EmailTemplateTypesDTO;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;

public class EmailTemplatesPolicyForm extends PolicyDTOTabPanelBasedForm<EmailTemplatesPolicyDTO> {

    private final static I18n i18n = I18n.get(EmailTemplatesPolicyForm.class);

    public EmailTemplatesPolicyForm(IForm<EmailTemplatesPolicyDTO> view) {
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
        private static final Map<EmailTemplateType, List<String>> templateObjects = new HashMap<EmailTemplateType, List<String>>();

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
                    for (EmailTemplateTypeDTO tplType : result.types) {
                        templateObjects.put(tplType.type, tplType.objectNames);
                    }
                }
            });
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
                formPanel.append(Location.Dual, proto().type()).decorate();
                formPanel.append(Location.Dual, proto().subject()).decorate();
                formPanel.append(Location.Dual, proto().useHeader()).decorate();
                formPanel.append(Location.Dual, proto().useFooter()).decorate();
                CRichTextArea editor = new CRichTextArea();
                editor.setImageProvider(new SiteImageResourceProvider());
                formPanel.append(Location.Dual, proto().content(), editor).decorate();
                if (isEditable()) {
                    // create variable selection button
                    final PushButton pb = editor.getNativeComponent().getEditor().getCustomButton();
                    pb.setText(i18n.tr("MERGE"));
                    pb.getElement().getStyle().setColor("black");
                    pb.setVisible(true);
                    final TemplateVarSelector vm = new TemplateVarSelector();
                    editor.getNativeComponent().getEditor().setCustomAction(new RichTextAction() {
                        @Override
                        public void perform(final Formatter formatter, final Command onComplete) {
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
                                vm.showBelow(pb);
                            }
                        }
                    });
                    // change template object list when template type selection changes
                    final CComponent<?, EmailTemplateType, ?> comp = get(proto().type());
                    comp.addValueChangeHandler(new ValueChangeHandler<EmailTemplateType>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<EmailTemplateType> event) {
                            vm.setItems(templateObjects.get(event.getValue()));
                        }
                    });
                    comp.addPropertyChangeHandler(new PropertyChangeHandler() {
                        @Override
                        public void onPropertyChange(PropertyChangeEvent event) {
                            if (event.isEventOfType(PropertyChangeEvent.PropertyName.repopulated)) {
                                vm.setItems(templateObjects.get(comp.getValue()));
                            }
                        }
                    });
                }
                return formPanel;
            }

            static class TemplateVarSelector extends PopupPanel {
                private String selectedValue;

                private Command selectionHandler;

                private final TemplateVarMenu menu = new TemplateVarMenu();

                public TemplateVarSelector() {
                    super(true);
                }

                public void setSelectionHandler(Command handler) {
                    selectionHandler = handler;
                }

                public String getSelectedValue() {
                    return selectedValue != null ? "${" + selectedValue + "}" : "";
                }

                public void showBelow(UIObject target) {
                    selectedValue = null;
                    if (getWidget() == null) {
                        addCloseHandler(new CloseHandler<PopupPanel>() {
                            @Override
                            public void onClose(CloseEvent<PopupPanel> event) {
                                if (selectionHandler != null) {
                                    selectionHandler.execute();
                                }
                            }
                        });

                        add(menu);
                    }
                    addAutoHidePartner(target.getElement());
                    showRelativeTo(target);
                }

                public void setItems(List<String> items) {
                    if (items == null) {
                        return;
                    }
                    menu.clearItems();
                    for (final String item : items) {
                        TemplateVarMenu parent = menu;
                        String[] segments = item.split("\\.");
                        for (int i = 0; i < segments.length; i++) {
                            String segment = segments[i];
                            MenuItem mi = parent.findItem(segment);
                            if (mi == null) {
                                final MenuItem menuItem = new MenuItem(segment, (Command) null);
                                menuItem.setTitle(item);
                                if (i == segments.length - 1) {
                                    // last item can be selected
                                    menuItem.setCommand(new Command() {
                                        @Override
                                        public void execute() {
                                            selectedValue = menuItem.getTitle();
                                            hide();
                                        }
                                    });
                                } else {
                                    menuItem.setSubMenu(new TemplateVarMenu());
                                }
                                parent.addItem(menuItem);
                                mi = menuItem;
                            }
                            parent = (TemplateVarMenu) mi.getSubMenu();
                        }
                    }
                }

                static class TemplateVarMenu extends MenuBar {
                    public TemplateVarMenu() {
                        super(true);
                        setAutoOpen(true);
                        setAnimationEnabled(true);
                    }

                    public MenuItem findItem(String text) {
                        for (MenuItem item : getItems()) {
                            if (text.equals(item.getText())) {
                                return item;
                            }
                        }
                        return null;
                    }
                }
            }
        }
    }
}
