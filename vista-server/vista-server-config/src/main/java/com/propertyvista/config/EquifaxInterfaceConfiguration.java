/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

public abstract class EquifaxInterfaceConfiguration {

    protected final AbstractVistaServerSideConfiguration config;

    protected EquifaxInterfaceConfiguration(AbstractVistaServerSideConfiguration config) {
        this.config = config;
    }

    protected abstract String getDefaultInquiryUrl();

    public final String getInquiryUrl() {
        return config.getConfigProperties().getValue("equifax.inquiryUrl", getDefaultInquiryUrl());
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                : ").append(getClass().getName()).append("\n");
        b.append("InquiryUrl                        : ").append(getInquiryUrl()).append("\n");
        return b.toString();
    }
}
