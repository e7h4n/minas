package com.jinyufeili.minas.poll.service;

import com.jinyufeili.minas.poll.data.VoteSheet;
import com.jinyufeili.minas.poll.storage.VoteSheetStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by pw on 6/12/16.
 */
@Service
public class VoteSheetService {

    @Autowired
    private VoteSheetStorage voteSheetStorage;

    public List<VoteSheet> getByRoomIds(Set<Integer> roomIds) {
        return voteSheetStorage.getByRoomIds(roomIds);
    }
}
