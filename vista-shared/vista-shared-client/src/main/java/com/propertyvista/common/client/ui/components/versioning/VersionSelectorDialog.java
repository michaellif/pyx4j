/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.versioning;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

public abstract class VersionSelectorDialog<V extends IVersionData<?>> extends EntitySelectorTableDialog<V> {

    static final I18n i18n = I18n.get(VersionSelectorDialog.class);

    public VersionSelectorDialog(Class<V> entityVersionClass, Key entityId) {
        super(entityVersionClass, false, Collections.<V> emptyList(), i18n.tr("Select Version"));
        setParentFiltering(entityId);
        setDialogPixelWidth(700);
    }

    public Key getSelectedVersionId() {
        for (V selected : getSelectedItems()) {
            return new Key(selected.holder().getPrimaryKey().asLong(), (selected.fromDate().isNull() ? 0 : selected.fromDate().getValue().getTime()));
        }
        return null;
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().versionNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().fromDate()).build(),
                new MemberColumnDescriptor.Builder(proto().createdByUser()).build()
            );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().versionNumber(), true));
    }
}