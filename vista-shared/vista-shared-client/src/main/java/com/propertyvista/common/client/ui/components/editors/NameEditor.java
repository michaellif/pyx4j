/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-11
 * @author Vlad
 */
package com.propertyvista.common.client.ui.components.editors;

import java.text.ParseException;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IParser;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.person.Name;
import com.propertyvista.shared.config.VistaFeatures;

public class NameEditor extends CForm<Name> {

    private final CField<Name, ?> viewComp;

    private final CrudAppPlace linkPlace;

    private final String customViewLabel;

    public NameEditor() {
        this(null);
    }

    public NameEditor(String customViewLabel) {
        this(customViewLabel, null);
    }

    @SuppressWarnings("rawtypes")
    public NameEditor(String customViewLabel, Class<? extends IEntity> linkType) {
        super(Name.class);

        this.customViewLabel = customViewLabel;

        viewComp = new CEntityLabel<Name>();
        viewComp.setViewable(true);

        linkPlace = (linkType != null ? AppPlaceEntityMapper.resolvePlace(linkType) : null);
        if (linkPlace != null) {
            ((CField) viewComp).setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    if (getLinkKey() != null) {
                        AppSite.getPlaceController().goTo(linkPlace.formViewerPlace(getLinkKey()));
                    }
                }
            });
        }
    }

    /**
     * overwrite to supply real entity key for hyperlink
     */
    public Key getLinkKey() {
        return getValue().getPrimaryKey();
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        if (!isViewable()) {
            formPanel.append(Location.Left, proto().firstName()).decorate().componentWidth(200);
            formPanel.append(Location.Left, proto().lastName()).decorate().componentWidth(200);
            formPanel.append(Location.Left, proto().middleName()).decorate().componentWidth(80);

            formPanel.append(Location.Right, proto().namePrefix()).decorate().componentWidth(80);
            formPanel.append(Location.Right, proto().nameSuffix()).decorate().componentWidth(80);
            formPanel.append(Location.Right, proto().maidenName()).decorate().componentWidth(180);

            // abbreviate middle name in case of Yardi:
            if (VistaFeatures.instance().yardiIntegration()) {
                @SuppressWarnings("unchecked")
                CTextComponent<String, ?> mnComp = ((CTextComponent<String, ?>) get(proto().middleName()));
                mnComp.setParser(new IParser<String>() {
                    @Override
                    public String parse(String string) throws ParseException {
                        return (!string.isEmpty() ? string.substring(0, 1).toUpperCase() + '.' : string);
                    }
                });
            }
        } else {
            viewComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            formPanel.append(Location.Left, viewComp).decorate().customLabel(customViewLabel);
        }

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!isEditable()) {
            viewComp.setValue(getValue(), true, populate);
        }
    }
}
