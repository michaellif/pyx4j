/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;

public class AdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

    private static final I18n i18n = I18n.get(AdjustmentFolder.class);

    public AdjustmentFolder() {
        super(BillableItemAdjustment.class, i18n.tr("Adjustment"), false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().type(), "9em"),
                new FolderColumnDescriptor(proto().value(), "5em"),
                new FolderColumnDescriptor(proto().effectiveDate(), "9em"),
                new FolderColumnDescriptor(proto().expirationDate(), "10em"));
            //@formatter:on
    }
}
