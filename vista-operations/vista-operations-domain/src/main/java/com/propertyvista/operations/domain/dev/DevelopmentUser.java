/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.dev;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(namespace = VistaNamespace.operationsNamespace)
@GwtBlacklist
public interface DevelopmentUser extends IEntity {

    IPrimitive<String> firstName();

    IPrimitive<String> lastName();

    @Caption(name = "E-mail")
    @Indexed
    @Length(64)
    IPrimitive<String> email();

    IPrimitive<String> homePhone();

    IPrimitive<String> mobilePhone();

    IPrimitive<String> businessPhone();

    IPrimitive<Boolean> testCallsOnHosts();

    @Indexed
    @Length(128)
    IPrimitive<String> host1();

    @Indexed
    @Length(128)
    IPrimitive<String> host2();

    @Indexed
    @Length(128)
    IPrimitive<String> host3();

    /**
     * Forward All email to developer email
     */
    IPrimitive<Boolean> forwardAll();

}
