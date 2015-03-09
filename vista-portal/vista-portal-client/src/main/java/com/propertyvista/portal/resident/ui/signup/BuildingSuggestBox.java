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
 */
package com.propertyvista.portal.resident.ui.signup;

import java.util.Collection;
import java.util.Comparator;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.CSelectorTextBox;
import com.pyx4j.widgets.client.selector.MultyWordSuggestOptionsGrabber;

import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationBuildingDTO;

public class BuildingSuggestBox extends CSelectorTextBox<SelfRegistrationBuildingDTO> {

    private MultyWordSuggestOptionsGrabber<SelfRegistrationBuildingDTO> optionsGrabber;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BuildingSuggestBox() {
        super(new MultyWordSuggestOptionsGrabber<SelfRegistrationBuildingDTO>());

        optionsGrabber = ((MultyWordSuggestOptionsGrabber) getOptionsGrabber());
        optionsGrabber.setFormatter(new IFormatter<SelfRegistrationBuildingDTO, String>() {
            @Override
            public String format(SelfRegistrationBuildingDTO value) {
                return SimpleMessageFormat.format("{0} {1} {2} {3}", //
                        value.marketingName(),//
                        value.streetAddress().getValue(), //
                        value.municipality().getValue(), //
                        value.region().getValue());
            }
        });
        setFormatter(new IFormatter<SelfRegistrationBuildingDTO, String>() {
            @Override
            public String format(SelfRegistrationBuildingDTO value) {
                if (value != null) {
                    return SimpleMessageFormat.format("{0}", value.streetAddress().getValue());
                }
                return null;
            }

        });

        setOptionFormatter(new IFormatter<SelfRegistrationBuildingDTO, SafeHtml>() {
            @Override
            public SafeHtml format(SelfRegistrationBuildingDTO value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendHtmlConstant(SimpleMessageFormat.format("<div style=\"font-size:12px;\"><div>{0}</div><div>{1}, {2}</div><div>{3}</div></div>",
                        (value.marketingName().getValue() == null ? "" : SafeHtmlUtils.fromString(value.marketingName().getValue()).asString()), //
                        SafeHtmlUtils.fromString(value.streetAddress().getValue()).asString(), //
                        SafeHtmlUtils.fromString(value.municipality().getValue()).asString(), //
                        SafeHtmlUtils.fromString(value.region().getValue()).asString()));
                return builder.toSafeHtml();
            }
        });
        optionsGrabber.setComparator(new Comparator<SelfRegistrationBuildingDTO>() {
            @Override
            public int compare(SelfRegistrationBuildingDTO paramT1, SelfRegistrationBuildingDTO paramT2) {
                return paramT1.getStringView().compareTo(paramT2.getStringView());
            }
        });
    }

    public void setOptions(Collection<SelfRegistrationBuildingDTO> options) {
        optionsGrabber.setAllOptions(options);
    }

    @Override
    public boolean isValueEmpty() {
        return (getValue() == null) || getValue().isNull() || getValue().buildingKey().isNull();
    }
}
