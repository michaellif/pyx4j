/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-12
 * @author ArtyomB
 */
package com.propertyvista.domain.legal.n4;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n(context = "Delivery Method")
@XmlType(name = "DeliveryMethod")
public enum N4DeliveryMethod {

    @Translate(value = "Handing by Hand")
    Hand,

    @Translate(value = "Sending by Mail")
    Mail,

    @Translate(value = "Sending by Courier")
    Courier;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    };
}