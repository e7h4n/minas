package com.jinyufeili.minas.poll.storage;

import com.jinyufeili.minas.poll.data.VoteSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by pw on 6/12/16.
 */
@Repository
public class VoteSheetStorage {

    private static final RowMapper<VoteSheet> ROW_MAPPER = ((rs, rowNum) -> {
        VoteSheet voteSheet = new VoteSheet();

        voteSheet.setId(rs.getInt("id"));
        voteSheet.setCreatedTime(rs.getDate("created_time").getTime());
        Date voteTime = rs.getDate("vote_time");
        voteSheet.setVotedTime(voteTime == null ? 0 : voteTime.getTime());
        voteSheet.setVoted(rs.getBoolean("voted"));
        voteSheet.setPollId(rs.getInt("poll_id"));
        voteSheet.setResidentId(rs.getInt("resident_id"));
        voteSheet.setRoomId(rs.getInt("room_id"));

        return voteSheet;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public List<VoteSheet> getByRoomIds(Set<Integer> roomIds) {
        if (CollectionUtils.isEmpty(roomIds)) {
            return Collections.emptyList();
        }

        return db.query("SELECT * FROM poll_votesheet WHERE room_id IN (:roomIds)",
                Collections.singletonMap("roomIds", roomIds), ROW_MAPPER);
    }
}
