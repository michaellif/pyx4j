/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectCityListService;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Province;

public abstract class CitySelectorDialog extends EntitySelectorTableDialog<City> {
    private final static I18n i18n = I18n.get(CitySelectorDialog.class);

    public CitySelectorDialog(boolean isMultiselect, List<City> alreadySelected) {
        super(City.class, false, isMultiselect, alreadySelected, i18n.tr("Select City"));
    }

    public CitySelectorDialog setProvince(List<Province> province) {
        addFilter(PropertyCriterion.in(proto().province(), province));
        return this;
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off                    
                new MemberColumnDescriptor.Builder(proto().name(), true).build(),
                new MemberColumnDescriptor.Builder(proto().province(), true).build()
        ); //@formatter:on
    }

    @Override
    protected AbstractListService<City> getSelectService() {
        return GWT.<AbstractListService<City>> create(SelectCityListService.class);
    }

}
