package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.gwt.shared.FileURLBuilder;

/*
 * CImage allows to display and edit a single image or a set of images (using sequential navigation)  
 */
public abstract class CImage<E extends IFile> extends CField<IList<E>, NImage<E>> {

    public enum Type {
        single, multiple
    }

    private Image placeholder;

    private Dimension imageSize;

    private Dimension thumbSize;

    private FileURLBuilder<E> imageFileUrlBuilder;

    private FileURLBuilder<E> thumbnailFileUrlBuilder;

    private UploadService<E, E> service;

    private final Class<E> imgClass;

    private final Type type;

    public CImage(Class<E> imgClass, Type type) {
        this.imgClass = imgClass;
        this.type = type;
        this.imageSize = new Dimension(250, 250);
        this.thumbSize = new Dimension(160, 120);
        setNativeWidget(new NImage<E>(this));
    }

    public Class<E> getImgClass() {
        return imgClass;
    }

    public Type getType() {
        return type;
    }

    protected abstract EntityFolderImages getFolderIcons();

    public String getImageUrl(E file) {
        return imageFileUrlBuilder.getUrl(file);
    }

    public void setThumbnailPlaceholder(Image placeholder) {
        this.placeholder = placeholder;
    }

    public Image getThumbnailPlaceholder() {
        return placeholder;
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        getWidget().resizeToFit();
    }

    public Dimension getImageSize() {
        return imageSize;
    }

    public void setThumbSize(int width, int height) {
        thumbSize = new Dimension(width, height);
    }

    public Dimension getThumbSize() {
        return thumbSize;
    }

    public void setImageFileUrlBuilder(FileURLBuilder<E> fileURLBuilder) {
        this.imageFileUrlBuilder = fileURLBuilder;
    }

    public void setThumbnailFileUrlBuilder(FileURLBuilder<E> fileURLBuilder) {
        this.thumbnailFileUrlBuilder = fileURLBuilder;
    }

    public void setUploadService(UploadService<E, E> service) {
        this.service = service;
    }

    public UploadService<E, E> getUploadService() {
        return service;
    }

    public abstract Widget getImageEntryView(CEntityForm<E> entryForm);
}
