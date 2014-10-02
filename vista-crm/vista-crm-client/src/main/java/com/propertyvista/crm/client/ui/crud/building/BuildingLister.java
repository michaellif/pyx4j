/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingLister extends AbstractLister<BuildingDTO> {

    private static final I18n i18n = I18n.get(BuildingLister.class);

    public BuildingLister() {
        super(BuildingDTO.class, !VistaFeatures.instance().yardiIntegration() ? true : false);

        setDataTableModel(new DataTableModel<BuildingDTO>(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().propertyCode(), true).build(),
            new MemberColumnDescriptor.Builder(proto().complex()).build(),
            new MemberColumnDescriptor.Builder(proto().complexPrimary(), false).build(),
            new MemberColumnDescriptor.Builder(proto().externalId(), false).build(),
            new MemberColumnDescriptor.Builder(proto().integrationSystemId(), VistaFeatures.instance().yardiIntegration()).build(),
            new MemberColumnDescriptor.Builder(proto().suspended(), false).build(),
            new MemberColumnDescriptor.Builder(proto().updated(), false).build(),
            
            new MemberColumnDescriptor.Builder(proto().info().name(), true).build(),
            new MemberColumnDescriptor.Builder(proto().info().type(), true).build(),
            new MemberColumnDescriptor.Builder(proto().info().shape(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().address().streetNumber(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().address().streetName(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().address().city(), true).build(),
            new MemberColumnDescriptor.Builder(proto().info().address().province(), true).build(),
            new MemberColumnDescriptor.Builder(proto().info().address().country(), false).build(),
            new MemberColumnDescriptor.Builder(proto().marketing().visibility(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().totalStoreys(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().residentialStoreys(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().structureType(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().structureBuildYear(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().constructionType(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().foundationType(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().floorType(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().landArea(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().waterSupply(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().centralAir(), false).build(),
            new MemberColumnDescriptor.Builder(proto().info().centralHeat(), false).build(),
            
            new MemberColumnDescriptor.Builder(proto().contacts().website(), false).build(),
            
            new MemberColumnDescriptor.Builder(proto().financial().dateAcquired(), true).build(),
            new MemberColumnDescriptor.Builder(proto().financial().purchasePrice(), true).build(),
            new MemberColumnDescriptor.Builder(proto().financial().marketPrice(), false).build(),
            new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalDate(), true).build(),
            new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalValue(), true).build(),
            
            new MemberColumnDescriptor.Builder(proto().defaultProductCatalog(), false).sortable(false).build(),

            new MemberColumnDescriptor.Builder(proto().merchantAccountPresent(), false).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().financial().currency().name(), false).title(proto().financial().currency()).build(),
            
            new MemberColumnDescriptor.Builder(proto().marketing().name(), false).title(i18n.tr("Marketing Name")).build()
        )); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().propertyCode(), false));
    }
}
