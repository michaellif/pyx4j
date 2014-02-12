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
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.rpc.services.selections.SelectGlCodeListService;
import com.propertyvista.domain.financial.GlCode;

public abstract class GlCodeSelectorDialog extends EntitySelectorTableVisorController<GlCode> {

    private final static I18n i18n = I18n.get(GlCodeSelectorDialog.class);

    public GlCodeSelectorDialog(IPane parentView) {
        this(parentView, null);
    }

    public GlCodeSelectorDialog(IPane parentView, boolean isMultiselect) {
        this(parentView, isMultiselect, Collections.<GlCode> emptyList());
    }

    public GlCodeSelectorDialog(IPane parentView, List<GlCode> alreadySelected) {
        this(parentView, alreadySelected != null, alreadySelected);
    }

    public GlCodeSelectorDialog(IPane parentView, boolean isMultiselect, List<GlCode> alreadySelected) {
        this(parentView, isMultiselect, alreadySelected, i18n.tr("Select GL Code"));
    }

    public GlCodeSelectorDialog(IPane parentView, boolean isMultiselect, List<GlCode> alreadySelected, String caption) {
        super(parentView, GlCode.class, isMultiselect, alreadySelected, caption);
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