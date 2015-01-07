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
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.crm.rpc.services.selections.SelectN4LeaseCandidateListService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4BatchDTO;
import com.propertyvista.dto.N4LeaseCandidateDTO;

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
        // open lease candidate selection dialog
        new EntitySelectorTableDialog<N4LeaseCandidateDTO>(N4LeaseCandidateDTO.class, false, true, Collections.<N4LeaseCandidateDTO> emptySet(),
                i18n.tr("Select Leases")) {

            {
                this.setDialogPixelWidth(700);
            }

            @Override
            public boolean onClickOk() {
                Vector<Lease> leaseCandidates = new Vector<>();
                for (N4LeaseCandidateDTO candidate : getSelectedItems()) {
                    leaseCandidates.add(EntityFactory.createIdentityStub(Lease.class, candidate.leaseId().getPrimaryKey()));
                }
                ((N4BatchCrudService) getService()).createBatches(new DefaultAsyncCallback<N4BatchDTO>() {
                    @Override
                    public void onSuccess(N4BatchDTO result) {
                        if (result != null) {
                            // open editor with the returned batch
                            AppSite.getPlaceController().goTo(
                                    AppSite.getHistoryMapper().createPlace(getItemOpenPlaceClass()).formEditorPlace(result.getPrimaryKey()));
                        }
                    }
                }, leaseCandidates);
                return true;
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList( //
                        new ColumnDescriptor.Builder(proto().propertyCode()).filterAlwaysShown(true).build(), //
                        new ColumnDescriptor.Builder(proto().unitNo()).build(), //
                        new ColumnDescriptor.Builder(proto().moveIn()).build(), //
                        new ColumnDescriptor.Builder(proto().moveOut()).build(), //
                        new ColumnDescriptor.Builder(proto().amountOwed()).filterAlwaysShown(true).build(), //
                        new ColumnDescriptor.Builder(proto().lastNotice()).build() //
                        );
            }

            @Override
            protected AbstractListCrudService<N4LeaseCandidateDTO> getSelectService() {
                return GWT.<AbstractListCrudService<N4LeaseCandidateDTO>> create(SelectN4LeaseCandidateListService.class);
            }

        }.show();
    }
}
