/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.wrapper;

import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.account.web.wrapper.UserVOWrapper;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.crm.service.RoomService;
import com.jinyufeili.minas.poll.data.Answer;
import com.jinyufeili.minas.poll.data.Poll;
import com.jinyufeili.minas.poll.data.Question;
import com.jinyufeili.minas.poll.data.VoteSheet;
import com.jinyufeili.minas.poll.service.AnswerService;
import com.jinyufeili.minas.poll.service.PollService;
import com.jinyufeili.minas.poll.service.QuestionService;
import com.jinyufeili.minas.poll.web.data.QuestionWithAnswerVO;
import com.jinyufeili.minas.poll.web.data.VoteSheetVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class VoteSheetVOWrapper {

    @Autowired
    private UserService userService;

    @Autowired
    private UserVOWrapper userVOWrapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PollService pollService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionWithAnswerVOWrapper questionWithAnswerVOWrapper;

    public VoteSheetVO wrap(VoteSheet voteSheet) {
        return wrap(Collections.singletonList(voteSheet)).get(0);
    }

    public List<VoteSheetVO> wrap(List<VoteSheet> voteSheetList) {
        Set<Integer> residentIds = voteSheetList.stream().map(VoteSheet::getResidentId).collect(Collectors.toSet());
        Set<Integer> roomIds = voteSheetList.stream().map(VoteSheet::getRoomId).collect(Collectors.toSet());
        Set<Integer> pollIds = voteSheetList.stream().map(VoteSheet::getPollId).collect(Collectors.toSet());
        Set<Integer> voteSheetIds = voteSheetList.stream().map(VoteSheet::getId).collect(Collectors.toSet());

        Map<Integer, Room> roomMap = roomService.getByIds(roomIds);
        Map<Integer, Resident> residentMap = residentService.getByIds(residentIds);
        Map<Integer, Poll> pollMap = pollService.getByIds(pollIds);
        Map<Integer, List<Question>> questionListMap = questionService.queryByPollIds(pollIds);
        Map<Integer, List<Answer>> answerMap = answerService.queryByVoteSheetIds(voteSheetIds);
        HashMap<Integer, Question> questionMap =
                questionListMap.values().stream().collect(HashMap<Integer, Question>::new, (map, questionList) -> {
                    for (Question question : questionList) {
                        map.put(question.getId(), question);
                    }
                }, (map1, map2) -> {
                    map1.putAll(map2);
                });
        List<Answer> answerList = answerMap.values().stream().collect(ArrayList<Answer>::new, (memo, curr) -> {
            memo.addAll(curr);
        }, (list1, list2) -> {
            list1.addAll(list2);
        });
        List<QuestionWithAnswerVO> questionWithAnswerVOs = questionWithAnswerVOWrapper.wrap(answerList, questionMap);
        HashMap<Integer, List<QuestionWithAnswerVO>> questionWithAnswerList =
                questionWithAnswerVOs.stream().collect(HashMap<Integer, List<QuestionWithAnswerVO>>::new, (map, vo) -> {
                    if (!map.containsKey(vo.getVoteSheetId())) {
                        map.put(vo.getVoteSheetId(), new ArrayList<>());
                    }
                    map.get(vo.getVoteSheetId()).add(vo);
                }, (map1, map2) -> {
                    map1.putAll(map2);
                });

        return voteSheetList.stream().map(v -> {
            Room room = roomMap.get(v.getRoomId());
            Resident resident = residentMap.get(v.getResidentId());
            Poll poll = pollMap.get(v.getPollId());
            List<QuestionWithAnswerVO> questionWithAnswerVOList = questionWithAnswerList.get(v.getId());
            return wrap(v, room, resident, poll, questionWithAnswerVOList);
        }).collect(Collectors.toList());
    }

    private VoteSheetVO wrap(VoteSheet v, Room room, Resident resident, Poll poll,
                             List<QuestionWithAnswerVO> questions) {
        VoteSheetVO vo = new VoteSheetVO();
        vo.setId(v.getId());
        vo.setResident(resident);
        vo.setRoom(room);
        vo.setVoted(v.isVoted());
        vo.setPoll(poll);
        vo.setQuestions(questions);
        return vo;
    }
}
