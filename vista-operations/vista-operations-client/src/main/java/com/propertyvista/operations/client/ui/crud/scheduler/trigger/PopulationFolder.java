/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author michaellif
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.TriggerPmc;
import com.propertyvista.operations.rpc.services.scheduler.SelectPmcListService;

public class PopulationFolder extends VistaTableFolder<TriggerPmc> {

    private static final I18n i18n = I18n.get(PopulationFolder.class);

    private final OperationsEntityForm<?> parentForm;

    public PopulationFolder(OperationsEntityForm<?> parentForm) {
        super(TriggerPmc.class, parentForm.isEditable());
        this.parentForm = parentForm;
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(new FolderColumnDescriptor(proto().pmc(), "40em", true));
    }

    @Override
    protected void addItem() {
        new EntitySelectorTableDialog<Pmc>(Pmc.class, true, getAlreadySelected(), i18n.tr("Select Pmc")) {

            @Override
            public boolean onClickOk() {
                for (Pmc item : getSelectedItems()) {
                    TriggerPmc tPmc = EntityFactory.create(TriggerPmc.class);
                    tPmc.pmc().set(item);
                    addItem(tPmc);
                }
                return true;
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off                    
                        new ColumnDescriptor.Builder(proto().name()).build(),
                        new ColumnDescriptor.Builder(proto().dnsName()).build(),
                        new ColumnDescriptor.Builder(proto().created()).build(),
                        new ColumnDescriptor.Builder(proto().features().yardiIntegration()).build(),
                        new ColumnDescriptor.Builder(proto().features().onlineApplication()).build(),
                        new ColumnDescriptor.Builder(proto().features().whiteLabelPortal()).build(),
                        new ColumnDescriptor.Builder(proto().features().yardiMaintenance()).build()
                ); //@formatter:on
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().name(), false));
            }

            @Override
            protected AbstractListCrudService<Pmc> getSelectService() {
                return GWT.<AbstractListCrudService<Pmc>> create(SelectPmcListService.class);
            }
        }.show();
    }

    private Set<Pmc> getAlreadySelected() {
        HashSet<Pmc> alreadySelected = new HashSet<>();
        for (TriggerPmc item : getValue()) {
            alreadySelected.add(item.pmc());
        }
        return alreadySelected;
    }

}