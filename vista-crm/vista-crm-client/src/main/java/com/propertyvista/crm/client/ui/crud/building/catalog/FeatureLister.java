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
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.versioning.VersionedLister;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureLister extends VersionedLister<Feature> {

    private final static I18n i18n = I18n.get(FeatureLister.class);

    public FeatureLister() {
        super(Feature.class, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().version().featureType(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().name(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().mandatory(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().recurring(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().visibility(), true).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().version().featureType().getPath().toString(), false));
    }

    @Override
    protected void onItemNew() {
        new SelectEnumDialog<Feature.Type>(i18n.tr("Select Feature Type"), EnumSet.allOf(Feature.Type.class)) {
            @Override
            public boolean onClickOk() {
                Feature feature = EntityFactory.create(Feature.class);
                feature.version().featureType().setValue(getSelectedType());
                feature.catalog().setPrimaryKey(getPresenter().getParent());
                getPresenter().editNew(getItemOpenPlaceClass(), feature);
                return true;
            }
        }.show();
    }
}
