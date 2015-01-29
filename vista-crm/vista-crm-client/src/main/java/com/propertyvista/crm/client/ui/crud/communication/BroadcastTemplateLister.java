/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.shared.IntegrityConstraintUserRuntimeException;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.filter.ValidationLabel;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.rpc.services.BroadcastTemplateCrudService;
import com.propertyvista.domain.communication.BroadcastTemplate;
import com.propertyvista.domain.communication.BroadcastTemplate.AudienceType;
import com.propertyvista.domain.communication.DeliveryHandle.MessageType;

public class BroadcastTemplateLister extends SiteDataTablePanel<BroadcastTemplate> {
    private static final I18n i18n = I18n.get(BroadcastTemplateLister.class);

    private Button newButton;

    public BroadcastTemplateLister() {
        super(BroadcastTemplate.class, GWT.<AbstractCrudService<BroadcastTemplate>> create(BroadcastTemplateCrudService.class));

        addUpperActionItem(newButton = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New Broadcast Template"), new Command() {
            @Override
            public void execute() {
            }
        }));

        Button.ButtonMenuBar subMenu = new Button.ButtonMenuBar();

        subMenu.addItem(new MenuItem(i18n.tr("Customer"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Customer);
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Tenant"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Tenant);
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Guarantor"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Guarantor);
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Prospect"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Prospect);
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Employee"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Employee);
            }
        }));

        newButton.setMenu(subMenu);
        newButton.setPermission(DataModelPermission.permissionCreate(BroadcastTemplate.class));

        setDeleteActionEnabled(true);
        setColumnDescriptors(new ColumnDescriptor[] { //
        new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().subject()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().category()).build(), //
                new ColumnDescriptor.Builder(proto().highImportance()).build() //
        });

        setDataTableModel(new DataTableModel<BroadcastTemplate>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }

    private void editNewEntity(final Object placeCriteria) {
        new TemplateNameInputDialog("Template name") {
            @Override
            public boolean onClickOk() {
                if (inputNameTextBox.getValue() == null || inputNameTextBox.getValue().trim().equals("")) {
                    nameBoxValidationLabel.setMessage(i18n.tr("The template name cannot be blank"));
                    return false;
                } else {
                    getService().list(new AsyncCallback<EntitySearchResult<BroadcastTemplate>>() {

                        @Override
                        public void onSuccess(EntitySearchResult<BroadcastTemplate> result) {
                            for (BroadcastTemplate current : result.getData()) {
                                if (current.name().getValue().equals(inputNameTextBox.getValue())) {
                                    nameBoxValidationLabel.setMessage(i18n.tr("The template name {0} already exists. Please change template name.",
                                            inputNameTextBox.getValue()));
                                    return;
                                }
                            }
                            nameBoxValidationLabel.clear();
                            editNew(inputNameTextBox.getValue(), placeCriteria);
                            hide(true);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            if (caught instanceof IntegrityConstraintUserRuntimeException) {
                                MessageDialog.error(i18n.tr("Get Broadcast templates"), caught.getMessage());
                            } else {
                                throw new UnrecoverableClientError(caught);
                            }
                        }
                    }, new EntityListCriteria<BroadcastTemplate>(BroadcastTemplate.class));
                }
                return false;
            }
        }.show();
    }

    private void editNew(String inputValue, final Object placeCriteria) {
        final BroadcastTemplateCrudService.BroadcastTemplateInitializationData initData = EntityFactory
                .create(BroadcastTemplateCrudService.BroadcastTemplateInitializationData.class);
        initData.name().setValue(inputValue);
        if (placeCriteria != null) {
            initData.audienceType().setValue((AudienceType) placeCriteria);
            if (((AudienceType) placeCriteria).equals(AudienceType.Employee)) {
                initData.messageType().setValue(MessageType.Organizational);
            } else {
                initData.messageType().setValue(MessageType.Informational);
            }
        } else {
            initData.audienceType().setValue(null); // should not appear
            initData.messageType().setValue(null);
        }
        editNew(com.propertyvista.crm.rpc.CrmSiteMap.Communication.BroadcastTemplate.class, initData);
    }

    private class TemplateNameInputDialog extends OkCancelDialog {

        protected final TextBox<String> inputNameTextBox;

        protected final ValidationLabel nameBoxValidationLabel;

        public TemplateNameInputDialog(String caption) {
            super(caption);
            FlowPanel contentPanel = new FlowPanel();
            //           initWidget(contentPanel);
            contentPanel.add(new Label(i18n.tr("Template Name")));

            inputNameTextBox = new TextBox<String>();
            contentPanel.add(inputNameTextBox);
            nameBoxValidationLabel = new ValidationLabel(inputNameTextBox);
            nameBoxValidationLabel.getElement().getStyle().setColor("red");
            contentPanel.add(nameBoxValidationLabel);

            inputNameTextBox.setParser(new IParser<String>() {
                @Override
                public String parse(String string) throws ParseException {
                    if (string != null && !string.trim().equals("") && !CommonsStringUtils.isEmpty(string)) {
                        return string;
                    } else {
                        throw new ParseException(i18n.tr("The template name cannot be empty."), 0);
                    }
                }
            });

            inputNameTextBox.setFormatter(new IFormatter<String, String>() {

                @Override
                public String format(String value) {
                    return value.toString();
                }
            });

            inputNameTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    nameBoxValidationLabel.setMessage(inputNameTextBox.isParsedOk() ? null : inputNameTextBox.getParseExceptionMessage());
                }
            });

            setBody(contentPanel.asWidget());
        }

        @Override
        public boolean onClickOk() {
            return false;
        }

    }
}
