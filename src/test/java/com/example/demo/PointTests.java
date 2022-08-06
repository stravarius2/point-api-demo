package com.example.demo;


import com.example.demo.configuration.EnableMockMvc;
import com.example.demo.exception.PointNotEnoughException;
import com.example.demo.model.condition.PointCondition;
import com.example.demo.model.condition.PointHistoryCondition;
import com.example.demo.repository.point.PointBalanceRepository;
import com.example.demo.repository.point.PointHistoryDetailRepository;
import com.example.demo.repository.point.PointHistoryRepository;
import com.example.demo.repository.point.entity.PointHistoryDetailEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static com.example.demo.meta.PointActionType.SAVE;
import static com.example.demo.meta.PointActionType.USE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@EnableMockMvc
public class PointTests {

    @Autowired
    MockMvc mockMvc;

    private Long memberId;

    private ObjectMapper objectMapper;

    @Autowired
    private PointBalanceRepository pointBalanceRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private PointHistoryDetailRepository pointHistoryDetailRepository;

    @BeforeEach
    public void init(){
        memberId = 389457435258923893L;
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void delete(){
        pointBalanceRepository.deleteAllByMemberId(memberId);
    }

    @Test
    public void 포인트_적립_조회() throws Exception {

        //10000포인트 적립
        //1000포인트 적립
        //잔여포인트 11000포인트

        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(10000L);

        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", this.memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        pointCondition.setPoint(1000L);

        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", this.memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/point")
                        .header("memberId", this.memberId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertEquals(response.getLong("totalPoint"), 11000L);
        Assertions.assertEquals(response.getLong("memberId"), memberId);
    }

    @Test
    public void 포인트_사용() throws Exception {

        //10000포인트 적립
        //5000포인트 사용
        //잔여포인트 5000
        //7000포인트 사용
        //초과 사용이므로 PointNotEnoughException

        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(10000L);

        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", this.memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        pointCondition = new PointCondition();
        pointCondition.setPoint(5000L);

        mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/point")
                        .header("memberId", memberId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertEquals(response.getLong("totalPoint"), 5000L);
        Assertions.assertEquals(response.getLong("memberId"), memberId);

        pointCondition.setPoint(7000L);

        mockMvc.perform(post("/api/v1/point/use")
                .header("memberId", memberId)
                .content(objectMapper.writeValueAsString(pointCondition))
                .contentType(APPLICATION_JSON))
                .andExpect(responses-> assertTrue(responses.getResolvedException() instanceof PointNotEnoughException));

    }

    @Test
    public void 포인트_적립_목록_조회() throws Exception{

        //10000원 적립
        //5000원 사용
        //조회 결과
        //5000원 사용
        //10000원 적립
        Long savePoint = 10000L;
        Long usePoint = 5000L;

        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(savePoint);

        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        pointCondition.setPoint(usePoint);

        mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        PointHistoryCondition historyCondition = new PointHistoryCondition();
        historyCondition.setPage(1);
        historyCondition.setSize(10);

        MvcResult result = mockMvc.perform(get("/api/v1/point/history")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(historyCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        JSONArray response = new JSONObject(result.getResponse().getContentAsString()).getJSONArray("content");

        Assertions.assertEquals(response.getJSONObject(0).getString("pointActionType"), USE.getDescription());
        Assertions.assertEquals(response.getJSONObject(0).getLong("point"), usePoint * -1);

        Assertions.assertEquals(response.getJSONObject(1).getString("pointActionType"), SAVE.getDescription());
        Assertions.assertEquals(response.getJSONObject(1).getLong("point"), savePoint);
    }

    @Test
    public void 포인트_사용중_만료() throws Exception{

        //1000포인트 X 3 적립
        //1500포인트 사용 -> 1500포인트 남음(1-1000포인트 + 2-500포인트)
        //2번째로 적립된 1000포인트 유효기간 현재시간 보다 이전으로 변경
        //500포인트 사용 / 500원이 남아있으나 유효기간이 경과 하였으므로 만료 처리 / 500포인트 만료 -> 1000포인트 남음
        //첫번째로 적립된 1000포인트중 500포인트 사용
        //남은 포인트 500

        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(1000L);

        for(int i = 0; i < 3; i++){
            mockMvc.perform(post("/api/v1/point/save")
                            .header("memberId", memberId)
                            .content(objectMapper.writeValueAsString(pointCondition))
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        pointCondition.setPoint(1500L);

        MvcResult result =  mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertEquals(response.getLong("totalPoint"), 1500L);

        Long historyId = pointHistoryRepository.findAllByMemberId(memberId).get(2).getId();
        PointHistoryDetailEntity detailEntity = pointHistoryDetailRepository.findAllByPointHistoryId(historyId).get(1);
        detailEntity.setExpiredAt(LocalDate.now().minusDays(1));
        pointHistoryDetailRepository.save(detailEntity);

        pointCondition.setPoint(500L);

        result = mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());
        Assertions.assertEquals(response.getLong("totalPoint"), 500L);

    }

}

