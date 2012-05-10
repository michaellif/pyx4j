/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 8, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.insurancemockup.forms;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.portal.domain.ptapp.IAgree;

public class AgreeFolder extends VistaTableFolder<IAgree> {

    private static final I18n i18n = I18n.get(AgreeFolder.class);

    public final static String DEFAULT_STYLE_PREFIX = "AgreeFolder";

    public static enum StyleSuffix implements IStyleName {
        Person, Agree
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final boolean editable;

    public AgreeFolder(boolean modifiable) {
        super(IAgree.class, false);
        this.editable = modifiable;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        //@formatter:off
      return Arrays.asList(
              new EntityFolderColumnDescriptor(proto().person().name(), "30em"),
              new EntityFolderColumnDescriptor(proto().agree(), "5em"));
        //@formatter:on
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof IAgree) {
            return new AgreeEditor();
        }
        return super.create(member);
    }

    private class AgreeEditor extends CEntityFolderRowEditor<IAgree> {

        public AgreeEditor() {
            super(IAgree.class, columns());
            setViewable(true);
        }

        @Override
        public IsWidget createContent() {
            IsWidget c = super.createContent();
            c.asWidget().setStyleName(DEFAULT_STYLE_PREFIX);
            get(proto().person().name()).asWidget().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Person.name());
            get(proto().agree()).asWidget().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Agree.name());
            return c;
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (proto().person().name() == column.getObject()) {
                return inject(proto().person().name(), new CEntityLabel<Name>());
            } else if (proto().agree() == column.getObject() && editable) {
                CComponent<?, ?> comp = inject(proto().agree(), new CCheckBox());
                comp.inheritViewable(false); // always not viewable!
                comp.inheritEditable(false); // control state later in populate...
                return comp;
            }
            return super.createCell(column);
        }

        @Override
        protected void onPopulate() {
            super.onPopulate();
            if (editable) {
                get(proto().agree()).setEditable(!getValue().agree().isBooleanTrue());
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().agree()).addValueValidator(new EditableValueValidator<Boolean>() {
                @Override
                public ValidationFailure isValid(CComponent<Boolean, ?> component, Boolean value) {
                    return value == Boolean.TRUE ? null : new ValidationFailure(i18n.tr("You Must Agree To The Terms And Conditions To Continue"));
                }
            });
        }
    }
}
