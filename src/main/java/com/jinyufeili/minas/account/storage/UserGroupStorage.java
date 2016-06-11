package com.jinyufeili.minas.account.storage;

import com.jinyufeili.minas.account.data.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by pw on 6/11/16.
 */
@Repository
public class UserGroupStorage {

    private static final RowMapper<UserGroup> ROW_MAPPER = ((rs, rowNum) -> {
        UserGroup userGroup = new UserGroup();

        userGroup.setId(rs.getInt("id"));
        userGroup.setName(rs.getString("name"));

        return userGroup;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public List<UserGroup> getGroupsOfUser(int userId) {
        return db.query(
                "SELECT g.* FROM auth_group g" +
                        " JOIN auth_user_groups ug ON ug.group_id = g.id" +
                        " JOIN wechat_wechatuser wu on wu.user_id = ug.user_id" +
                        " WHERE wu.id = :userId",
                Collections.singletonMap("userId", userId), ROW_MAPPER);
    }
}
