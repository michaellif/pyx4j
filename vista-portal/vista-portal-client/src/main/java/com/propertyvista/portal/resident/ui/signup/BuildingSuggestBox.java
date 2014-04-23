/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.signup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.forms.client.ui.CAbstractSuggestBox;
import com.pyx4j.forms.client.ui.NSuggestBox;

import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationBuildingDTO;

public class BuildingSuggestBox extends CAbstractSuggestBox<SelfRegistrationBuildingDTO> {

    public BuildingSuggestBox() {
        setFormatter(new BuildingSuggestFormatter());
        setParser(new BuildingSuggestParser());
    }

    class BuildingSuggestFormatter implements IFormatter<SelfRegistrationBuildingDTO> {

        @Override
        public String format(SelfRegistrationBuildingDTO value) {
            if (value == null) {
                return "";
            }
            return value.address().getValue();
        }
    }

    class BuildingSuggestParser implements IParser<SelfRegistrationBuildingDTO> {

        @Override
        public SelfRegistrationBuildingDTO parse(String string) throws ParseException {
            for (SelfRegistrationBuildingDTO option : getOptions()) {
                if (getOptionName(option).equals(string)) {
                    return option;
                }
            }
            return null;
        }

    }

    @Override
    public String getOptionName(SelfRegistrationBuildingDTO o) {
        return o.address().getValue();
    }

    @Override
    public void setOptions(Collection<SelfRegistrationBuildingDTO> opt) {
        super.setOptions(opt);
        List<Suggestion> defaultSuggestions = new ArrayList<>();
        for (final SelfRegistrationBuildingDTO option : opt) {
            defaultSuggestions.add(new Suggestion() {
                @Override
                public String getDisplayString() {
                    return option.address().getValue();
                }

                @Override
                public String getReplacementString() {
                    return option.address().getValue();
                }

            });
        }

        NSuggestBox<SelfRegistrationBuildingDTO> w = getNativeComponent();
        if (w != null) {
            ((MultiWordSuggestOracle) w.getEditor().getSuggestOracle()).setDefaultSuggestions(defaultSuggestions);
        }
    }

    @Override
    public boolean isValueEmpty() {
        return (getValue() == null) || getValue().isNull() || getValue().buildingKey().isNull();
    }
}
