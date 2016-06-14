package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.storage.UserGroupStorage;
import com.jinyufeili.minas.account.storage.UserStorage;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/9/16.
 */
@Service
public class UserService implements UserDetailsService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private UserGroupStorage userGroupStorage;

    @Autowired
    private WxMpService wechatService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = getByOpenId(username);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(user.getOpenId(), user.getOpenId(),
                        getAuthorities(user.getId()));

        return userDetails;
    }

    public User get(int id) {
        return userStorage.get(id);
    }

    public int create(User user) {
        return userStorage.create(user);
    }

    public User getByOpenId(String openId) {
        return userStorage.getByOpenId(openId);
    }

    public List<User> queryFreeUser() {
        return userStorage.queryFreeUser();
    }

    public boolean update(User user) {
        return userStorage.update(user);
    }

    public List<User> queryByGroupId(int groupId) {
        return userStorage.queryByGroupId(groupId);
    }

    public List<User> queryNonGroupUser() {
        return userStorage.queryNonGroupUser();
    }

    public List<User> queryUnbinded() {
        return userStorage.queryUnbinded();
    }

    public String sendBindNotification(User user) throws WxErrorException {
        LOG.info("发送认证通知 {}", user.getName());
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        message.setToUser(user.getOpenId());
        message.setTemplateId("oGq4gWwc4ISaDwUcBMYNX4KvOABiJj14t59f1ANbX6Y");
        message.getDatas().add(new WxMpTemplateData("first", "翡丽铂庭小区业主身份认证"));
        message.getDatas().add(new WxMpTemplateData("keyword1", "需要补充个人信息"));
        message.getDatas().add(new WxMpTemplateData("keyword2", "等待认证"));
        message.getDatas().add(new WxMpTemplateData("remark",
                "翡丽社区平台只对小区业主提供服务, 因此需要您提供您的姓名、门牌号及联系方式。\n" +
                        "您可以直接在此回复以上信息, 例如: 丁仪, 爱公馆4-101, 18601234567。\n该信息仅对筹备组人员可见, 收到您信息后需要1-3天来完成身份认证。"));
        return wechatService.templateSend(message);
    }

    private Collection<GrantedAuthority> getAuthorities(int userId) {
        return userGroupStorage.getGroupsOfUser(userId).stream()
                .map(g -> new SimpleGrantedAuthority("ROLE_" + g.getName())).collect(Collectors.toSet());
    }

    private List<User> queryByName(String name) {
        return userStorage.queryByName(name);
    }
}
