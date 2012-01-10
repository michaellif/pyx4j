/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.CrmUser;

//TODO rename to ISharedCrmUserEntity 
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface ISharedUserEntity extends IEntity {

    /*
     * To make it public set user.key to Key.DORMANT_KEY
     */
    @ReadOnly
    @Detached
    @NotNull
    @MemberColumn(name = "user_id")
    CrmUser user();

}
