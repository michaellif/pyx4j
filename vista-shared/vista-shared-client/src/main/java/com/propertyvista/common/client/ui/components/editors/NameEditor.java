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
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.AccessoryEntityForm;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.person.Name;

public class NameEditor extends AccessoryEntityForm<Name> {

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
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        if (!isViewable()) {
            formPanel.append(Location.Left, proto().firstName()).decorate().componentWidth(220);
            formPanel.append(Location.Left, proto().lastName()).decorate().componentWidth(220);
            formPanel.append(Location.Left, proto().middleName()).decorate().componentWidth(80);

            formPanel.append(Location.Right, proto().namePrefix()).decorate().componentWidth(80);
            formPanel.append(Location.Right, proto().nameSuffix()).decorate().componentWidth(80);
            formPanel.append(Location.Right, proto().maidenName()).decorate().componentWidth(180);
        } else {
            viewComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            viewComp.setDecorator(new FieldDecoratorBuilder(15, true).customLabel(customViewLabel).build());
            formPanel.append(Location.Dual, viewComp);
        }

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!isEditable()) {
            viewComp.setValue(getValue());
        }
    }
}
