package com.sakiko.service;

import com.sakiko.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    public void add(AddressBook addressBook);

    List<AddressBook> list(AddressBook addressBook);

    AddressBook defaultList(Long currentId);

    void update(AddressBook addressBook);

    void deleteById(Long id);

    AddressBook getById(Long id);

    void setDefault(AddressBook addressBook);
}
