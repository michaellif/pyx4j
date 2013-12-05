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
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.person.Name;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class NameEditor extends CEntityForm<Name> {

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
        BasicFlexFormPanel main = new BasicFlexFormPanel();
        int row = -1;

        if (!isViewable()) {
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().firstName()), 200).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().lastName()), 200).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().middleName()), 60).build());

            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().namePrefix()), 60).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().nameSuffix()), 60).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().maidenName()), 200).build());
        } else {
            main.setWidget(0, 0, 1, new FormWidgetDecoratorBuilder(viewComp, 200).customLabel(customViewLabel).build());
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
