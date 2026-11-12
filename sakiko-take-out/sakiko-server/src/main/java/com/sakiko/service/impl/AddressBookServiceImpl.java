package com.sakiko.service.impl;

import com.sakiko.context.BaseContext;
import com.sakiko.entity.AddressBook;
import com.sakiko.mapper.AddressBookMapper;
import com.sakiko.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Resource
    AddressBookMapper addressBookMapper;

    public void add(AddressBook addressBook) {
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
    }

    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    public AddressBook defaultList(Long currentId) {
        return addressBookMapper.defaultList(currentId);
    }

    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }


    public AddressBook getById(Long id) {

        return addressBookMapper.getById(id);
    }

    @Transactional
    public void setDefault(AddressBook addressBook) {
        List<AddressBook> list = addressBookMapper.list(addressBook);
        for (AddressBook book : list) {
            book.setIsDefault(0);
            addressBookMapper.update(book);
        }
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);

    }
}
