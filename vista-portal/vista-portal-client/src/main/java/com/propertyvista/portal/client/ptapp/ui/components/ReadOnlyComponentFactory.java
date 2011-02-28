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
package com.propertyvista.portal.client.ptapp.ui.components;

import java.util.Date;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.EntityFormComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;

public class ReadOnlyComponentFactory extends EntityFormComponentFactory {

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?, ?> comp = null;
        if (mm.getObjectClassType() == ObjectClassType.Primitive) {
            if (mm.getValueClass().equals(String.class)) {
                return new CLabel();
            } else if (mm.getValueClass().isEnum()) {
                return new CEnumLabel();
            } else if (mm.isEntity()) {
                return new CEntityLabel();
            } else if (mm.isNumberValueClass()) {
                return new CNumberLabel();
            } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))) {
                return new CDateLabel();
            } else {
                comp = super.create(member);
            }
        } else {
            comp = super.create(member);
        }
        return comp;
    }
}
