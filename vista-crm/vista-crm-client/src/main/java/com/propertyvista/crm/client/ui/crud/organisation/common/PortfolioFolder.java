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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioFolder extends VistaTableFolder<Portfolio> {

    private final static I18n i18n = I18n.get(PortfolioFolder.class);

    public PortfolioFolder(boolean modifiable) {
        super(Portfolio.class, modifiable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return java.util.Arrays.asList(new EntityFolderColumnDescriptor(proto().name(), "20em"),
                new EntityFolderColumnDescriptor(proto().description(), "30em"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Portfolio) {
            return new CEntityFolderRowEditor<Portfolio>(Portfolio.class, columns()) {
                @SuppressWarnings("rawtypes")
                @Override
                protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                    CComponent<?> comp = null;

                    if (proto().name() == column.getObject()) {
                        comp = inject(column.getObject(), new CLabel<String>());
                        ((CField) comp).setNavigationCommand(new Command() {
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
        } else {
            return super.create(member);
        }
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
        new PortfolioSelectorDialog(getValue()).show();
    }

    private class PortfolioSelectorDialog extends EntitySelectorTableDialog<Portfolio> {

        public PortfolioSelectorDialog(List<Portfolio> alreadySelected) {
            super(Portfolio.class, true, alreadySelected, i18n.tr("Select Portfolio"));
            setWidth("700px");
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
            return Arrays.asList(new Sort(proto().name().getPath().toString(), false));
        }

        @Override
        protected AbstractListService<Portfolio> getSelectService() {
            return GWT.<AbstractListService<Portfolio>> create(SelectPortfolioListService.class);
        }

        @Override
        public boolean onClickOk() {
            for (Portfolio selected : getSelectedItems()) {
                addItem(selected);
            }
            return true;
        }
    }
}
