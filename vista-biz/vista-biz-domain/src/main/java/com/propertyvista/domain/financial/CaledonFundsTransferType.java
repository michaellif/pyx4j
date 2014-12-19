/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author vlads
 */
package com.propertyvista.domain.financial;

public enum CaledonFundsTransferType {

    PreAuthorizedDebit("PAD", "pad"),

    DirectBankingPayment("DBP", "dbp"),

    InteracOnlinePayment("IOP", "iop");

    private final String code;

    private final String fileNamePart;

    CaledonFundsTransferType(String code, String fileNamePart) {
        this.code = code;
        this.fileNamePart = fileNamePart;
    }

    public String getCode() {
        return code;
    }

    public String getFileNamePart() {
        return fileNamePart;
    }

    public String getDirectoryName(String name) {
        return fileNamePart + "_" + name;
    }

    public FundsTransferType asFundsTransferType() {
        switch (this) {
        case PreAuthorizedDebit:
            return FundsTransferType.PreAuthorizedDebit;
        case DirectBankingPayment:
            return FundsTransferType.DirectBankingPayment;
        case InteracOnlinePayment:
            return FundsTransferType.InteracOnlinePayment;
        default:
            throw new IllegalArgumentException();
        }
    }
}