package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.controller.FilmController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController filmController;

    @Test
    void contextLoads() {
        assertThat(filmController).isNotNull();
    }

    @Test
    void whenCorrectRequestShouldReturns200() throws Exception {
        String requestBody = "{\"name\":\"Avatar\",\"description\":\"fantastic\",\"releaseDate\":\"2009-12-17\",\"duration\":\"162\"}";
        MvcResult result =  mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
        JsonObject convertedObject = new Gson().fromJson(result.getResponse().getContentAsString(), JsonObject.class);
        Assert.assertTrue("Неверное имя",convertedObject.get("name").getAsString().equals("Avatar"));
        Assert.assertTrue("Неверное описание",convertedObject.get("description").getAsString().equals("fantastic"));
        Assert.assertTrue("Неверная дата релиза",convertedObject.get("releaseDate").getAsString().equals("2009-12-17"));
        Assert.assertTrue("Неверная продолжительность фильма",convertedObject.get("duration").getAsString().equals("162"));
    }

    @Test
    void whenNameEmptyShouldReturns400() throws Exception {
        String requestBody = "{\"name\":\"\",\"description\":\"fantastic\",\"releaseDate\":\"2009-12-17\",\"duration\":\"162\"}";
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDescriptionBigger200SimbolsReturns400() throws Exception {
        String requestBody = String.format("{\"name\":\"Avatar\",\"description\":\"%s\",\"releaseDate\":\"2009-12-17\",\"duration\":\"162\"}", StringUtils.repeat('#', 201));
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenReleaseDateLessMinShouldReturn400() throws Exception {
        String requestBody = "{\"name\":\"Avatar\",\"description\":\"fantastic\",\"releaseDate\":\"1890-12-28\",\"duration\":\"162\"}";
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDurationIsNegativeReturn400() throws Exception {
        String requestBody = "{\"name\":\"Avatar\",\"description\":\"fantastic\",\"releaseDate\":\"2009-12-17\",\"duration\":\"-162\"}";
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
