package com.jinyufeili.minas.account.storage;

import com.jinyufeili.minas.account.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/9/16.
 */
@Repository
public class UserStorage {

    private static final RowMapper<AuthUser> ROW_MAPPER = (rs, i) -> {
        AuthUser authUser = new AuthUser();

        authUser.setId(rs.getInt("id"));
        authUser.setUsername(rs.getString("first_name"));
        authUser.setCreatedTime(rs.getDate("date_joined").getTime());
        return authUser;
    };

    private static final RowMapper<? extends WechatUser> WECHAT_ROW_MAPPER = (RowMapper<WechatUser>) (rs, i) -> {
        WechatUser wechatUser = new WechatUser();
        wechatUser.setId(rs.getInt("id"));
        wechatUser.setOpenId(rs.getString("openId"));
        wechatUser.setAccessToken(rs.getString("access_token"));
        wechatUser.setRefreshToken(rs.getString("refresh_token"));
        wechatUser.setExpiredTime(rs.getDate("expire_at").getTime());
        wechatUser.setUserId(rs.getInt("user_id"));
        return wechatUser;
    };

    @Autowired
    private NamedParameterJdbcOperations db;

    public User get(int id) {
        WechatUser wechatUser =
                db.queryForObject("SELECT * FROM wechat_wechatuser WHERE id = :id", Collections.singletonMap("id", id),
                        WECHAT_ROW_MAPPER);

        AuthUser authUser = db.queryForObject("SELECT * FROM auth_user WHERE id = :id",
                Collections.singletonMap("id", wechatUser.getUserId()), ROW_MAPPER);
        return mergeUser(authUser, wechatUser);
    }

    public User getByOpenId(String openId) {
        WechatUser wechatUser = db.queryForObject("SELECT * FROM wechat_wechatuser WHERE openId = :openId",
                Collections.singletonMap("openId", openId), WECHAT_ROW_MAPPER);
        AuthUser authUser = db.queryForObject("SELECT * FROM auth_user WHERE id = :id",
                Collections.singletonMap("id", wechatUser.getUserId()), ROW_MAPPER);
        return mergeUser(authUser, wechatUser);
    }

    public List<User> queryByName(String name) {
        List<AuthUser> authUsers =
                db.query("SELECT * FROM auth_user WHERE first_name = :name", Collections.singletonMap("name", name),
                        ROW_MAPPER);

        return authUsers.stream().map(this::getUser).collect(Collectors.toList());
    }

    public int create(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", user.getName());
        params.addValue("createdTime", new Date(System.currentTimeMillis()));
        params.addValue("expiredTime", new Date(user.getExpiredTime()));
        params.addValue("openId", user.getOpenId());
        params.addValue("accessToken", user.getAccessToken());
        params.addValue("refreshToken", user.getRefreshToken());

        params.addValue("_username", user.getName() + System.currentTimeMillis());
        SecureRandom random = new SecureRandom();
        params.addValue("_password", new BigInteger(130, random).toString(32));

        KeyHolder kh = new GeneratedKeyHolder();
        db.update("INSERT INTO auth_user" +
                " SET username = :_username" +
                ", password = :_password" +
                ", is_superuser = 0" +
                ", last_name = ''" +
                ", first_name = :name" +
                ", email = ''" +
                ", is_staff = 0" +
                ", is_active = 1" +
                ", date_joined = :createdTime", params, kh);
        int userId = kh.getKey().intValue();
        params.addValue("userId", userId);

        kh = new GeneratedKeyHolder();
        db.update("INSERT INTO wechat_wechatuser" +
                " SET openId = :openId" +
                ", access_token = :accessToken" +
                ", refresh_token = :refreshToken" +
                ", expire_at = :expiredTime" +
                ", user_id = :userId", params, kh);

        return kh.getKey().intValue();
    }

    public boolean update(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", user.getName());
        params.addValue("expiredTime", new Date(user.getExpiredTime()));
        params.addValue("openId", user.getOpenId());
        params.addValue("accessToken", user.getAccessToken());
        params.addValue("refreshToken", user.getRefreshToken());
        params.addValue("id", user.getId());

        WechatUser wechatUser =
                db.queryForObject("SELECT * FROM wechat_wechatuser WHERE id = :id", params, WECHAT_ROW_MAPPER);
        params.addValue("userId", wechatUser.getUserId());

        boolean success = db.update("UPDATE auth_user SET first_name = :name WHERE id = :userId", params) > 0;
        success = success && db.update("UPDATE wechat_wechatuser" +
                " SET openId = :openId" +
                ", access_token = :accessToken" +
                ", refresh_token = :refreshToken" +
                ", expire_at = :expiredTime WHERE id = :id", params) > 0;

        return success;
    }

    public List<User> queryFreeUser() {
        List<Integer> userIds = db.queryForList("select wu.id" +
                " from wechat_wechatuser wu" +
                " left join crm_resident r on r.wechat_user_id = wu.id" +
                " where r.id is NULL", Collections.emptyMap(), Integer.class);

        return userIds.stream().map(this::get).collect(Collectors.toList());
    }

    public List<User> queryByGroupId(int groupId) {
        return db.query("select u.*" +
                " from auth_user u" +
                " join auth_user_groups ug on ug.user_id = u.id" +
                " where ug.group_id = :groupId", Collections.singletonMap("groupId", groupId), ROW_MAPPER).stream()
                .map(this::getUser).collect(Collectors.toList());
    }

    public List<User> queryNonGroupUser() {
        return db.query("select u.*" +
                " from auth_user u" +
                " left join auth_user_groups ug on ug.user_id = u.id" +
                " where ug.id is NULL", Collections.emptyMap(), ROW_MAPPER).stream().map(this::getUser)
                .collect(Collectors.toList());
    }

    public List<User> queryUnbinded() {
        List<Integer> userIds = db.queryForList(
                "select wu.id from wechat_wechatuser wu left join crm_resident r on r.wechat_user_id = wu.id where r.id is NULL",
                Collections.emptyMap(), Integer.class);

        return userIds.stream().map(this::get).collect(Collectors.toList());
    }

    private User getUser(AuthUser authUser) {
        WechatUser wechatUser = db.queryForObject("SELECT * FROM wechat_wechatuser WHERE user_id = :userId",
                Collections.singletonMap("userId", authUser.getId()), WECHAT_ROW_MAPPER);

        return mergeUser(authUser, wechatUser);
    }

    private User mergeUser(AuthUser authUser, WechatUser wechatUser) {
        User user = new User();
        user.setId(wechatUser.getId());
        user.setExpiredTime(wechatUser.getExpiredTime());
        user.setRefreshToken(wechatUser.getRefreshToken());
        user.setAccessToken(wechatUser.getAccessToken());
        user.setCreatedTime(authUser.getCreatedTime());
        user.setOpenId(wechatUser.getOpenId());
        user.setName(authUser.getUsername());
        return user;
    }

    private static class AuthUser {

        private int id;

        private String username;

        private long createdTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(long createdTime) {
            this.createdTime = createdTime;
        }
    }

    private static class WechatUser {

        private int id;

        private String openId;

        private String accessToken;

        private String refreshToken;

        private long expiredTime;

        private int userId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public long getExpiredTime() {
            return expiredTime;
        }

        public void setExpiredTime(long expiredTime) {
            this.expiredTime = expiredTime;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
