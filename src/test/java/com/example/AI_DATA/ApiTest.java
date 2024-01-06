package com.example.AI_DATA;

import com.example.AI_DATA.bulletin.Service.BulletinService;
import com.example.AI_DATA.bulletin.model.Bulletin;
import org.junit.jupiter.api.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.net.http.HttpClient;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BulletinService bulletinService;

    private Long bulletinId;

    @BeforeEach
    void before() {
        Bulletin bulletin = new Bulletin("test1", "test", null);

        bulletinService.save(bulletin);

        bulletinId = bulletin.getId();
    }

    @Test
    public void api200() throws Exception {
        mockMvc.perform(get("bulletin/view/" + bulletinId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 조회 성공"))
                .andExpect(jsonPath("$.message.data.title").value("test"));
    }

    @Test
    public void api400() throws Exception {
        mockMvc.perform(get("bulletin/view/" + (bulletinId + 1)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("게시글 조회 실패"));

    }
}
