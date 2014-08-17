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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.activity.crud.communication.MessageEditorActivity;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.tools.common.selectors.CommunicationEndpointSelector;
import com.propertyvista.crm.rpc.services.selections.SelectCrmUserListService;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class MessageEditForm extends CrmEntityForm<MessageDTO> {

    private static final I18n i18n = I18n.get(MessageEditForm.class);

    private FormPanel searchCriteriaPanel;

    private Button.ButtonMenuBar subMenu;

    private final Button actionsButton;

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

        formPanel.h4("To");
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
                new CommunicationEndpointSelectorDialog<CrmUser>(MessageEditForm.this.getParentView(), CrmUser.class) {

                    @Override
                    protected AbstractListCrudService<CrmUser> getSelectService() {
                        return GWT.<AbstractListCrudService<CrmUser>> create(SelectCrmUserListService.class);
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
                new PortfolioSelectorDialog(MessageEditForm.this.getParentView()) {
                    @Override
                    public void onClickOk() {
                        onAdd(getSelectedItems());
                    }
                }.show();
            }
        }));

        subMenu.addItem(new MenuItem(i18n.tr("Unit"), new Command() {
            @Override
            public void execute() {
                new UnitSelectorDialog(MessageEditForm.this.getParentView(), true) {
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

        formPanel.h3("");
        formPanel.br();
        formPanel.append(Location.Dual, proto().subject()).decorate();

        formPanel.append(Location.Dual, proto().topic(), new CEntityComboBox<MessageCategory>(MessageCategory.class) {
            @Override
            public void retriveOptions(final AsyncOptionsReadyCallback<MessageCategory> callback) {
                resetCriteria();
                resetOptions();
                if (getParentView() == null || getParentView().getPresenter() == null) {
                    retriveOptionsPrivate(callback);
                } else {
                    MessageEditorActivity presenter = ((MessageEditorActivity) getParentView().getPresenter());
                    final MessageGroupCategory categoryType = presenter.getCategoryType();
                    if (categoryType == null) {
                        retriveOptionsPrivate(callback);
                    } else {
                        final PropertyCriterion crit = PropertyCriterion.eq(proto().category(), categoryType);

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
                    }
                });
            }
        }).decorate();

        get(proto().topic());

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
        if (value == null || value.getPrimaryKey() == null || value.getPrimaryKey().isDraft()) {
            MessageCategory mc = ((MessageEditorActivity) getParentView().getPresenter()).getCategory();
            if (mc != null && !mc.isNull()) {
                get(proto().topic()).setEditable(false);
            } else {
                get(proto().topic()).setEditable(true);
            }
        }
        return value;
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

    private abstract class PortfolioSelectorDialog extends EntitySelectorTableVisorController<Portfolio> {

        public PortfolioSelectorDialog(IPane parentView) {
            super(parentView, Portfolio.class, true, null, i18n.tr("Select Portfolio"));
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().description()).wordWrap(true).build()
            ); //@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        protected AbstractListCrudService<Portfolio> getSelectService() {
            return GWT.<AbstractListCrudService<Portfolio>> create(SelectPortfolioListService.class);
        }
    }

    private abstract class CommunicationEndpointSelectorDialog<E extends AbstractPmcUser> extends EntitySelectorTableVisorController<E> {

        public CommunicationEndpointSelectorDialog(IPane parentView, Class<E> entityClass) {
            super(parentView, entityClass, true, i18n.tr("Select User"));
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(new MemberColumnDescriptor.Builder(proto().name()).searchable(true).build());
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        public void onClickOk() {
            onAdd(getSelectedItems());
        }
    }

    private void onAdd(Collection<? extends CommunicationEndpoint> eps) {
        if (eps != null && eps.size() > 0) {
            for (CommunicationEndpoint selected : eps) {
                if (!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(selected.getPrimaryKey())) {
                    addRecipient(selected);
                }
            }
        }
    }

    private void addRecipient(CommunicationEndpoint selected) {
        CommunicationEndpointDTO proto = EntityFactory.create(CommunicationEndpointDTO.class);
        Class<? extends IEntity> epType = selected.getInstanceValueClass();
        if (epType.equals(Building.class)) {
            proto.name().set(((Building) selected).propertyCode());
            proto.type().setValue(ContactType.Building);
        } else if (epType.equals(Portfolio.class)) {
            proto.name().set(((Portfolio) selected).name());
            proto.type().setValue(ContactType.Portfolio);
        }
        if (epType.equals(AptUnit.class)) {
            proto.name().setValue(((AptUnit) selected).getStringView());
            proto.type().setValue(ContactType.Unit);
        } else if (epType.equals(CustomerUser.class)) {
            proto.name().set(((CustomerUser) selected).name());
            proto.type().setValue(ContactType.Tenant);
        } else if (epType.equals(CrmUser.class)) {
            proto.name().set(((CrmUser) selected).name());
            proto.type().setValue(ContactType.Employee);
        } else if (epType.equals(Tenant.class)) {
            proto.name().setValue(((Tenant) selected).customer().person().name().getStringView());
            proto.type().setValue(ContactType.Tenant);
        }

        proto.endpoint().set(selected);
        epSelector.addItem(proto);
    }
}
