/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.sql.Time;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.forms.client.ui.BaseEditableComponentFactory;
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class VistaViewersComponentFactory extends BaseEditableComponentFactory {

    @Override
    public CField<?, ?> create(IObject<?> member) {
        CField<?, ?> comp;
        MemberMeta mm = member.getMeta();
        if (mm.getObjectClassType() == ObjectClassType.Primitive) {
            if (mm.getValueClass().equals(String.class)) {
                comp = new CLabel<String>();
            } else if (mm.getValueClass().isEnum()) {
                comp = new CEnumLabel();
            } else if (mm.isNumberValueClass()) {
                comp = new CNumberLabel();
                if (mm.getFormat() != null) {
                    ((CNumberLabel) comp).setNumberFormat(mm.getFormat(), mm.useMessageFormat());
                }
            } else if (mm.getValueClass().equals(Date.class) || mm.getValueClass().equals(java.sql.Date.class) || mm.getValueClass().equals(LogicalDate.class)) {
                comp = new CDateLabel();
                if (mm.getFormat() != null) {
                    ((CDateLabel) comp).setDateFormat(mm.getFormat());
                }
            } else if (mm.getValueClass().equals(Time.class)) {
                comp = new CTimeLabel();
                if (mm.getFormat() != null) {
                    ((CTimeLabel) comp).setTimeFormat(mm.getFormat());
                }
            } else if (mm.getValueClass() == Boolean.class) {
                comp = new CBooleanLabel();
            } else {
                comp = super.create(member);
            }
        } else if ((member.getValueClass().equals(Province.class)) || (member.getValueClass().equals(Country.class))) {
            comp = new CEntityLabel();
        } else if (mm.isEntity() && !mm.isDetached() && !mm.isOwnedRelationships()) {
            comp = new CEntityLabel();
        } else {
            comp = super.create(member);
        }
        return super.create(member);
    }
}
