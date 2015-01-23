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

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.eviction.EvictionStatusEditor.EvictionStepSelectionHandler;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.eviction.EvictionStatus;
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
        formPanel.append(Location.Dual, proto().lease(), leaseLabel).decorate();
        formPanel.append(Location.Dual, proto().createdOn()).decorate();
        formPanel.append(Location.Dual, proto().createdBy(), new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class))).decorate();
        formPanel.append(Location.Dual, proto().updatedOn()).decorate();
        formPanel.append(Location.Dual, proto().closedOn()).decorate();
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

    class StatusHistoryFolder extends VistaBoxFolder<EvictionStatus> {

        public StatusHistoryFolder() {
            super(EvictionStatus.class);
            addValueChangeHandler(new ValueChangeHandler<IList<EvictionStatus>>() {

                @Override
                public void onValueChange(ValueChangeEvent<IList<EvictionStatus>> event) {
                    setAddable(hasMoreSteps());
                }
            });
        }

        @Override
        protected CForm<? extends EvictionStatus> createItemForm(IObject<?> member) {
            return new EvictionStatusEditor( //
                    new EvictionStepSelectionHandler() {

                        @Override
                        public Set<EvictionFlowStep> getAvailableSteps() {
                            Set<EvictionFlowStep> availableSteps = new HashSet<>(EvictionCaseForm.this.getValue().evictionFlowPolicy().evictionFlow());
                            for (EvictionStatus status : StatusHistoryFolder.this.getValue()) {
                                if (!status.evictionStep().equals(getValue())) {
                                    availableSteps.remove(status.evictionStep());
                                }
                            }
                            return availableSteps;
                        }
                    }, uploadable);
        }

        @Override
        public VistaBoxFolderItemDecorator<EvictionStatus> createItemDecorator() {
            VistaBoxFolderItemDecorator<EvictionStatus> itemDecorator = super.createItemDecorator();
            itemDecorator.setExpended(false);
            return itemDecorator;
        }

        private boolean hasMoreSteps() {
            EvictionCaseDTO evictionCase = EvictionCaseForm.this.getValue();
            return evictionCase == null ? false : evictionCase.evictionFlowPolicy().evictionFlow().size() > getValue().size();
        }
    }
}
