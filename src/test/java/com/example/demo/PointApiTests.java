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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
public class PointApiTests {

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
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        pointCondition.setPoint(1000L);

        mockMvc.perform(post("/api/v1/point/save")
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

        Assertions.assertEquals(response.getLong("totalPoint"), 11000L);

    }

    @Test
    public void 포인트_사용() throws Exception {

        //10000포인트 적립
        //5000포인트 사용
        //잔여포인트 5000
        //7000포인트 사용
        //초과 사용이므로 PointNotEnoughException

        JSONObject response;

        //10000 적립
        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(10000L);
        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        //5000 사용
        pointCondition.setPoint(5000L);
        mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        //포인트 잔액 조회
        MvcResult result = mockMvc.perform(get("/api/v1/point")
                        .header("memberId", memberId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertEquals(response.getLong("totalPoint"), 5000L);

        //잔여 5000 사용요청 7000시 실패 확인
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

        JSONArray responseArray;
        long savePoint = 10000L;
        long usePoint = 5000L;

        //10000 적립
        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(savePoint);
        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        //5000 사용
        pointCondition.setPoint(usePoint);
        mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        //내역 조회
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

        responseArray = new JSONObject(result.getResponse().getContentAsString()).getJSONArray("content");

        //조회 결과는 최근순 사용 -> 적립
        Assertions.assertEquals(responseArray.getJSONObject(0).getString("pointActionType"), USE.getDescription());
        Assertions.assertEquals(responseArray.getJSONObject(0).getLong("point"), usePoint * -1);

        Assertions.assertEquals(responseArray.getJSONObject(1).getString("pointActionType"), SAVE.getDescription());
        Assertions.assertEquals(responseArray.getJSONObject(1).getLong("point"), savePoint);
    }

    @Test
    public void 포인트_사용중_만료() throws Exception{

        //1000포인트 X 3 적립
        //1500포인트 사용 -> 1500포인트 남음(1-1000포인트 + 2-500포인트)
        //2번째로 적립된 1000포인트 유효기간 현재시간 보다 이전으로 변경
        //500포인트 사용 / 500원이 남아있으나 유효기간이 경과 하였으므로 만료 처리 / 500포인트 만료 -> 1000포인트 남음
        //첫번째로 적립된 1000포인트중 500포인트 사용
        //남은 포인트 500

        JSONObject response;

        //3000 적립
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

        //1500 사용
        pointCondition.setPoint(1500L);
        MvcResult result =  mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());
        Assertions.assertEquals(response.getLong("totalPoint"), 1500L);

        //이미 500원이 사용된 두번째 적립의 기간 만료 처리
        Long historyId = pointHistoryRepository.findAllByMemberId(memberId).get(2).getId();
        PointHistoryDetailEntity detailEntity = pointHistoryDetailRepository.findAllByPointHistoryId(historyId).get(1);
        detailEntity.setExpiredAt(LocalDate.now().minusDays(1));
        pointHistoryDetailRepository.save(detailEntity);

        //500 사용
        pointCondition.setPoint(500L);
        result = mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());

        //잔여 1500 -> 500포인트 만료 -> 500포인트 사용 -> 잔여 500
        Assertions.assertEquals(response.getLong("totalPoint"), 500L);

    }

    @Test
    public void 포인트_사용취소() throws Exception{

        //1000포인트 X 3 적립
        //2500포인트 사용
        //사용취소
        //3000포인트 확인
        //내역 확인

        JSONObject response;
        JSONArray responseArray;

        //적립 3000
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

        //사용 2500
        pointCondition.setPoint(2500L);
        MvcResult result =  mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());
        Assertions.assertEquals(response.getLong("totalPoint"), 500L);

        //내역 확인
        PointHistoryCondition historyCondition = new PointHistoryCondition();
        historyCondition.setPage(1);
        historyCondition.setSize(10);

        result = mockMvc.perform(get("/api/v1/point/history")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(historyCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        responseArray = new JSONObject(result.getResponse().getContentAsString()).getJSONArray("content");
        Assertions.assertEquals(responseArray.getJSONObject(0).getString("pointActionType"), USE.getDescription());
        Assertions.assertEquals(responseArray.getJSONObject(0).getLong("point"), -2500L);

        //사용취소
        Long historyId = responseArray.getJSONObject(0).getLong("id");
        result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/point/cancel")
                        .header("memberId", memberId)
                        .param("id", String.valueOf(historyId))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());
        Assertions.assertEquals(response.getLong("totalPoint"), 3000L);

        //내역확인
        result = mockMvc.perform(get("/api/v1/point/history")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(historyCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        responseArray = new JSONObject(result.getResponse().getContentAsString()).getJSONArray("content");

        Assertions.assertEquals(responseArray.getJSONObject(0).getString("pointActionType"), SAVE.getDescription());
        Assertions.assertEquals(responseArray.getJSONObject(0).getLong("point"), 1000L);

    }

}

