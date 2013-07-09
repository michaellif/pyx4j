package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.FileURLBuilder;

/*
 * CImage allows to display and edit a single image or a set of images (using sequential navigation)  
 */
public abstract class CImageList<E extends IFile> extends CField<IList<E>, NImageList<E>> {

    private Image placeholder;

    private FileURLBuilder<E> imageFileUrlBuilder;

    private FileURLBuilder<E> thumbnailFileUrlBuilder;

    private UploadService<E, E> service;

    private final Class<E> imgClass;

    public CImageList(Class<E> imgClass) {
        this.imgClass = imgClass;
        setNativeWidget(new NImageList<E>(this));
    }

    public Class<E> getImgClass() {
        return imgClass;
    }

    protected abstract EntityFolderImages getFolderIcons();

    public String getImageUrl(E file) {
        return imageFileUrlBuilder.getUrl(file);
    }

    public void setPlaceholderImage(Image placeholder) {
        this.placeholder = placeholder;
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
