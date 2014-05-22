/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.forms.client.ui.BaseEditableComponentFactory;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CSignature;

import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.security.CustomerSignature;

public class VistaEditorsComponentFactory extends BaseEditableComponentFactory {

    @Override
    public CField<?, ?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.getValueClass().equals(CustomerSignature.class)) {
            return new CSignature(mm.getCaption());
        } else if (member.getValueClass().equals(ISOCountry.class) && EditorType.suggest.equals(member.getMeta().getEditorType())) {
            final CountrySuggestBox comp = new CountrySuggestBox();
            comp.setFormatter(new IFormatter<ISOCountry, String>() {
                @Override
                public String format(ISOCountry value) {
                    return value.name;
                }
            });
            comp.setParser(new IParser<ISOCountry>() {
                @Override
                public ISOCountry parse(String string) {
                    return ISOCountry.forName(string);
                }
            });
            return comp;
        } else {
            return super.create(member);
        }
    }
}
