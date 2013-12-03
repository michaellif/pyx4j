/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid;

import java.text.ParseException;

import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInLeaseParticipantDTO;

public class PayerOptionFormat implements IFormat<MoneyInLeaseParticipantDTO> {

    @Override
    public String format(MoneyInLeaseParticipantDTO value) {
        return value.name().getValue();
    }

    @Override
    public MoneyInLeaseParticipantDTO parse(String string) throws ParseException {
        return null; // should not be used 
    }

}
