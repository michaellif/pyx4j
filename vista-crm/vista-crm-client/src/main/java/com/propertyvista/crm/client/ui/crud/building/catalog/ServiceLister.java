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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService.ServiceInitializationdata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.misc.VistaTODO;

public class ServiceLister extends AbstractLister<Service> {

    private final static I18n i18n = I18n.get(ServiceLister.class);

    public ServiceLister() {
        super(Service.class, true, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().code()).build(),
            new MemberColumnDescriptor.Builder(proto().version().name()).build(),
            new MemberColumnDescriptor.Builder(proto().version().price()).build(),
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(), 
            new MemberColumnDescriptor.Builder(proto().expiredFrom(), false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().code(), false), new Sort(proto().version().name(), false));
    }

    @Override
    protected EntityListCriteria<Service> updateCriteria(EntityListCriteria<Service> criteria) {
        if (!VistaTODO.VISTA_2256_Default_Product_Catalog_Show) {
            criteria.eq(criteria.proto().isDefaultCatalogItem(), Boolean.FALSE);
        }
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new ProductCodeSelectorDialog(ARCode.Type.services(), i18n.tr("Select Service ARCode")) {
            @Override
            public boolean onClickOk() {
                ServiceInitializationdata id = EntityFactory.create(ServiceInitializationdata.class);
                id.parent().set(EntityFactory.createIdentityStub(ProductCatalog.class, getPresenter().getParent()));
                id.code().set(getSelectedItem());
                getPresenter().editNew(getItemOpenPlaceClass(), id);
                return true;
            }
        }.show();
    }
}
