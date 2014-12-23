/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-06
 * @author vlads
 */
package com.propertyvista.domain.settings;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.customizations.CountryOfOperation;

@Table(prefix = "admin", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PmcVistaFeatures extends IEntity {

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> onlineApplication();

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> whiteLabelPortal();

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<CountryOfOperation> countryOfOperation();

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> yardiIntegration();

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> yardiMaintenance();

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> tenantEmailEnabled();

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> tenantSureIntegration();

    //------ calculated values ------------
    @Transient
    IPrimitive<Integer> yardiInterfaces();

}
