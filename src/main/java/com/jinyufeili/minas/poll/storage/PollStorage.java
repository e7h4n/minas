package com.jinyufeili.minas.poll.storage;

import com.jinyufeili.minas.poll.data.Poll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by pw on 6/12/16.
 */
@Repository
public class PollStorage {

    private static final RowMapper<Poll> ROW_MAPPER = ((rs, rowNum) -> {
        Poll poll = new Poll();

        poll.setId(rs.getInt("id"));
        poll.setName(rs.getString("name"));

        return poll;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public List<Poll> getByIds(Set<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        return db.query("SELECT * FROM poll_poll WHERE id IN (:id)", Collections.singletonMap("ids", ids), ROW_MAPPER);
    }
}
