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

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.EntityFormComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;

import com.propertyvista.common.domain.financial.Money;
import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.common.domain.ref.Province;

public class VistaViewersComponentFactory extends EntityFormComponentFactory {

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.getObjectClassType() == ObjectClassType.Primitive) {
            if (mm.getValueClass().equals(String.class)) {
                return new CLabel();
            } else if (mm.getValueClass().isEnum()) {
                return new CEnumLabel();
            } else if (mm.isNumberValueClass()) {
                CNumberLabel comp = new CNumberLabel();
                if (mm.getFormat() != null) {
                    (comp).setNumberFormat(mm.getFormat());
                }
                return comp;
            } else if (mm.getValueClass().equals(Date.class) || mm.getValueClass().equals(java.sql.Date.class) || mm.getValueClass().equals(LogicalDate.class)) {
                CDateLabel comp = new CDateLabel();
                if (mm.getFormat() != null) {
                    (comp).setDateFormat(mm.getFormat());
                }
                return comp;
            } else if (mm.getValueClass() == Boolean.class) {
                return new CBooleanLabel();
            } else {
                return super.create(member);
            }
        } else if ((member.getValueClass().equals(Province.class)) || (member.getValueClass().equals(Country.class))) {
            return new CEntityLabel();
        } else if (member.getValueClass().equals(Money.class)) {
            return new CMoneyLabel();
        } else if (!mm.isOwnedRelationships() && mm.isEntity()) {
            return new CEntityLabel();
        }
        return super.create(member);
    }
}
