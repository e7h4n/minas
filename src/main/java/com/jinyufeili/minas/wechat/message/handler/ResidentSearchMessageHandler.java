package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.poll.data.Poll;
import com.jinyufeili.minas.poll.data.VoteSheet;
import com.jinyufeili.minas.poll.service.PollService;
import com.jinyufeili.minas.poll.service.VoteSheetService;
import com.jinyufeili.minas.wechat.message.interceptor.ResidentSearchMessageInterceptor;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class ResidentSearchMessageHandler extends AbstractTextResponseMessageHandler {

    private static final String EMPTY_ROOM_NAME = "";

    @Autowired
    private VoteSheetService voteSheetService;

    @Autowired
    private PollService pollService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        Object objRoomList = context.get(ResidentSearchMessageInterceptor.ROOM_LIST);
        Object objRoomResidentMap = context.get(ResidentSearchMessageInterceptor.ROOM_RESIDENT_MAP);

        if (!(objRoomList instanceof List) || !(objRoomResidentMap instanceof Map)) {
            return null;
        }

        List<Room> roomList = (List<Room>) objRoomList;
        Map<Integer, List<Resident>> roomResidentMap = (Map<Integer, List<Resident>>) objRoomResidentMap;

        List<VoteSheet> voteSheetList = voteSheetService.getByRoomIds(roomList.stream().map(Room::getId).collect(
                Collectors.toSet()));
        Map<Integer, Poll> pollMap = pollService.getByIds(
                voteSheetList.stream().map(VoteSheet::getPollId).collect(Collectors.toSet())).stream().collect(
                Collectors.toMap(Poll::getId, Function.identity()));

        if (CollectionUtils.isEmpty(roomList)) {
            return "没有找到结果，请检查输入格式，例如: 14101, 211101, 陈之光";
        }

        List<String> messageList = roomList.stream().map(r -> {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s\n房屋建面: %.2f平米\n车位面积: %.2f平米", getRoomName(r), r.getRoomArea(),
                    r.getParkingSpaceArea()));

            List<Resident> residents = roomResidentMap.get(r.getId());
            if (!CollectionUtils.isEmpty(residents)) {
                sb.append("\n--------");

                for (Resident resident : residents) {
                    sb.append(String.format("\n业主: %s\n联系方式: %s", resident.getName(), resident.getMobilePhone()));
                }
            }
            return sb.toString();
        }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(voteSheetList)) {
            messageList.add("--------");
            messageList.addAll(voteSheetList.stream().map(
                    voteSheet -> String.format("%s参与情况: %s", pollMap.get(voteSheet.getPollId()).getName(),
                            voteSheet.isVoted() ? "参与" : "未参与或废票")).collect(Collectors.toList()));
        }

        return String.join("\n", messageList);
    }

    private String getRoomName(Room r) {
        if (r.getRegion() == 1) {
            return String.format("爱公馆 %d-%d", r.getBuilding(), r.getHouseNumber());
        } else if (r.getRegion() == 2) {
            return String.format("铂爵郡 %d-%d-%d", r.getBuilding(), r.getUnit(), r.getHouseNumber());
        }

        return EMPTY_ROOM_NAME;
    }
}
