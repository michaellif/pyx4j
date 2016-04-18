package com.pyx4j.forms.client.ui;

import com.pyx4j.gwt.commons.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.gwt.shared.IFileURLBuilder;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

/*
 * CImage allows to display and edit a single image or a set of images (using sequential navigation)  
 */
public abstract class CImageSlider<E extends IHasFile<?>> extends CField<IList<E>, NImageSlider<E>> {

    private Image placeholder;

    private Dimension imageSize;

    private Dimension thumbSize;

    private final IFileURLBuilder imageFileUrlBuilder;

    private final UploadService<? extends IEntity, ?> service;

    private final Class<E> imgClass;

    public CImageSlider(Class<E> imgClass, UploadService<?, ?> service, IFileURLBuilder fileURLBuilder) {
        this.imgClass = imgClass;
        this.service = service;
        this.imageFileUrlBuilder = fileURLBuilder;
        this.imageSize = new Dimension(250, 250);
        this.thumbSize = new Dimension(160, 120);
        setNativeComponent(new NImageSlider<E>(this));
    }

    public Class<E> getImgClass() {
        return imgClass;
    }

    protected abstract FolderImages getFolderIcons();

    public String getImageUrl(E file) {
        return imageFileUrlBuilder.getUrl(file.file());
    }

    public void setThumbnailPlaceholder(Image placeholder) {
        this.placeholder = placeholder;
        getNativeComponent().reset();
    }

    public Image getThumbnailPlaceholder() {
        return placeholder;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        getNativeComponent().setScaleMode(scaleMode);
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        getNativeComponent().resizeToFit();
    }

    public Dimension getImageSize() {
        return imageSize;
    }

    public void setThumbSize(int width, int height) {
        thumbSize = new Dimension(width, height);
    }

    public void setOrganizerWidth(int width) {
        getNativeComponent().setOrganizerWidth(width);
    }

    public Dimension getThumbSize() {
        return thumbSize;
    }

    UploadService<?, ?> getUploadService() {
        return service;
    }

    public abstract IsWidget getImageEntryView(CForm<E> entryForm);
}
