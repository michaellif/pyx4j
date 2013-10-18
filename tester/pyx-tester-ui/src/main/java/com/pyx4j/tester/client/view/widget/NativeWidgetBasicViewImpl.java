/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Nov 9, 2011
 * @author igor
 * @version $Id$
 */
package com.pyx4j.tester.client.view.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.combobox.ListBox;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;
import com.pyx4j.widgets.client.dialog.MessageDialog_v2;
import com.pyx4j.widgets.client.photoalbum.BasicPhotoAlbumModel;
import com.pyx4j.widgets.client.photoalbum.Photo;
import com.pyx4j.widgets.client.photoalbum.PhotoAlbum;

public class NativeWidgetBasicViewImpl extends ScrollPanel implements NativeWidgetBasicView {

    private static final I18n i18n = I18n.get(NativeWidgetBasicViewImpl.class);

    public NativeWidgetBasicViewImpl() {
        setSize("100%", "100%");

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Main Form"));

        LogicalDate starting = new LogicalDate();
        LogicalDate minDate = new LogicalDate(110, 1, 1);
        LogicalDate maxDate = new LogicalDate(112, 12, 31);

        final TextBox textBox = new TextBox();

        DatePickerComposite datePicker = new DatePickerComposite();
        datePicker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

            @Override
            public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                String date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(event.getChosenDate());
                textBox.setText(date);
            }

        });

        //Label lbl = new Label("CalendarUtil.getStartingDayOfWeek is: " + new Integer(CalendarUtil.getStartingDayOfWeek()).toString());
        //main.setWidget(++row, 0, lbl);
        main.setWidget(++row, 0, textBox);
        main.setWidget(++row, 0, datePicker);

        main.setHR(++row, 0, 1);

        DatePickerComposite datePickerMulti = new DatePickerComposite(3, starting, minDate, maxDate, new ArrayList<LogicalDate>());
        main.setWidget(++row, 0, datePickerMulti);

        main.setHR(++row, 0, 1);

        final Button dialogButton = new Button("Long message Dialog");
        dialogButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                MessageDialog_v2
                        .info("Very_Long_Message_Dialog_Very_Long_Message_Dialog_Very_Long_Message_Dialog_Very_Long_Message_Dialog<br>Very_Long_Message_Dialog_Very_Long_Message_Dialog_Very_Long_Message");
            }
        });

        main.setWidget(++row, 0, dialogButton);

        main.setHR(++row, 0, 1);

        {
            ListBox<String> comboBox = new ListBox<String>(false, true);
            List<String> options = new ArrayList<String>();

            options.add("Item 1");
            options.add("Item 2");

            comboBox.setSelection("Item 1");

            comboBox.setOptions(options);

            comboBox.getElement().getStyle().setMargin(5, Unit.PX);

            main.setWidget(++row, 0, comboBox);
        }

        {
            ListBox<String> comboBox = new ListBox<String>(false, true);
            List<String> options = new ArrayList<String>();

            options.add("Item 1");
            options.add("Item 2");
            options.add("Item 3");
            options.add("Item 444444444444444444");
            options.add("Item 5");
            options.add("Item 6");
            options.add("Item 7");
            options.add("Item 8");
            options.add("Item 9");
            options.add("Item 10");
            options.add("Item 11");
            options.add("Item 12");
            options.add("Item 131313131313131313131313131313");
            options.add("Item 14");
            options.add("Item 15");

            comboBox.setOptions(options);

            comboBox.setSelection("Item 14");

            comboBox.getElement().getStyle().setMargin(5, Unit.PX);

            main.setWidget(++row, 0, comboBox);

        }

        {
            ListBox<String> comboBox = new ListBox<String>(false, true);
            List<String> options = new ArrayList<String>();

            options.add("Item 1");
            options.add("Item 2");
            options.add("Item 3");
            options.add("Item 4");
            options.add("Item 5");
            options.add("Item 6");
            options.add("Item 7");
            options.add("Item 8");
            options.add("Item 9");

            comboBox.setOptions(options);

            comboBox.getElement().getStyle().setMargin(5, Unit.PX);

            main.setWidget(++row, 0, comboBox);

        }

        {
            ListBox<String> comboBox = new ListBox<String>(true, true);
            List<String> options = new ArrayList<String>();

            options.add("Item 1");
            options.add("Item 2");
            options.add("Item 3");
            options.add("Item 4");
            options.add("Item 5");
            options.add("Item 6");
            options.add("Item 7");
            options.add("Item 8");
            options.add("Item 9");

            comboBox.setOptions(options);

            comboBox.getElement().getStyle().setMargin(5, Unit.PX);

            main.setWidget(++row, 0, comboBox);

        }

        {
            PhotoAlbum photoAlbum = new PhotoAlbum() {

                @Override
                public void addPhotoCommand() {
                    getPhotoAlbumModel().addPhoto(
                            new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo#"));
                }

                @Override
                public void updateCaptionCommand(int index) {
                    getPhotoAlbumModel().updateCaption(index, "EDITED " + System.currentTimeMillis());
                }

            };

            photoAlbum.setWidth("700px");
            photoAlbum.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            photoAlbum.getElement().getStyle().setBorderColor("black");
            photoAlbum.getElement().getStyle().setBorderWidth(2, Unit.PX);
            final BasicPhotoAlbumModel model = new BasicPhotoAlbumModel();
            photoAlbum.setPhotoAlbumModel(model);

            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo1"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo2"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo3"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo4"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo5"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo6"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo7"));

            main.setWidget(++row, 0, photoAlbum);

        }

        {
            main.setHR(++row, 0, 1);

            final RadioGroup<String> rg = new RadioGroup<String>(RadioGroup.Layout.VERTICAL);
            rg.setOptions(Arrays.asList("String1", "String2", "String3"));
            main.setWidget(++row, 0, rg);

            final Button button1 = new Button("Set new Options");
            button1.addClickHandler(new ClickHandler() {

                int setCount = 1;

                @Override
                public void onClick(ClickEvent event) {
                    List<String> opts = new ArrayList<String>();
                    setCount++;
                    for (int i = 0; i <= 4; i++) {
                        opts.add("String" + (i + setCount));
                    }
                    rg.setOptions(opts);
                }
            });
            main.setWidget(++row, 0, button1);
        }

        add(main);
    }
}
