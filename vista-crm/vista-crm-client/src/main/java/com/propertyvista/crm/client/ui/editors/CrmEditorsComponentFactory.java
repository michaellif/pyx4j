/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.client.ui.CEntitySuggestBox;
import com.pyx4j.entity.client.ui.flex.EntityFormComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.common.client.ui.CMoney;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.ref.Country;

public class CrmEditorsComponentFactory extends EntityFormComponentFactory {

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Money.class)) {
            return new CMoney();
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
        } else {
            return super.create(member);
        }
    }
}
