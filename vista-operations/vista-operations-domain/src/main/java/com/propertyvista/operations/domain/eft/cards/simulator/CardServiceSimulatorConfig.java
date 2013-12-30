/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards.simulator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@RequireFeature(ApplicationDevelopmentFeature.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
public interface CardServiceSimulatorConfig extends IEntity {

    public enum SimpulationType {

        SimulateTransations,

        RespondEmpty,

        RespondWithText,

        RespondWithHttpCode,

        RespondWithCode
    }

    @NotNull
    IPrimitive<SimpulationType> responseType();

    IPrimitive<String> responseCode();

    IPrimitive<Integer> responseHttpCode();

    IPrimitive<String> responseText();

    @Caption(name = "Response Delay", description = "milliseconds")
    IPrimitive<Integer> responseDelay();

    @Caption(description = "Do not accept cards before this date")
    IPrimitive<LogicalDate> acceptCardExpiryFrom();

    @Caption(description = "Do not accept cards after this date")
    IPrimitive<LogicalDate> acceptCardExpiryTo();

}
