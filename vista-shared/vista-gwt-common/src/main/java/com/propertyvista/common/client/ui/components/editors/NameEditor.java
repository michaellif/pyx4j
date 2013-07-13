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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.person.Name;

public class NameEditor extends CEntityDecoratableForm<Name> {

    private final CComponent<Name> viewComp;

    private final String customViewLabel;

    private final CrudAppPlace linkPlace;

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

        linkPlace = (linkType != null ? AppPlaceEntityMapper.resolvePlace(linkType) : null);
        viewComp = new CEntityLabel<Name>();
        viewComp.setViewable(true);
        viewComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

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
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        if (!isViewable()) {
            int row = -1;
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().firstName()), 15).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().lastName()), 15).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().middleName()), 5).build());

            row = -1;
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().namePrefix()), 5).build());
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().nameSuffix()), 5).build());
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().maidenName()), 15).build());
        } else {
            main.setWidget(0, 0, 2, new FormDecoratorBuilder(viewComp, 15, true).customLabel(customViewLabel).build());
        }

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!isEditable()) {
            viewComp.setValue(getValue());
        }
    }
}
