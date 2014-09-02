/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.activity.crud.communication.MessageEditorActivity;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.EmployeeSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.PortfolioSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.tools.common.selectors.CommunicationEndpointSelector;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class MessageEditForm extends CrmEntityForm<MessageDTO> {

    private static final I18n i18n = I18n.get(MessageEditForm.class);

    private FormPanel searchCriteriaPanel;

    private Button.ButtonMenuBar subMenu;

    private final Button actionsButton;

    private Widget to;

    private Widget h3;

    private Widget newLine;

    CommunicationEndpointSelector epSelector;

    public MessageEditForm(IForm<MessageDTO> view) {
        super(MessageDTO.class, view);
        setTabBarVisible(false);
        actionsButton = new Button(i18n.tr("Select Recipients"));
        selectTab(addTab(createGeneralForm(), i18n.tr("New message")));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public IsWidget createGeneralForm() {
        FormPanel formPanel = new FormPanel(this);

        to = formPanel.h4("To");
        subMenu = new Button.ButtonMenuBar();
        subMenu.addItem(new MenuItem(i18n.tr("Tenant"), new Command() {
            @Override
            public void execute() {
                new TenantSelectorDialog(MessageEditForm.this.getParentView(), true) {
                    @Override
                    public void onClickOk() {
                        Collection<Tenant> ts = getSelectedItems();
                        if (ts != null && getSelectedItems().size() > 0) {
                            for (Tenant selected : ts) {
                                addRecipient(selected);
                            }
                        }
                    }
                }.show();
            }
        }));

        subMenu.addItem(new MenuItem(i18n.tr("Corporate"), new Command() {
            @Override
            public void execute() {
                new EmployeeSelectorDialog(MessageEditForm.this.getParentView(), true) {
                    @Override
                    public void onClickOk() {
                        onAdd(getSelectedItems());
                    }
                }.show();
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Building"), new Command() {
            @Override
            public void execute() {
                BuildingSelectorDialog dialog = new BuildingSelectorDialog(MessageEditForm.this.getParentView(), true) {
                    @Override
                    public void onClickOk() {
                        onAdd(getSelectedItems());
                    }
                };
                dialog.getCancelButton().setVisible(true);
                dialog.show();
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Portfolio"), new Command() {
            @Override
            public void execute() {
                new PortfolioSelectorDialog(MessageEditForm.this.getParentView(), true) {
                    @Override
                    public void onClickOk() {
                        onAdd(getSelectedItems());
                    }
                }.show();
            }
        }));

        actionsButton.setMenu(subMenu);
        searchCriteriaPanel = new FormPanel(this);
        searchCriteriaPanel.append(Location.Dual, createCommunicationEndpointSelector());
        formPanel.append(Location.Dual, searchCriteriaPanel);
        formPanel.h4("", actionsButton);

        h3 = formPanel.h3("");
        newLine = formPanel.br();
        formPanel.append(Location.Dual, proto().subject()).decorate();

        formPanel.append(Location.Dual, proto().category(), new CEntityComboBox<MessageCategory>(MessageCategory.class) {
            @Override
            public void retriveOptions(final AsyncOptionsReadyCallback<MessageCategory> callback) {
                resetCriteria();
                resetOptions();
                if (getParentView() == null || getParentView().getPresenter() == null) {
                    retriveOptionsPrivate(callback);
                } else {
                    MessageEditorActivity presenter = ((MessageEditorActivity) getParentView().getPresenter());
                    final CategoryType categoryType = presenter.getCategoryType();
                    if (categoryType == null) {
                        retriveOptionsPrivate(callback);
                    } else {
                        final PropertyCriterion crit = PropertyCriterion.eq(proto().categoryType(), categoryType);

                        resetCriteria();
                        addCriterion(crit);

                        resetOptions();
                        retriveOptionsPrivate(callback);
                    }
                }
            }

            private void retriveOptionsPrivate(final AsyncOptionsReadyCallback<MessageCategory> callback) {
                super.retriveOptions(new AsyncOptionsReadyCallback<MessageCategory>() {
                    @Override
                    public void onOptionsReady(List<MessageCategory> opt) {

                        if (callback != null && opt != null) {
                            callback.onOptionsReady(opt);
                        }
                        /*-if (opt.size() > 0) {
                            if (MessageGroupCategory.Ticket.equals(opt.get(0).category().getValue())) {
                                setToVisible(false);
                            } else {
                                setToVisible(true);

                            }
                        }-*/
                    }
                });
            }
        }).decorate();

        get(proto().category());

        formPanel.append(Location.Left, proto().allowedReply()).decorate();
        formPanel.append(Location.Right, proto().highImportance()).decorate();
        formPanel.br();
        formPanel.append(Location.Dual, proto().text()).decorate();
        formPanel.append(Location.Dual, proto().attachments(), new MessageAttachmentFolder());
        formPanel.br();

        return formPanel;
    }

    @Override
    protected MessageDTO preprocessValue(MessageDTO value, boolean fireEvent, boolean populate) {
        setToVisible(true);
        if (value == null || value.getPrimaryKey() == null || value.getPrimaryKey().isDraft()) {
            MessageCategory mc = ((MessageEditorActivity) getParentView().getPresenter()).getCategory();
            if (mc != null && !mc.isNull()) {
                get(proto().category()).setEditable(false);
                if (CategoryType.Ticket.equals(mc.categoryType().getValue())) {
                    setToVisible(false);
                }
            } else {
                get(proto().category()).setEditable(true);
            }
            CategoryType mgc = ((MessageEditorActivity) getParentView().getPresenter()).getCategoryType();
            if (mgc != null) {
                setToVisible(!CategoryType.Ticket.equals(mgc));
            }
        }
        return value;
    }

    private void setToVisible(boolean isVisible) {
        searchCriteriaPanel.setVisible(isVisible);
        actionsButton.setVisible(isVisible);
        to.setVisible(isVisible);
        h3.setVisible(isVisible);
        newLine.setVisible(isVisible);
    }

    public void reinit() {
        epSelector.removeAll();
    }

    public void addToItem(CommunicationEndpointDTO item) {
        getValue().to().add(item);
    }

    public void removeToItem(CommunicationEndpointDTO item) {
        getValue().to().remove(item);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
    }

    private CommunicationEndpointSelector createCommunicationEndpointSelector() {
        return epSelector = new CommunicationEndpointSelector() {//@formatter:off
            @Override protected void onItemAdded(CommunicationEndpointDTO item) {
                super.onItemAdded(item);
                MessageEditForm.this.addToItem(item);
             }
            @Override
            protected void onItemRemoved(CommunicationEndpointDTO item) {
                MessageEditForm.this.removeToItem(item);
            }


        };//@formatter:on
    }

    private void onAdd(Collection<? extends IEntity> eps) {
        if (eps != null && eps.size() > 0) {
            for (IEntity selected : eps) {
                if (!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(selected.getPrimaryKey())) {
                    addRecipient(selected);
                }
            }
        }
    }

    private void addRecipient(IEntity selected) {
        CommunicationEndpointDTO proto = EntityFactory.create(CommunicationEndpointDTO.class);
        Class<? extends IEntity> epType = selected.getInstanceValueClass();
        if (epType.equals(Building.class)) {
            proto.name().set(((Building) selected).propertyCode());
            proto.type().setValue(ContactType.Building);
            CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
            cg.building().set(selected);
            proto.endpoint().set(cg);
        } else if (epType.equals(Portfolio.class)) {
            proto.name().set(((Portfolio) selected).name());
            proto.type().setValue(ContactType.Portfolio);
            CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
            cg.portfolio().set(selected);
            proto.endpoint().set(cg);
        } else if (epType.equals(Employee.class)) {
            proto.name().setValue(((Employee) selected).name().getStringView());
            proto.type().setValue(ContactType.Employee);
            proto.endpoint().set(selected);
        } else if (epType.equals(Tenant.class)) {
            proto.name().setValue(((Tenant) selected).customer().person().name().getStringView());
            proto.type().setValue(ContactType.Tenant);
            proto.endpoint().set(selected);
        }

        epSelector.addItem(proto);
    }
}
