package com.ria.acmetesting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ria.acmetesting.config.DBContainers;
import com.ria.acmetesting.dbentities.*;
import com.ria.acmetesting.respositories.QuestionRepository;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class AdminControllerTest {
    @ClassRule
    public static PostgreSQLContainer<DBContainers> postgreSQLContainer = DBContainers.getInstance();
    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    QuestionRepository questionRepository;
    ObjectMapper objectMapper;
    @BeforeEach
    public void initializeObjectMapper(){
        objectMapper = new ObjectMapper();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }
    @Test
    void addQuestion() throws Exception{
        Question question = new Question();
        MockHttpServletResponse  response = mockMvc.perform(MockMvcRequestBuilders.post("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("Question statement, subject, options and answer cannot be null", response.getContentAsString());

        question = new Question(1, "What is 6+7", "Maths",
                new ArrayList<>(List.of("3", "5", "13", "17")), "c");
        response = mockMvc.perform(MockMvcRequestBuilders.post("/admin/question")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();

        Question addedQuestion = objectMapper.readValue(response.getContentAsString(), Question.class);
        assertThat(addedQuestion).isEqualToIgnoringGivenFields(question, "id");

    }

    @Test
    void getQuestionById() throws Exception {
        Question question = new Question(1, 1, "What is 2+5", "Maths",
                new ArrayList<>(List.of("1", "4", "5", "7")), "d");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question")
                .param("questionId", "1"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        Question receivedQuestion = objectMapper.readValue(response.getContentAsString(), Question.class);
        assertThat(question).isEqualToComparingFieldByField(receivedQuestion);

        response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question")
                        .param("questionId", "324"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Question not found", response.getContentAsString());

    }

    @Test
    void getQuestionByStatement() throws Exception{
        Question question = new Question(1, 1, "What is 2+5", "Maths",
                new ArrayList<>(List.of("1", "4", "5", "7")), "d");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question/statement")
                        .param("questionStatement", "What is 2+5"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        Question receivedQuestion = objectMapper.readValue(response.getContentAsString(), Question.class);
        assertThat(question).isEqualToComparingFieldByField(receivedQuestion);

        response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question/statement")
                        .param("questionStatement", "What is not 2+5"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Question not found", response.getContentAsString());
    }

    @Test
    void updateQuestion() throws Exception{
        Question question = new Question();
        MockHttpServletResponse  response = mockMvc.perform(MockMvcRequestBuilders.put("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("Question statement, subject, options and answer cannot be null", response.getContentAsString());

        question = new Question(15, 3, "What is y if 3/y + 1 = 2", "Maths",
                new ArrayList<>(List.of("4", "3", "5", "6")), "b");
        response = mockMvc.perform(MockMvcRequestBuilders.put("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        Question updatedQuestion = objectMapper.readValue(response.getContentAsString(), Question.class );
        assertThat(updatedQuestion).isEqualToComparingFieldByField(question);
    }

    @Test
    void deleteQuestion() throws Exception{
        MockHttpServletResponse  response = mockMvc.perform(MockMvcRequestBuilders.delete("/admin/question")
                        .param("questionId", "252345"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Question not found", response.getContentAsString());
        response = mockMvc.perform(MockMvcRequestBuilders.delete("/admin/question")
                        .param("questionId", "15"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        assertEquals("Deleted successfully", response.getContentAsString());

    }

    @Test
    void addSubject() throws Exception{
        Subject subject = new Subject();
        MockHttpServletResponse  response = mockMvc.perform(MockMvcRequestBuilders.post("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(subject)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals("Subject name and allowed-attempts cannot be null", response.getContentAsString());

        subject = new Subject("Logic", 5, new HashSet<>());

        response = mockMvc.perform(MockMvcRequestBuilders.post("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(subject)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();

        Subject receivedSubject = objectMapper.readValue(response.getContentAsString(), Subject.class);

        assertThat(receivedSubject).isEqualToIgnoringGivenFields(subject, "id");

    }

    @Test
    void getSubjectById() throws Exception {
        Subject subject = new Subject(2, "English", 5, new HashSet<>());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject")
                        .param("subjectId", "2"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();
        Subject receivedSubject = objectMapper.readValue(response.getContentAsString(), Subject.class);

        assertThat(receivedSubject).isEqualToComparingFieldByField(subject);

        response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject")
                        .param("subjectId", "324"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Subject not found", response.getContentAsString());
    }

    @Test
    void getSubjectByName() throws Exception {
        Subject subject = new Subject(2, "English", 5, new HashSet<>());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject/name")
                        .param("subjectName", "English"))
                .andReturn().getResponse();

        Subject receivedSubject = objectMapper.readValue(response.getContentAsString(), Subject.class);

        assertThat(receivedSubject).isEqualToComparingFieldByField(subject);

        response = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject/name")
                        .param("subjectName", "GK"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Subject not found", response.getContentAsString());
    }

    @Test
    void updateSubject() throws Exception{
        Subject subject = new Subject();
        MockHttpServletResponse  response = mockMvc.perform(MockMvcRequestBuilders.put("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(subject)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("Subject name and allowed-attempts cannot be null", response.getContentAsString());

        subject = new Subject(2,"English", 6, new HashSet<>());

        response = mockMvc.perform(MockMvcRequestBuilders.put("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(subject)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        Subject receivedSubject = objectMapper.readValue(response.getContentAsString(), Subject.class);
        assertThat(receivedSubject).isEqualToComparingFieldByField(subject);
    }

    @Test
    void deleteSubject() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete("/admin/subject")
                        .param("subjectId", "252345"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Subject not found", response.getContentAsString());

        Subject subject = new Subject("GK", 5, new HashSet<>());

        response = mockMvc.perform(MockMvcRequestBuilders.post("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(subject)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();

        Subject addedSubject = objectMapper.readValue(response.getContentAsString(), Subject.class);
        assertThat(subject).isEqualToIgnoringGivenFields(addedSubject, "id");

        int  addedSubjectId = addedSubject.getId();
        response = mockMvc.perform(MockMvcRequestBuilders.delete("/admin/subject")
                        .param("subjectId", Integer.toString(addedSubjectId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        assertEquals("Deleted successfully", response.getContentAsString());
    }
}