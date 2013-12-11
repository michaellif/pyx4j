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

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISignature.SignatureType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntitySuggestBox;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.EntityFormComponentFactory;
import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.common.client.ui.components.editors.GeoLocationEditor;
import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.security.CustomerSignature;

public class VistaEditorsComponentFactory extends EntityFormComponentFactory {

    @Override
    public CComponent<?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.getValueClass().equals(CustomerSignature.class)) {
            return new CSignature(SignatureType.AgreeBox, mm.getCaption());
        } else if (member.getValueClass().equals(Country.class) && EditorType.suggest.equals(member.getMeta().getEditorType())) {
            final CEntitySuggestBox<Country> c = new CEntitySuggestBox<Country>(Country.class);
            c.setFormat(new IFormat<Country>() {

                @Override
                public String format(Country value) {
                    return value.getStringView();
                }

                @Override
                public Country parse(String string) {
                    for (Country option : c.getOptions()) {
                        if (c.getOptionName(option).equals(string)) {
                            return option;
                        }
                    }
                    Country entity = EntityFactory.create(Country.class);
                    entity.name().setValue(string);
                    return entity;
                }
            });
            return c;
        } else if (member.getValueClass().equals(GeoLocation.class)) {
            return new GeoLocationEditor();
        }
        return super.create(member);
    }
}
