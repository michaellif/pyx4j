/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.eviction.EvictionStatusEditorBase.EvictionStepSelectionHandler;
import com.propertyvista.crm.client.ui.crud.lease.eviction.n4.N4EvictionStatusEditor;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.eviction.EvictionCaseStatus;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseForm extends CrmEntityForm<EvictionCaseDTO> {

    private static final I18n i18n = I18n.get(EvictionCaseForm.class);

    private final boolean uploadable;

    private final StatusHistoryFolder historyFolder = new StatusHistoryFolder();

    public EvictionCaseForm(IPrimeFormView<EvictionCaseDTO, ?> view, boolean uploadable) {
        super(EvictionCaseDTO.class, view);
        this.uploadable = uploadable;

        FormPanel formPanel = new FormPanel(this);

        CEntityLabel<Lease> leaseLabel = isEditable() ? new CEntityLabel<Lease>() : new CEntityCrudHyperlink<Lease>(
                AppPlaceEntityMapper.resolvePlace(Lease.class));
        formPanel.append(Location.Left, proto().lease(), leaseLabel).decorate();
        formPanel.append(Location.Left, proto().createdBy(), new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class))).decorate();
        formPanel.append(Location.Right, proto().createdOn()).decorate();
        formPanel.append(Location.Right, proto().updatedOn()).decorate();
        formPanel.append(Location.Right, proto().closedOn()).decorate();
        formPanel.append(Location.Dual, proto().note()).decorate();

        formPanel.h1(i18n.tr("Status History"));
        formPanel.append(Location.Dual, proto().history(), historyFolder);

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        boolean isNew = getValue().getPrimaryKey() == null;
        if (isNew) {
            get(proto().lease()).setVisible(false);
            get(proto().createdOn()).setVisible(false);
            get(proto().createdBy()).setVisible(false);
            get(proto().updatedOn()).setVisible(false);
            get(proto().closedOn()).setVisible(false);
        }
    }

    class StatusHistoryFolder extends VistaBoxFolder<EvictionCaseStatus> {

        private final EvictionStepSelectionHandler stepSelectionHandler = new EvictionStepSelectionHandler() {

            @Override
            public Set<EvictionFlowStep> getAvailableSteps() {
                Set<EvictionFlowStep> availableSteps = new HashSet<>(EvictionCaseForm.this.getValue().evictionFlowPolicy().evictionFlow());
                for (EvictionCaseStatus status : getValue()) {
                    availableSteps.remove(status.evictionStep());
                }
                return availableSteps;
            }
        };

        public StatusHistoryFolder() {
            super(EvictionCaseStatus.class);
        }

        @Override
        protected CFolderItem<EvictionCaseStatus> createItem(boolean first) {
            // need own folder item implementation to handle polymorphic EvictionStatus entity
            return new StatusHistoryFolderItem();
        }

        @Override
        protected CForm<? extends EvictionStatus> createItemForm(IObject<?> member) {
            // not used - the item form is created by StatusHistoryFolderItem
            return null;
        }

        @Override
        public VistaBoxFolderItemDecorator<EvictionCaseStatus> createItemDecorator() {
            VistaBoxFolderItemDecorator<EvictionCaseStatus> itemDecorator = super.createItemDecorator();
            itemDecorator.setExpended(false);
            return itemDecorator;
        }

        @Override
        protected void onValueSet(boolean populate) {
            setAddable(hasMoreSteps());
        }

        private boolean hasMoreSteps() {
            EvictionCaseDTO evictionCase = EvictionCaseForm.this.getValue();
            return evictionCase == null ? false : evictionCase.evictionFlowPolicy().evictionFlow().size() > getValue().size();
        }

        class StatusHistoryFolderItem extends CFolderItem<EvictionCaseStatus> {

            private EvictionStatusEditorBase<? extends EvictionCaseStatus> statusEditor;

            public StatusHistoryFolderItem() {
                super(EvictionCaseStatus.class);
            }

            @Override
            public IFolderItemDecorator<EvictionCaseStatus> createItemDecorator() {
                return StatusHistoryFolder.this.createItemDecorator();
            }

            @Override
            protected CForm<? extends EvictionCaseStatus> createItemForm(IObject<?> member) {
                EvictionStatusEditorBase<? extends EvictionCaseStatus> itemForm = null;
                if (statusEditor == null) {
                    // Just base editor to allow stepType selection
                    itemForm = new EvictionStatusEditorBase<EvictionCaseStatus>(EvictionCaseStatus.class, stepSelectionHandler);
                } else {
                    itemForm = statusEditor;
                }
                // trigger editor revalidation via preprocessValue() on flow step change
                itemForm.getStepSelector().addValueChangeHandler(new ValueChangeHandler<EvictionFlowStep>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<EvictionFlowStep> event) {
                        setStatusEditor(event.getValue());
                    }
                });

                return itemForm;
            }

            @Override
            protected void onValueSet(boolean populate) {
                if (getValue() != null) {
                    setStatusEditor(getValue().evictionStep());
                }
            }

            private void setStatusEditor(EvictionFlowStep flowStep) {
                if (flowStep.stepType().isNull()) {
                    return;
                }

                EvictionCaseStatus value = getValue();
                EvictionCaseStatus newValue = value;
                boolean newEditor = false;

                switch (flowStep.stepType().getValue()) {
                case N4:
                    if (statusEditor == null || statusEditor.getClass() != N4EvictionStatusEditor.class) {
                        statusEditor = new N4EvictionStatusEditor(stepSelectionHandler, uploadable);
                        newEditor = true;
                        if (value.getInstanceValueClass() != EvictionStatusN4.class) {
                            newValue = EntityFactory.create(EvictionStatusN4.class);
                        }
                    }
                    break;
                default:
                    if (statusEditor == null || statusEditor.getClass() != EvictionStatusEditor.class) {
                        statusEditor = new EvictionStatusEditor<EvictionStatus>(EvictionStatus.class, stepSelectionHandler, uploadable);
                        newEditor = true;
                        if (value.getInstanceValueClass() != EvictionStatus.class) {
                            newValue = EntityFactory.create(EvictionStatus.class);
                        }
                    }
                    break;
                }

                if (newEditor) {
                    if (newValue != value) {
                        newValue.evictionStep().set(flowStep);
                        int idx = StatusHistoryFolder.this.getValue().indexOf(value);
                        StatusHistoryFolder.this.getValue().set(idx, newValue);
                        value = newValue;
                    }
                    createContent();
                    getNativeComponent().setContent(statusEditor);
                    addValidations();
                    populate(value);
                }
            }
        }
    }
}
