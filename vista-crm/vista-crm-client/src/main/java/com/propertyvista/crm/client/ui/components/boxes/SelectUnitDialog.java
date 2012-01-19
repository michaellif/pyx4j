/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public abstract class SelectUnitDialog extends EntitySelectorDialog<AptUnit> {

    private static I18n i18n = I18n.get(SelectUnitDialog.class);

    public SelectUnitDialog() {
        this(false);
    }

    @SuppressWarnings("unchecked")
    public SelectUnitDialog(boolean displayAvailableUnitsOnly) {
        super(AptUnit.class, false, Collections.EMPTY_LIST, i18n.tr("Select Unit"));
    }

    @Override
    protected String width() {
        return "900px";
    }

    @Override
    protected String height() {
        return "400px";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ColumnDescriptor<AptUnit>> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                // building data                
                new MemberColumnDescriptor.Builder(proto().belongsTo().propertyCode(), true).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().complex(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().propertyManager(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().name(), true).title(i18n.tr("Building")).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().type(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().shape(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().streetNumber(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().streetNumberSuffix(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().streetName(), true).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().streetType(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().streetDirection(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().city(), true).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().province(), true).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().address().country(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().totalStoreys(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().residentialStoreys(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().structureType(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().structureBuildYear(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().constructionType(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().foundationType(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().floorType(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().landArea(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().waterSupply(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().centralAir(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().info().centralHeat(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().contacts().website(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().contacts().email(), false).title(proto().belongsTo().contacts().email())
                        .<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().financial().dateAcquired(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().financial().purchasePrice(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().financial().marketPrice(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().financial().lastAppraisalDate(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().financial().lastAppraisalValue(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().financial().currency().name(), false).title(proto().belongsTo().financial().currency())
                        .<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().belongsTo().marketing().name(), false).title(i18n.tr("Building Marketing Name")).<AptUnit> build(),

                // unit data
                new MemberColumnDescriptor.Builder(proto().info().floor(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().info().number(), true).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().info().area()).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().info()._bedrooms()).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().info()._bathrooms()).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().name()).title(i18n.tr("Floorplan")).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().floorplan().marketingName(), false).<AptUnit> build(),
                new MemberColumnDescriptor.Builder(proto().availableForRent()).<AptUnit> build()
                ); //@formatter:on
    }

    @Override
    protected AbstractListService<AptUnit> getSelectService() {
        return GWT.<AbstractListService<AptUnit>> create(SelectUnitCrudService.class);
    }
}