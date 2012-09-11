/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.leasexpiration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.dto.AptUnitDTO;

public class OccupiedUnitsDetailsLister extends BasicLister<AptUnitDTO> {

    private static final I18n i18n = I18n.get(OccupiedUnitsDetailsLister.class);

    public OccupiedUnitsDetailsLister(final Command backToSummaryCommand) {
        super(AptUnitDTO.class, true, false);
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
                new MemberColumnDescriptor.Builder(proto()._availableForRent()).build()
        );//@formatter:on

        addActionItem(new Button(i18n.tr("Back to Summary"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                backToSummaryCommand.execute();
            }

        }));

    }

    @Override
    protected void onItemSelect(AptUnitDTO item) {
        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(proto().getInstanceValueClass()).formViewerPlace(item.getPrimaryKey()));
    }
}
