/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 15, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.portal.domain.dto.financial.PaymentInfoDTO;

public class PaymentInfoFolder extends VistaTableFolder<PaymentInfoDTO> {

    public PaymentInfoFolder() {
        super(PaymentInfoDTO.class, false);
        setViewable(true);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(// @formatter:off
                new EntityFolderColumnDescriptor(proto().amount(), "7em"),
                new EntityFolderColumnDescriptor(proto().paymentDate(), "9em"),
                new EntityFolderColumnDescriptor(proto().paymentMethod().type(), "10em"),
                new EntityFolderColumnDescriptor(proto().payer(), "20em")
        ); // formatter:on
    }
}