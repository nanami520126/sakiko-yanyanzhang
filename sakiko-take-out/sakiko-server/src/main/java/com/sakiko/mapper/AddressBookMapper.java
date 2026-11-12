package com.sakiko.mapper;

import com.sakiko.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    @Insert("insert into address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    List<AddressBook> list(AddressBook addressBook);

    @Select("SELECT * FROM address_book where user_id = #{currentId} and is_default = 1")
    AddressBook defaultList(Long currentId);

    void update(AddressBook addressBook);

    @Delete("DELETE FROM address_book where id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM address_book where id = #{id}")
    AddressBook getById(Long id);
}
