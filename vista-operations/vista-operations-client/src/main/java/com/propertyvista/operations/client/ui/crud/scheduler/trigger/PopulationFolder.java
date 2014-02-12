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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;

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
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(new EntityFolderColumnDescriptor(proto().pmc(), "40em", true));
    }

    @Override
    protected void addItem() {
        new EntitySelectorTableVisorController<Pmc>(parentForm.getParentView(), Pmc.class, true, getAlreadySelected(), i18n.tr("Select Pmc")) {

            @Override
            public void onClickOk() {
                for (Pmc item : getSelectedItems()) {
                    TriggerPmc tPmc = EntityFactory.create(TriggerPmc.class);
                    tPmc.pmc().set(item);
                    addItem(tPmc);
                }
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off                    
                        new MemberColumnDescriptor.Builder(proto().name()).build(),
                        new MemberColumnDescriptor.Builder(proto().dnsName()).build(),
                        new MemberColumnDescriptor.Builder(proto().created()).build()
                ); //@formatter:on
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().name(), false));
            }

            @Override
            protected AbstractListService<Pmc> getSelectService() {
                return GWT.<AbstractListService<Pmc>> create(SelectPmcListService.class);
            }
        }.show();
    }

    private List<Pmc> getAlreadySelected() {
        List<Pmc> alreadySelected = new LinkedList<Pmc>();
        for (TriggerPmc item : getValue()) {
            alreadySelected.add(item.pmc());
        }
        return alreadySelected;
    }

}