package com.miaxis.postal.data.bean;

/**
 * @author Tank
 * @date 2021/7/5 1:43 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class OrderImage {

    public String photoUrl;

    public OrderImage(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "OrderImage{" +
                "photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
