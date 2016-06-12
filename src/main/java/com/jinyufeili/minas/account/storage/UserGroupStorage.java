package com.jinyufeili.minas.account.storage;

import com.jinyufeili.minas.account.data.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
        return db.query("SELECT g.* FROM auth_group g" +
                " JOIN auth_user_groups ug ON ug.group_id = g.id" +
                " JOIN wechat_wechatuser wu on wu.user_id = ug.user_id" +
                " WHERE wu.id = :userId", Collections.singletonMap("userId", userId), ROW_MAPPER);
    }

    public List<UserGroup> getAllGroups() {
        return db.query("select * from auth_group", ROW_MAPPER);
    }

    public boolean unlinkGroupByUserId(int userId, Set<Integer> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return true;
        }

        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("userId", userId);
        source.addValue("groupIds", groupIds);
        List<Integer> ids = db.queryForList("select ug.id from auth_user_groups ug" +
                " join wechat_wechatuser wu on wu.user_id = ug.user_id" +
                " where ug.group_id in (:groupIds)" +
                " and wu.id = :userId", source, Integer.class);

        return db.update("delete from auth_user_groups where id in (:ids)", Collections.singletonMap("ids", ids)) > 0;
    }

    public boolean linkGroupByUserId(int userId, Set<Integer> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return true;
        }

        int authUserId = db.queryForObject("select user_id from wechat_wechatuser where id = :id",
                Collections.singletonMap("id", userId), Integer.class);

        for (int groupId : groupIds) {
            MapSqlParameterSource source = new MapSqlParameterSource();
            source.addValue("userId", authUserId);
            source.addValue("groupId", groupId);
            db.update("insert into auth_user_groups set user_id = :userId, group_id = :groupId", source);
        }

        return true;
    }

    public UserGroup get(int groupId) {
        return db.queryForObject("select * from auth_group where id = :id", Collections.singletonMap("id", groupId),
                ROW_MAPPER);
    }
}
