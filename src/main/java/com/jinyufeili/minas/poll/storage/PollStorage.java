package com.jinyufeili.minas.poll.storage;

import com.jinyufeili.minas.poll.data.Poll;
import com.jinyufeili.minas.poll.data.PollStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/12/16.
 */
@Repository
public class PollStorage {

    private static final RowMapper<Poll> ROW_MAPPER = ((rs, rowNum) -> {
        Poll poll = new Poll();

        poll.setId(rs.getInt("id"));
        poll.setName(rs.getString("name"));
        poll.setStatus(PollStatus.findByInt(rs.getInt("status")));
        poll.setDesc(rs.getString("desc"));

        return poll;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public Map<Integer, Poll> getByIds(Set<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }

        return db.query("SELECT * FROM poll_poll WHERE id IN (:ids)", Collections.singletonMap("ids", ids), ROW_MAPPER)
                .stream().collect(Collectors.toMap(Poll::getId, Function.identity()));
    }

    public List<Poll> query(Set<PollStatus> statuses) {
        return db.query("SELECT * FROM poll_poll where status in (:statuses) order by id desc", Collections
                        .singletonMap("statuses", statuses.stream().map(PollStatus::toInt).collect(Collectors.toSet())),
                ROW_MAPPER);
    }

    public Poll get(int pollId) {
        return db.queryForObject("select * from poll_poll where id = :pollId",
                Collections.singletonMap("pollId", pollId), ROW_MAPPER);
    }
}
