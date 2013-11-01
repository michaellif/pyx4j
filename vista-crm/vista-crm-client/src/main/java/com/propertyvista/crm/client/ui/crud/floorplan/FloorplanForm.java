/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.shared.IFileURLBuilder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.MediaUploadFloorplanService;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.misc.VistaTODO;

public class FloorplanForm extends CrmEntityForm<FloorplanDTO> {

    private static final I18n i18n = I18n.get(FloorplanForm.class);

    public FloorplanForm(IForm<FloorplanDTO> view) {
        super(FloorplanDTO.class, view);

        Tab tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

        addTab(createMarketingTab(i18n.tr("Marketing")));

    }

    private CEntityFolder<FloorplanAmenity> createAmenitiesListEditor() {
        return new VistaTableFolder<FloorplanAmenity>(FloorplanAmenity.class, isEditable()) {
            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "45em"));
                return columns;
            }
        };
    }

    private TwoColumnFlexFormPanel createMarketingTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Marketing Summary"));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().marketingName())).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().description())).build());
        main.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

        main.setH1(++row, 0, 2, i18n.tr("Images"));
        CImageSlider<MediaFile> imageSlider = new CImageSlider<MediaFile>(MediaFile.class,
                GWT.<MediaUploadFloorplanService> create(MediaUploadFloorplanService.class), new MediaImageUrlBuilder()) {
            @Override
            protected EntityFolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CEntityForm<MediaFile> entryForm) {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().caption()), 8, 15, 16).build());
                main.setWidget(++row, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().description()), 8, 15, 16).build());
                main.setWidget(++row, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().visibility()), 8, 7, 16).build());

                return main;
            }
        };
        imageSlider.setImageSize(240, 160);
        main.setWidget(++row, 0, 2, inject(proto().media(), imageSlider));

        if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
            main.setH1(++row, 0, 2, i18n.tr("ILS Profile"));
            main.setWidget(++row, 0, 2, inject(proto().ilsProfile(), new ILSProfileFloorplanFolder()));
        }

        return main;
    }

    private TwoColumnFlexFormPanel createGeneralTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int leftRow = -1;
        int rightRow = -1;

        main.setH1(++leftRow, 0, 2, i18n.tr("Floorplan Information"));

        leftRow = rightRow = Math.max(leftRow, rightRow);

        main.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().bedrooms()), 3).build());
        main.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().dens()), 3).build());
        main.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().area()), 8).build());

        main.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().floorCount()), 3).build());
        main.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().bathrooms()), 3).build());
        main.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().halfBath()), 3).build());
        main.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().areaUnits()), 8).build());

        leftRow = rightRow = Math.max(leftRow, rightRow);

        main.setH1(++leftRow, 0, 2, proto().amenities().getMeta().getCaption());
        main.setWidget(++leftRow, 0, 2, inject(proto().amenities(), createAmenitiesListEditor()));

        return main;
    }

    private class ILSProfileFloorplanFolder extends VistaBoxFolder<ILSProfileFloorplan> {
        public ILSProfileFloorplanFolder() {
            super(ILSProfileFloorplan.class);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof ILSProfileFloorplan) {
                return new ILSProfileFloorplanEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected void addItem() {
            // get allowed providers
            ((FloorplanEditorView.Presenter) getParentView().getPresenter()).getILSVendors(new DefaultAsyncCallback<Vector<ILSVendor>>() {
                @Override
                public void onSuccess(Vector<ILSVendor> vendors) {
                    // clear used providers
                    for (ILSProfileFloorplan pr : FloorplanForm.this.getValue().ilsProfile()) {
                        vendors.remove(pr.vendor().getValue());
                    }
                    // show selection dialog
                    new SelectEnumDialog<ILSVendor>(i18n.tr("Select ILS Vendor"), vendors) {
                        @Override
                        public boolean onClickOk() {
                            if (getSelectedType() != null) {
                                ILSProfileFloorplan item = EntityFactory.create(ILSProfileFloorplan.class);
                                item.vendor().setValue(getSelectedType());
                                addItem(item);
                            }
                            return true;
                        }

                        @Override
                        public String getEmptySelectionMessage() {
                            return i18n.tr("No Vendors to choose from.");
                        }
                    }.show();
                }
            }, EntityFactory.createIdentityStub(Floorplan.class, FloorplanForm.this.getValue().getPrimaryKey()));
        }

        private class ILSProfileFloorplanEditor extends CEntityDecoratableForm<ILSProfileFloorplan> {
            public ILSProfileFloorplanEditor() {
                super(ILSProfileFloorplan.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().vendor(), new CEnumLabel())).build());
                content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().priority())).build());

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().listingTitle())).build());
                content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().description())).build());
                content.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

                return content;
            }
        }
    }

    class MediaImageUrlBuilder implements IFileURLBuilder<MediaFile> {
        @Override
        public String getUrl(MediaFile file) {
            return MediaUtils.createMediaImageUrl(file);
        }
    };
}