/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.IPaneView;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.client.ui.crud.building.catalog.ProductCodeSelectorDialog;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService.ServiceInitializationdata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class ServiceLister extends SiteDataTablePanel<Service> {

    private final static I18n i18n = I18n.get(ServiceLister.class);

    private final IPaneView<?> parentView;

    public ServiceLister(IPaneView<?> parentView) {
        super(Service.class, GWT.<AbstractCrudService<Service>> create(ServiceCrudService.class), !VistaFeatures.instance().yardiIntegration(), !VistaFeatures
                .instance().yardiIntegration());
        this.parentView = parentView;
        setFilteringEnabled(false);

        if (VistaTODO.VISTA_2256_Default_Product_Catalog_Show) {
            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().code()).build(), //
                    new ColumnDescriptor.Builder(proto().version().name()).build(), //
                    new ColumnDescriptor.Builder(proto().version().price()).build(), //
                    new ColumnDescriptor.Builder(proto().version().versionNumber()).build(), // 
                    new ColumnDescriptor.Builder(proto().version().availableOnline()).build(), //
                    new ColumnDescriptor.Builder(proto().expiredFrom()).build(), //
                    new ColumnDescriptor.Builder(proto().defaultCatalogItem()).build());
        } else {
            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().code()).build(), //
                    new ColumnDescriptor.Builder(proto().version().name()).build(), //
                    new ColumnDescriptor.Builder(proto().version().price()).visible(!VistaFeatures.instance().yardiIntegration()).build(), //
                    new ColumnDescriptor.Builder(proto().version().versionNumber()).build(), // 
                    new ColumnDescriptor.Builder(proto().version().availableOnline()).build(), //
                    new ColumnDescriptor.Builder(proto().expiredFrom()).build());
        }

        setDataTableModel(new DataTableModel<Service>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        if (VistaTODO.VISTA_2256_Default_Product_Catalog_Show) {
            return Arrays.asList(new Sort(proto().code(), false), new Sort(proto().defaultCatalogItem(), false));
        } else {
            return Arrays.asList(new Sort(proto().code(), false), new Sort(proto().version().name(), false));
        }
    }

    @Override
    protected EntityListCriteria<Service> updateCriteria(EntityListCriteria<Service> criteria) {
        if (!VistaTODO.VISTA_2256_Default_Product_Catalog_Show) {
            criteria.eq(criteria.proto().defaultCatalogItem(), Boolean.FALSE);
        }
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new ProductCodeSelectorDialog(parentView, ARCode.Type.services(), i18n.tr("Select Service ARCode")) {
            @Override
            public boolean onClickOk() {
                ServiceInitializationdata id = EntityFactory.create(ServiceInitializationdata.class);
                id.parent().set(EntityFactory.createIdentityStub(ProductCatalog.class, getDataSource().getParentEntityId()));
                id.code().set(getSelectedItem());
                editNew(getItemOpenPlaceClass(), id);
                return true;
            }
        }.show();
    }
}
