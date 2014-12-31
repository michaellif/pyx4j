/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectionDialog;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService.N4BatchInitData;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchLister extends SiteDataTablePanel<N4BatchDTO> {

    private static final I18n i18n = I18n.get(N4BatchLister.class);

    public N4BatchLister() {
        super(N4BatchDTO.class, GWT.<AbstractCrudService<N4BatchDTO>> create(N4BatchCrudService.class), true);

        setColumnDescriptors(createColumnDescriptors());
        setDataTableModel(new DataTableModel<N4BatchDTO>());
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        N4BatchDTO proto = EntityFactory.getEntityPrototype(N4BatchDTO.class);

        return new ColumnDescriptor[] { //
        new ColumnDescriptor.Builder(proto.name()).build(), //
                new ColumnDescriptor.Builder(proto.created()).build(), //
                new ColumnDescriptor.Builder(proto.signingEmployee()).columnTitle(i18n.tr("Agent")).build() //                
        };
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), true), new Sort(proto().created(), false));
    }

    @Override
    protected void onItemNew() {
        // TODO - use Lease Candidate Selection dialog
        new BuildingSelectionDialog() {
            @Override
            public boolean onClickOk() {
                N4BatchInitData id = EntityFactory.create(N4BatchInitData.class);
                editNew(getItemOpenPlaceClass(), id);
                return true;
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList( //
                        new ColumnDescriptor.Builder(proto().propertyCode()).build(), //
                        new ColumnDescriptor.Builder(proto().info().name()).build(), //
                        new ColumnDescriptor.Builder(proto().info().address()).width("50%").build(), //
                        new ColumnDescriptor.Builder(proto().info().address().streetNumber()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().streetName()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().city()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().province()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().country()).searchableOnly().build() //
                        );
            }
        }.show();
    }
}
