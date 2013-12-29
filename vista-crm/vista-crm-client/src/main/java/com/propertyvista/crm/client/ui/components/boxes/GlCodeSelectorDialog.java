/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectGlCodeListService;
import com.propertyvista.domain.financial.GlCode;

public abstract class GlCodeSelectorDialog extends EntitySelectorTableDialog<GlCode> {

    private final static I18n i18n = I18n.get(GlCodeSelectorDialog.class);

    public GlCodeSelectorDialog() {
        this(Collections.<GlCode> emptyList());
    }

    public GlCodeSelectorDialog(boolean isMultiselect) {
        this(isMultiselect, Collections.<GlCode> emptyList());
    }

    public GlCodeSelectorDialog(List<GlCode> alreadySelected) {
        this(false, alreadySelected);
    }

    public GlCodeSelectorDialog(boolean isMultiselect, List<GlCode> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select GL Code"));
    }

    public GlCodeSelectorDialog(boolean isMultiselect, List<GlCode> alreadySelected, String caption) {
        super(GlCode.class, isMultiselect, alreadySelected, caption);
        setDialogPixelWidth(700);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().codeId(), true).build(),
                new MemberColumnDescriptor.Builder(proto().description(), true).build(),
                new MemberColumnDescriptor.Builder(proto().glCodeCategory(), true).build()
        ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().codeId(), false), new Sort(proto().glCodeCategory(), false));
    }

    @Override
    protected AbstractListService<GlCode> getSelectService() {
        return GWT.<AbstractListService<GlCode>> create(SelectGlCodeListService.class);
    }
}