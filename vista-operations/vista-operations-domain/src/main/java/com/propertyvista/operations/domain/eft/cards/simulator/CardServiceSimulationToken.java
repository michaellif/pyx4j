/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards.simulator;

import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@RequireFeature(ApplicationDevelopmentFeature.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
public interface CardServiceSimulationToken extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    CardServiceSimulationCard card();

    @ToString
    @Length(30)
    @Indexed(uniqueConstraint = true)
    IPrimitive<String> token();

    IPrimitive<Boolean> active();

    @OrderColumn
    IPrimitive<Integer> odr();
}
