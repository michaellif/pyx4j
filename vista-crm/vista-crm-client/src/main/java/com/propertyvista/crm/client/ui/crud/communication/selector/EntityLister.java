/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.site.client.backoffice.activity.EntitySelectorTableVisorController.VersionDisplayMode;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.RadioGroup.Layout;

public class EntityLister<E extends IEntity> extends AbstractLister<E> {

    private VersionDisplayMode versionDisplayMode = VersionDisplayMode.displayFinal;

    private final CRadioGroupEnum<VersionDisplayMode> displayModeButton = new CRadioGroupEnum<VersionDisplayMode>(VersionDisplayMode.class, Layout.HORISONTAL);
    {
        displayModeButton.setValue(versionDisplayMode);
        displayModeButton.addValueChangeHandler(new ValueChangeHandler<VersionDisplayMode>() {
            @Override
            public void onValueChange(ValueChangeEvent<VersionDisplayMode> event) {
                onVersionDisplayModeChange(event.getValue());
            }
        });
    }

    public EntityLister(Class<E> clazz, boolean isVersioned) {
        super(clazz);

        getDataTablePanel().setPageSizeOptions(Arrays.asList(new Integer[] { PAGESIZE_SMALL, PAGESIZE_MEDIUM }));
        if (isVersioned) {
            getDataTablePanel().addUpperActionItem(displayModeButton.asWidget());
        }

    }

    public VersionDisplayMode getVersionDisplayMode() {
        return versionDisplayMode;
    }

    @Override
    public List<Sort> getDefaultSorting() {
        List<Sort> sort = new ArrayList<Sort>();//EntitySelectorTableVisorController.this.getDefaultSorting();
//        if (sort == null) {
//            sort = super.getDefaultSorting();
//        }
        return sort;
    }

    protected void onVersionDisplayModeChange(VersionDisplayMode mode) {
        versionDisplayMode = mode;
        obtain(0);
    }

    @Override
    protected EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
        switch (getVersionDisplayMode()) {
        case displayDraft:
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            break;
        case displayFinal:
            criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
            break;
        }
        return super.updateCriteria(criteria);
    }

}
