/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-16
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.PreauthorizedPayment;

class CoveredItemFolder extends VistaTableFolder<PreauthorizedPayment.CoveredItem> {

    public CoveredItemFolder() {
        super(PreauthorizedPayment.CoveredItem.class, false);
        setEditable(false);
        setViewable(true);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().billableItem(),"40em"),
                new EntityFolderColumnDescriptor(proto().percent(), "5em"));
          //@formatter:on                
    }
}