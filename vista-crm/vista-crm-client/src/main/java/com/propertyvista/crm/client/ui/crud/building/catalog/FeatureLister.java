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
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.domain.financial.offering.Feature;

public class FeatureLister extends ListerBase<Feature> {

    private final static I18n i18n = I18n.get(FeatureLister.class);

    public FeatureLister() {
        super(Feature.class, false, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().version().type(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().name(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().mandatory(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().recurring(), true).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<Feature> updateCriteria(EntityListCriteria<Feature> criteria) {
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new CreateNewFeatureDialog().show();
    }

    private class CreateNewFeatureDialog extends SelectEnumDialog<Feature.Type> implements OkCancelOption {

        public CreateNewFeatureDialog() {
            super(i18n.tr("Select Feature Type"), EnumSet.allOf(Feature.Type.class));
        }

        @Override
        public boolean onClickOk() {
            Feature feature = EntityFactory.create(Feature.class);
            feature.version().type().setValue(getSelectedType());
            feature.catalog().setPrimaryKey(getPresenter().getParent());
            getPresenter().editNew(getItemOpenPlaceClass(), feature);
            return true;
        }

        @Override
        public boolean onClickCancel() {
            return true;
        }

    }
}
