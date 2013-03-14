/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.paps;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;

public class PreauthorizedPayments extends VistaTableFolder<PreauthorizedPayment> {

    public PreauthorizedPayments() {
        super(PreauthorizedPayment.class);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().amountType(), "10em"),
                new EntityFolderColumnDescriptor(proto().amount(), "10em"),
                new EntityFolderColumnDescriptor(proto().paymentMethod(), "30em"));
          //@formatter:on
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LeasePaymentMethod) {
            return new CEntityLabel<LeasePaymentMethod>();
        }
        return super.create(member);
    }
}
