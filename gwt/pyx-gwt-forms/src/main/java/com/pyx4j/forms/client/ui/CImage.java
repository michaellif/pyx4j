package com.pyx4j.forms.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.Image;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.FileURLBuilder;

/*
 * CImage allows to display and edit a single image or a set of images (using sequential navigation)  
 */
public class CImage<E extends IFile> extends CField<List<E>, NImage<E>> {

    public enum Type {
        single, multiple
    }

    private final Type type;

    private Image placeholder;

    private FileURLBuilder<E> imageFileUrlBuilder;

    private FileURLBuilder<E> thumbnailFileUrlBuilder;

    private UploadService<E, E> service;

    public CImage(Type type) {
        this.type = type;
    }

    public String getImageUrl(E file) {
        return imageFileUrlBuilder.getUrl(file);
    }

    //TODO What is this function for
    public E getNewValue(IFile file) {
        return (E) file;
    }

    public void setPlaceholderImage(Image placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected NImage<E> createWidget() {
        return new NImage<E>(this);
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
}
