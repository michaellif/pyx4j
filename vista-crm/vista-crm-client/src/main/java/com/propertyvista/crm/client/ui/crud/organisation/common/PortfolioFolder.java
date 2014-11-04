/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.boxes.PortfolioSelectionDialog;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioFolder extends VistaTableFolder<Portfolio> {

    private final IPane parentView;

    public PortfolioFolder(IPane parentView, boolean isEditable) {
        super(Portfolio.class, isEditable);
        this.parentView = parentView;
        setOrderable(false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(new FolderColumnDescriptor(proto().name(), "20em"), new FolderColumnDescriptor(proto().description(), "30em"));
    }

    @Override
    protected CForm<Portfolio> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<Portfolio>(Portfolio.class, columns()) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                CField<?, ?> comp = null;

                if (proto().name() == column.getObject()) {
                    comp = inject(column.getObject(), new CLabel<String>());
                    comp.setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new CrmSiteMap.Organization.Portfolio().formViewerPlace(getValue().id().getValue()));
                        }
                    });
                } else if (proto().description() == column.getObject()) {
                    comp = inject(column.getObject(), new CLabel<String>());
                } else {
                    comp = super.createCell(column);
                }

                return comp;
            }
        };
    }

    @Override
    protected IFolderDecorator<Portfolio> createFolderDecorator() {
        return new VistaTableFolderDecorator<Portfolio>(this, this.isEditable()) {
            {
                setShowHeader(false);
            }
        };
    }

    @Override
    protected void addItem() {
        new PortfolioSelectorDialogExtraFilters(new HashSet<>(getValue())) {
            @Override
            public boolean onClickOk() {
                for (Portfolio selected : getSelectedItems()) {
                    addItem(selected);
                }
                return true;
            }
        }.show();
    }

    private abstract class PortfolioSelectorDialogExtraFilters extends PortfolioSelectionDialog {

        public PortfolioSelectorDialogExtraFilters(Set<Portfolio> alreadySelected) {
            super(true, alreadySelected);
        }

        @Override
        protected void setFilters(List<Criterion> filters) {
            super.setFilters(filters);

            if (parentView != null && parentView instanceof EmployeeForm) {
                EmployeeForm employeeForm = (EmployeeForm) parentView;
                if (employeeForm.isRestrictAccessSet()) {
                    List<Portfolio> portfolioAccess = employeeForm.getPortfolioAccess();
                    if (portfolioAccess != null && !portfolioAccess.isEmpty()) {
                        List<Key> portfolioAccessKeys = new ArrayList<Key>(portfolioAccess.size());
                        for (Portfolio entity : portfolioAccess) {
                            portfolioAccessKeys.add(entity.getPrimaryKey());
                        }
                        addFilter(PropertyCriterion.in(EntityFactory.getEntityPrototype(Portfolio.class).id(), portfolioAccessKeys));
                    } else {
                        addFilter(PropertyCriterion.isNull(EntityFactory.getEntityPrototype(Portfolio.class).id()));
                    }
                }
            }
        }
    }
}
