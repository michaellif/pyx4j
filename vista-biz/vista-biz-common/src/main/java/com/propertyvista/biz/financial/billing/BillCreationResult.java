/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import com.propertyvista.domain.financial.billing.Bill;

public class BillCreationResult {

    enum Status {
        failed, created
    }

    private Status status;

    private String message;

    public BillCreationResult(Bill bill) {
        switch (bill.billStatus().getValue()) {
        case Failed:
            status = Status.failed;
            break;
        case Finished:
            status = Status.created;
            break;
        }
    }

    public BillCreationResult(String message) {
        status = Status.failed;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
