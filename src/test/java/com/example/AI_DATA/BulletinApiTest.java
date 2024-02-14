package com.example.AI_DATA;

import com.example.AI_DATA.BulletinApiController;
import com.example.AI_DATA.bulletin.Service.BulletinService;
import com.example.AI_DATA.bulletin.model.Bulletin;
import com.example.AI_DATA.bulletin.repository.BulletinRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;


import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BulletinApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BulletinService bulletinService;

    @Autowired
    private BulletinApiController bulletinApiController;


    private Long bulletinId;

    @BeforeEach
    void before() {
        bulletinId = this.bulletinService.getLatestBulletinId();
    }

//    @BeforeEach //replace with making new bulletin test
//    void before() {
//        Bulletin bulletin = new Bulletin("test1", "test", null);
//
//        bulletinService.save(bulletin);
//
//        bulletinId = bulletin.getId();
//    }

//    @Test
//    @DisplayName("Bulletin Save Test without Image")
//    public void bulletinSaveTestWithOutImage() throws Exception {
//        Bulletin bulletin = new Bulletin("test1", "test", null);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String bulletinJson = objectMapper.writeValueAsString(bulletin);
//
//        MvcResult mvcResult = mockMvc.perform(post("/bulletin/save")
//                .contentType("application/json")
//                .content(bulletinJson))
//                .andExpect(status().is2xxSuccessful())
//                .andReturn();
//
//    }

    @Test
    @DisplayName("Bulletin Save Test With Image")
    @Order(1)
    public void bulletinSaveTestWithImage() throws Exception {
        Bulletin bulletin = new Bulletin("test1", "test", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String bulletinJson = objectMapper.writeValueAsString(bulletin);

        String filePath = "C:\\Users\\dokyu\\OneDrive - UOS\\바탕 화면\\AI_DATA\\src\\test\\java\\com\\example\\AI_DATA\\image\\00BY54RO.jpg";
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                fileContent
        );

//        assert file.getBytes().length!=0;

        MvcResult mvcResult = mockMvc.perform(multipart("/bulletin/save/image")
                                .file(file)
                                .content(bulletinJson)
                                .contentType("application/json")
                        )
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }



    @Test
    @DisplayName("View Test")
    @Order(2)
    public void api200() throws Exception {
        mockMvc.perform(get("/bulletin/view/" + bulletinId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 조회 성공"))
                .andExpect(jsonPath("$.data.title").value("test1"));
    }

    @Test
    @DisplayName("Invalid View Test")
    @Order(3)
    public void api400() throws Exception {
        mockMvc.perform(get("/bulletin/view/" + (bulletinId + 1)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("게시글 조회 실패"));
    }

    @Test
    @DisplayName("Get AI Prediction Test")
    @Order(4)
    public void getAiPrediction() throws Exception {
        mockMvc.perform(get("/bulletin/prediction/" + bulletinId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Modify Bulletin test")
    @Order(5)
    public void modifyBulletin() throws Exception {
        BulletinApiController bulletinApiController = new BulletinApiController(bulletinService);

        mockMvc = MockMvcBuilders.standaloneSetup(bulletinApiController).build();

        Bulletin modifiedBulletin = new Bulletin("modified title", "modified content", null);

        ObjectMapper objectMapper = new ObjectMapper();
        String modifiedBulletinJson = objectMapper.writeValueAsString(modifiedBulletin);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/bulletin/modify/" + bulletinId)
                .content(modifiedBulletinJson)
                .contentType("application/json");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 수정 성공"))
                .andExpect(jsonPath("$.data.title").value("modified title"))
                .andDo(print());
    }

    @Test
    @DisplayName("Delete Bulletin test")
    @Order(6)
    public void bulletinDeleteTest() throws Exception {
        mockMvc.perform(delete("/bulletin/delete/" + bulletinId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));

        mockMvc.perform(get("/bulletin/view/" + bulletinId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("게시글 조회 실패"));
    }

}
