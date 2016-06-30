package com.jinyufeili.minas.poll.service;

import com.jinyufeili.minas.poll.data.Poll;
import com.jinyufeili.minas.poll.data.PollStatus;
import com.jinyufeili.minas.poll.storage.PollStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pw on 6/12/16.
 */
@Service
public class PollService {

    @Autowired
    private PollStorage pollStorage;

    public Map<Integer, Poll> getByIds(Set<Integer> pollIds) {
        return pollStorage.getByIds(pollIds);
    }

    public List<Poll> query(Set<PollStatus> status) {
        return pollStorage.query(status);
    }

    public Poll get(int pollId) {
        return pollStorage.get(pollId);
    }
}
