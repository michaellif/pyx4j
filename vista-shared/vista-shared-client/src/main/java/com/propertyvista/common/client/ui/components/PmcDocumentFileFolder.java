/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.domain.pmc.info.PmcDocumentFile;

public class PmcDocumentFileFolder extends VistaTableFolder<PmcDocumentFile> {

    private static final I18n i18n = I18n.get(PmcDocumentFileFolder.class);

    public static final List<FolderColumnDescriptor> COLUMNS;

    static {
        PmcDocumentFile proto = EntityFactory.getEntityPrototype(PmcDocumentFile.class);
        COLUMNS = Arrays.asList((new FolderColumnDescriptor(proto.file().fileName(), "30em")));
    }

    private static class PmcDocumentFileForm extends CFolderRowEditor<PmcDocumentFile> {

        public PmcDocumentFileForm() {
            super(PmcDocumentFile.class, COLUMNS);
            setViewable(true);
        }

        @Override
        protected CField<?, ?> createCell(FolderColumnDescriptor column) {
            if (column.getObject() == proto().file().fileName()) {
                CTextField cmp = new CTextField();
                cmp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        // TODO fix this
                        Window.open(MediaUtils.createPmcDocumentUrl(((PmcDocumentFileForm) getParent()).getValue()), "_blank", null);
                    }
                });
                cmp.setFormatter(new IFormatter<String, String>() {
                    @Override
                    public String format(String value) {
                        if (value == null || value.equals("")) {
                            return i18n.tr("No File");
                        } else {
                            return value;
                        }
                    }
                });

                cmp.setParser(new IParser<String>() {
                    @Override
                    public String parse(String string) throws ParseException {
                        return string;
                    }
                });
                inject(column.getObject(), cmp);
                return cmp;
            }
            return super.createCell(column);
        }
    }

    public PmcDocumentFileFolder() {
        super(PmcDocumentFile.class);
        setOrderable(false);
    }

    @Override
    protected void addItem() {
        throw new Error("TODO implement me");
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    protected CForm<PmcDocumentFile> createItemForm(IObject<?> member) {
        return new PmcDocumentFileForm();
    }

    @Override
    protected IFolderDecorator<PmcDocumentFile> createFolderDecorator() {
        VistaTableFolderDecorator<PmcDocumentFile> d = (VistaTableFolderDecorator<PmcDocumentFile>) super.createFolderDecorator();
        d.setShowHeader(false);
        return d;
    }

}
