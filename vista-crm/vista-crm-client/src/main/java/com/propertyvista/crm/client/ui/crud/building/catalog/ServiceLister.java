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

import java.util.EnumSet;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.common.client.ui.components.VersionedLister;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceLister extends VersionedLister<Service> {

    private final static I18n i18n = I18n.get(ServiceLister.class);

    public ServiceLister() {
        super(Service.class, false, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().version().type()).build(),
            new MemberColumnDescriptor.Builder(proto().version().name()).build()
        );//@formatter:on
    }

    @Override
    protected void onItemNew() {
        new CreateNewServiceDialog().show();
    }

    private class CreateNewServiceDialog extends SelectEnumDialog<Service.Type> implements OkCancelOption {

        public CreateNewServiceDialog() {
            super(i18n.tr("Select Service Type"), EnumSet.allOf(Service.Type.class));
        }

        @Override
        public boolean onClickOk() {
            Service newService = EntityFactory.create(Service.class);
            newService.version().type().setValue(getSelectedType());
            newService.catalog().setPrimaryKey(getPresenter().getParent());
            getPresenter().editNew(getItemOpenPlaceClass(), newService);
            return true;
        }

        @Override
        public boolean onClickCancel() {
            return true;
        }

        @Override
        public String defineWidth() {
            return "250px";
        }

        @Override
        public String defineHeight() {
            return "100px";
        }
    }

}
