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
    public void ?????????_??????() throws Exception {

        //10000????????? ??????
        //1000????????? ??????
        //??????????????? 11000?????????

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
    public void ?????????_??????() throws Exception {

        //10000????????? ??????
        //5000????????? ??????
        //??????????????? 5000
        //7000????????? ??????
        //?????? ??????????????? PointNotEnoughException

        JSONObject response;

        //10000 ??????
        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(10000L);
        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        //5000 ??????
        pointCondition.setPoint(5000L);
        mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        //????????? ?????? ??????
        MvcResult result = mockMvc.perform(get("/api/v1/point")
                        .header("memberId", memberId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertEquals(response.getLong("totalPoint"), 5000L);

        //?????? 5000 ???????????? 7000??? ?????? ??????
        pointCondition.setPoint(7000L);
        mockMvc.perform(post("/api/v1/point/use")
                .header("memberId", memberId)
                .content(objectMapper.writeValueAsString(pointCondition))
                .contentType(APPLICATION_JSON))
                .andExpect(responses-> assertTrue(responses.getResolvedException() instanceof PointNotEnoughException));

    }

    @Test
    public void ?????????_??????_??????() throws Exception{

        //10000??? ??????
        //5000??? ??????
        //?????? ??????
        //5000??? ??????
        //10000??? ??????

        JSONArray responseArray;
        long savePoint = 10000L;
        long usePoint = 5000L;

        //10000 ??????
        PointCondition pointCondition = new PointCondition();
        pointCondition.setPoint(savePoint);
        mockMvc.perform(post("/api/v1/point/save")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        //5000 ??????
        pointCondition.setPoint(usePoint);
        mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        //?????? ??????
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

        //?????? ????????? ????????? ?????? -> ??????
        Assertions.assertEquals(responseArray.getJSONObject(0).getString("pointActionType"), USE.getDescription());
        Assertions.assertEquals(responseArray.getJSONObject(0).getLong("point"), usePoint * -1);

        Assertions.assertEquals(responseArray.getJSONObject(1).getString("pointActionType"), SAVE.getDescription());
        Assertions.assertEquals(responseArray.getJSONObject(1).getLong("point"), savePoint);
    }

    @Test
    public void ?????????_?????????_??????() throws Exception{

        //1000????????? X 3 ??????
        //1500????????? ?????? -> 1500????????? ??????(1-1000????????? + 2-500?????????)
        //2????????? ????????? 1000????????? ???????????? ???????????? ?????? ???????????? ??????
        //500????????? ?????? / 500?????? ??????????????? ??????????????? ?????? ??????????????? ?????? ?????? / 500????????? ?????? -> 1000????????? ??????
        //???????????? ????????? 1000???????????? 500????????? ??????
        //?????? ????????? 500

        JSONObject response;

        //3000 ??????
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

        //1500 ??????
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

        //?????? 500?????? ????????? ????????? ????????? ?????? ?????? ??????
        Long historyId = pointHistoryRepository.findAllByMemberId(memberId).get(2).getId();
        PointHistoryDetailEntity detailEntity = pointHistoryDetailRepository.findAllByPointHistoryId(historyId).get(1);
        detailEntity.setExpiredAt(LocalDate.now().minusDays(1));
        pointHistoryDetailRepository.save(detailEntity);

        //500 ??????
        pointCondition.setPoint(500L);
        result = mockMvc.perform(post("/api/v1/point/use")
                        .header("memberId", memberId)
                        .content(objectMapper.writeValueAsString(pointCondition))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        response = new JSONObject(result.getResponse().getContentAsString());

        //?????? 1500 -> 500????????? ?????? -> 500????????? ?????? -> ?????? 500
        Assertions.assertEquals(response.getLong("totalPoint"), 500L);

    }

    @Test
    public void ?????????_????????????() throws Exception{

        //1000????????? X 3 ??????
        //2500????????? ??????
        //????????????
        //3000????????? ??????
        //?????? ??????

        JSONObject response;
        JSONArray responseArray;

        //?????? 3000
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

        //?????? 2500
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

        //?????? ??????
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

        //????????????
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

        //????????????
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

