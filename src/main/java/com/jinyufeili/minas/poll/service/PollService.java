package com.jinyufeili.minas.poll.service;

import com.jinyufeili.minas.poll.data.Poll;
import com.jinyufeili.minas.poll.storage.PollStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by pw on 6/12/16.
 */
@Service
public class PollService {

    @Autowired
    private PollStorage pollStorage;

    public List<Poll> getByIds(Set<Integer> collect) {
        return pollStorage.getByIds(collect);
    }
}
