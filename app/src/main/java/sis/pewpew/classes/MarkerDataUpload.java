package sis.pewpew.classes;

import android.support.annotation.Keep;

@Keep
@SuppressWarnings({"unused", "WeakerAccess"})
public class MarkerDataUpload {

    public String id;
    public String title;
    public String iconUrl;
    public String details;
    public String address;
    public String group;
    public String contactsPhone;
    public String contactsEmail;
    public String contactsUrl;
    public String workTime;
    public String workTimeBreak;
    public double lat;
    public double lng;

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getWorkTime() {
        return workTime;
    }

    public String getWorkTimeBreak() {
        return workTimeBreak;
    }

    public String getDetails() {
        return details;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public String getContactsEmail() {
        return contactsEmail;
    }

    public String getContactsUrl() {
        return contactsUrl;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public MarkerDataUpload(String id, String group, String title, String address, String iconUrl, String workTime, String workTimeBreak, String details, String contactsPhone,
                            String contactsEmail, String contactsUrl, double lat, double lng) {
        this.id = id;
        this.title = title;
        this.group = group;
        this.address = address;
        this.iconUrl = iconUrl;
        this.workTime = workTime;
        this.workTimeBreak = workTimeBreak;
        this.details = details;
        this.contactsPhone = contactsPhone;
        this.contactsEmail = contactsEmail;
        this.contactsUrl = contactsUrl;
        this.lat = lat;
        this.lng = lng;
    }
}
