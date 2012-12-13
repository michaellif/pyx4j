/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.rpc.dto;

import com.pyx4j.commons.Pair;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.customizations.CountryOfOperation;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface PmcAccountCreationRequest extends IEntity {

    @NotNull
    IPrimitive<String> firstName();

    @NotNull
    IPrimitive<String> lastName();

    @Editor(type = EditorType.email)
    @NotNull
    IPrimitive<String> email();

    @Editor(type = EditorType.email)
    @NotNull
    @Transient(logTransient = true)
    IPrimitive<String> confirmEmail();

    @NotNull
    @Editor(type = EditorType.password)
    @Transient(logTransient = true)
    IPrimitive<String> password();

    @NotNull
    @Editor(type = EditorType.password)
    @Transient(logTransient = true)
    IPrimitive<String> confirmPassword();

    @NotNull
    @Caption(name = "Company name")
    IPrimitive<String> name();

    @NotNull
    IPrimitive<CountryOfOperation> countryOfOperation();

    @NotNull
    @Caption(name = "Tenant Protal URL")
    IPrimitive<String> dnsName();

    /**
     * Text from image for human verification.
     */
    @NotNull
    @Caption(name = "Enter The Code")
    @Editor(type = Editor.EditorType.captcha)
    IPrimitive<Pair<String, String>> captcha();

}
