package com.jinyufeili.minas.account.storage;

import com.jinyufeili.minas.account.data.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * Created by pw on 6/10/16.
 */
public class UserStorageTest {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static final String USER_NAME = "foo";

    public static final String OPEN_ID = "openId";

    public static final String AK = "ak";

    public static final String RK = "rk";

    public static final long EXPIRED_TIME = System.currentTimeMillis();

    private UserStorage storage;

    private NamedParameterJdbcTemplate db;

    @Before
    public void setUp() throws Exception {
        DataSource ds = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName(
                "testdb;mode=MySQL").setScriptEncoding("UTF-8").addDefaultScripts().ignoreFailedDrops(true).build();

        db = new NamedParameterJdbcTemplate(ds);
        storage = new UserStorage();

        Field field = storage.getClass().getDeclaredField("db");
        field.setAccessible(true);
        field.set(storage, db);
    }

    @Test
    public void getByOpenId() throws Exception {
        createUser();
        User user = storage.getByOpenId(OPEN_ID);

        Assert.assertEquals(1, user.getId());
    }

    @Test
    public void getByName() throws Exception {
        createUser();
        List<User> users = storage.queryByName(USER_NAME);

        Assert.assertEquals(1, users.size());
    }

    @Test
    public void create() throws Exception {
        int userAId = createUser();
        User userA = storage.get(userAId);

        int userBId = createUserB();
        User userB;
        userB = storage.get(userBId);

        Assert.assertEquals(1, userAId);
        Assert.assertEquals(2, userBId);
        Assert.assertNotEquals(userA.getName(), userB.getName());
        Assert.assertNotEquals(userA.getOpenId(), userB.getOpenId());
        Assert.assertNotEquals(userA.getAccessToken(), userB.getAccessToken());
        Assert.assertNotEquals(userA.getRefreshToken(), userB.getRefreshToken());
        Assert.assertNotEquals(userA.getExpiredTime(), userB.getExpiredTime());
        Assert.assertNotEquals(userA.getId(), userB.getId());
    }

    @Test
    public void get() throws Exception {
        int userId = createUser();
        User user;
        user = storage.get(userId);
        Assert.assertEquals(USER_NAME, user.getName());
        Assert.assertEquals(OPEN_ID, user.getOpenId());
        Assert.assertEquals(AK, user.getAccessToken());
        Assert.assertEquals(RK, user.getRefreshToken());
        Assert.assertTrue(user.getCreatedTime() > 0);
        Assert.assertTrue(user.getExpiredTime() > 0);
    }

    @Test
    public void update() throws Exception {
        int userAId = createUser();
        User userA = storage.get(userAId);

        int userBId = createUserB();

        String newName = "baz";
        userA.setName(newName);
        String newAccessToken = "userANew";
        userA.setAccessToken(newAccessToken);
        boolean result = storage.update(userA);
        Assert.assertTrue(result);

        userA = storage.get(userAId);
        Assert.assertEquals(newName, userA.getName());
        Assert.assertEquals(newAccessToken, userA.getAccessToken());

        User userB = storage.get(userBId);
        Assert.assertNotEquals(newName, userB.getName());
        Assert.assertNotEquals(newAccessToken, userB.getAccessToken());
    }

    @Test
    public void remove() throws Exception {
        int userAId = createUser();
        int userBId = createUserB();
        int userCId = createUser();

        storage.remove(userAId);
        storage.remove(userCId);

        LOG.info("userAId: {}", userAId);
        LOG.info("userBId: {}", userBId);
        User user = storage.get(userBId);
        Assert.assertEquals("bar", user.getName());

        Integer residentA = db.queryForObject("select wechat_user_id from crm_resident where id = 1", Collections.emptyMap(), Integer.class);
        Assert.assertNull(residentA);

        Integer residentB = db.queryForObject("select wechat_user_id from crm_resident where id = 2", Collections.emptyMap(), Integer.class);
        Assert.assertEquals(userBId, residentB.intValue());

        try {
            storage.get(userAId);
            Assert.assertFalse(true);
        } catch (EmptyResultDataAccessException e) {
            Assert.assertTrue(true);
        }

        try {
            storage.get(userCId);
            Assert.assertFalse(true);
        } catch (EmptyResultDataAccessException e) {
            Assert.assertTrue(true);
        }
    }

    private int createUserB() {
        User userB = new User();
        userB.setName("bar");
        userB.setOpenId("userBOpenId");
        userB.setAccessToken("ak_b");
        userB.setRefreshToken("rk_b");
        userB.setExpiredTime(EXPIRED_TIME + 100000000l);
        return storage.create(userB);
    }

    private int createUser() {
        User user = new User();
        user.setName(USER_NAME);
        user.setOpenId(OPEN_ID);
        user.setAccessToken(AK);
        user.setRefreshToken(RK);
        user.setExpiredTime(EXPIRED_TIME);
        return storage.create(user);
    }
}