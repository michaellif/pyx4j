/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.landlord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.building.LandlordMediaUploadService;
import com.propertyvista.domain.property.LandlordMedia;
import com.propertyvista.dto.LandlordDTO;

public class LandlordForm extends CrmEntityForm<LandlordDTO> {

    private static final I18n i18n = I18n.get(LandlordForm.class);

    public LandlordForm(IForm<LandlordDTO> view) {
        super(LandlordDTO.class, view);

        Tab tab = addTab(createGeneralPanel(i18n.tr("General")));
        selectTab(tab);

        addTab(createBuildingsPanel(i18n.tr("Buildings")));

    }

    private TwoColumnFlexFormPanel createGeneralPanel(String title) {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(title);

        int row = 0;
        panel.setWidget(row, 0, (new FormDecoratorBuilder(inject(proto().name()))).build());
        panel.setWidget(row, 1, (new FormDecoratorBuilder(inject(proto().website()))).build());

        panel.setH1(++row, 0, 2, proto().address().getMeta().getCaption());
        panel.setWidget(++row, 0, inject(proto().address(), new AddressStructuredEditor(false)));
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        panel.setBR(++row, 0, 2);
        CImage logo = new CImage(GWT.<LandlordMediaUploadService> create(LandlordMediaUploadService.class), new VistaFileURLBuilder(LandlordMedia.class));
        logo.setScaleMode(ScaleMode.Contain);
        logo.setImageSize(368, 60);
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().logo().file(), logo), true).customLabel(i18n.tr("Logo")).build());

        panel.setBR(++row, 0, 2);
        CImage signature = new CImage(GWT.<LandlordMediaUploadService> create(LandlordMediaUploadService.class), new VistaFileURLBuilder(LandlordMedia.class));
        signature.setScaleMode(ScaleMode.Contain);
        signature.setImageSize(368, 60);
        signature.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.signaturePlaceholder()));
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().signature().file(), signature), true).customLabel(i18n.tr("Signature")).build());

        return panel;
    }

    private TwoColumnFlexFormPanel createBuildingsPanel(String title) {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(title);

        panel.setWidget(0, 0, 2, inject(proto().buildings(), new LandlordBuildingFolder(isEditable())));

        return panel;
    }

    @Override
    public void addValidations() {
        get(proto().website()).addValueValidator(new EditableValueValidator<String>() {

            @Override
            public ValidationError isValid(CComponent<String> component, String url) {
                if (url != null) {
                    if (ValidationUtils.isSimpleUrl(url)) {
                        return null;
                    } else {
                        return new ValidationError(component, i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
                    }
                }
                return null;
            }
        });
        ((CField) get(proto().website())).setNavigationCommand(new Command() {
            @Override
            public void execute() {
                String url = getValue().website().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, proto().website().getMeta().getCaption(), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
            }
        });
    }

}
