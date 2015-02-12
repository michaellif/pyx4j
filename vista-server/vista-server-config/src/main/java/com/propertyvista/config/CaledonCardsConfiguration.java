/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2015
 * @author ernestog
 */
package com.propertyvista.config;

public abstract class CaledonCardsConfiguration {

    protected final AbstractVistaServerSideConfiguration config;

    protected CaledonCardsConfiguration(AbstractVistaServerSideConfiguration config) {
        this.config = config;
    }

    protected boolean useCardValidationDefault() {
        return true;
    }

    public final boolean useCardValidation() {
        return config.getConfigProperties().getBooleanValue("caledon.useCardValidation", useCardValidationDefault());
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                : ").append(getClass().getName()).append("\n");
        b.append("useCardValidation                 : ").append(useCardValidation()).append("\n");
        return b.toString();
    }
}
