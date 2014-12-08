/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.marketing;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.domain.marketing.PortalResidentMarketingTip;
import com.propertyvista.operations.rpc.services.QuickTipCrudService;

public class QuickTipLister extends SiteDataTablePanel<PortalResidentMarketingTip> {

    protected static final I18n i18n = I18n.get(QuickTipLister.class);

    public QuickTipLister() {
        super(PortalResidentMarketingTip.class, GWT.<AbstractCrudService<PortalResidentMarketingTip>> create(QuickTipCrudService.class), true);

        setColumnDescriptors( //
                new MemberColumnDescriptor.Builder(proto().created()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().updated()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().target()).build(), //
                new MemberColumnDescriptor.Builder(proto().comments()).build(), //
                new MemberColumnDescriptor.Builder(proto().content()).visible(false).build());

        setDataTableModel(new DataTableModel<PortalResidentMarketingTip>());

        setDeleteActionEnabled(true);

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().target(), false));
    }

}
