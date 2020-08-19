package com.by5388.demo.cursorloaderdemo;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * @author Administrator  on 2020/8/19.
 */
public interface IContactsApi {
    /**
     * 查询的Uri
     */
    Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
    /**
     * 查询的列
     */
    String[] CONTACTS_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
    };
    /**
     * 排序规则
     */
    String SORT_KEY = ContactsContract.Contacts.SORT_KEY_PRIMARY;
    int INDEX_ID = 0;
    int INDEX_NAME = 1;


}
