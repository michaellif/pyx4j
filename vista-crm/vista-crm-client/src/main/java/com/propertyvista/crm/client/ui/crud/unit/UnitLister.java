/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.AptUnitDTO;

public class UnitLister extends ListerBase<AptUnitDTO> {

    private static final I18n i18n = I18n.get(UnitLister.class);

    public UnitLister() {
        this(true);
    }

    public UnitLister(boolean allowAddNew) {
        super(AptUnitDTO.class, CrmSiteMap.Properties.Unit.class, false, allowAddNew);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().buildingCode()).build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().name()).title(i18n.tr("Floorplan Name")).build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().marketingName()).title(i18n.tr("Floorplan Marketing Name")).build(),
                new MemberColumnDescriptor.Builder(proto().info().economicStatus()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().info().floor()).build(),
                new MemberColumnDescriptor.Builder(proto().info().number()).build(),
                new MemberColumnDescriptor.Builder(proto().info().area()).build(),
                new MemberColumnDescriptor.Builder(proto().info().areaUnits()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bedrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().info()._bathrooms()).build(),
                new MemberColumnDescriptor.Builder(proto().financial()._unitRent()).build(),
                new MemberColumnDescriptor.Builder(proto().financial()._marketRent()).build(),
                new MemberColumnDescriptor.Builder(proto().availableForRent()).build()
       );//@formatter:on

    }
}
