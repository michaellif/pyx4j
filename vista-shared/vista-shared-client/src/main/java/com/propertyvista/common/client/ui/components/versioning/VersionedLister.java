/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.versioning;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.RadioGroup.Layout;

public abstract class VersionedLister<E extends IVersionedEntity<?>> extends AbstractLister<E> {

    @I18n(context = "Version Display Mode")
    public enum VersionDisplayMode {
        displayDraft, displayFinal;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

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

    public VersionedLister(Class<E> clazz) {
        super(clazz);
        getDataTablePanel().addUpperActionItem(displayModeButton.asWidget());
    }

    public VersionedLister(Class<E> clazz, boolean allowAddNew) {
        super(clazz, allowAddNew);
        getDataTablePanel().addUpperActionItem(displayModeButton.asWidget());
    }

    public VersionedLister(Class<E> clazz, boolean allowAddNew, boolean allowDelete) {
        super(clazz, allowAddNew, allowDelete);
        getDataTablePanel().addUpperActionItem(displayModeButton.asWidget());
    }

    public VersionDisplayMode getVersionDisplayMode() {
        return versionDisplayMode;
    }

    protected void onVersionDisplayModeChange(VersionDisplayMode mode) {
        versionDisplayMode = mode;
        getPresenter().populate();
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

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().version().versionNumber(), false));
    }
}
