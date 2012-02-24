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
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioFolder extends VistaTableFolder<Portfolio> {

    private final static I18n i18n = I18n.get(PortfolioFolder.class);

    public PortfolioFolder(boolean isModifyable) {
        super(Portfolio.class, isModifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return java.util.Arrays.asList(new EntityFolderColumnDescriptor(proto().name(), "15em"),

        new EntityFolderColumnDescriptor(proto().description(), "20em"));
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Portfolio) {
            return new CEntityFolderRowEditor<Portfolio>(Portfolio.class, columns()) {
                @Override
                protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                    CComponent<?, ?> comp = null;
                    if (proto().name() == column.getObject()) {
                        if (isEditable()) {
                            comp = inject(column.getObject(), new CLabel());
                        } else {
                            comp = new CHyperlink(new Command() {
                                @Override
                                public void execute() {
                                    AppSite.getPlaceController().goTo(
                                            AppSite.getHistoryMapper().createPlace(CrmSiteMap.Organization.Portfolio.class)
                                                    .formViewerPlace(getValue().id().getValue()));
                                }
                            });
                            comp = inject(column.getObject(), comp);
                        }
                    } else if (proto().description() == column.getObject()) {
                        comp = inject(column.getObject(), new CLabel());
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
    protected IFolderDecorator<Portfolio> createDecorator() {
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

    private class PortfolioSelectorDialog extends EntitySelectorDialog<Portfolio> {

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
