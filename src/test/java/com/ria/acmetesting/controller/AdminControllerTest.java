package com.ria.acmetesting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ria.acmetesting.config.DBContainers;
import com.ria.acmetesting.dbentities.*;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    @Autowired
    SubjectRepository subjectRepository;
    ObjectMapper objectMapper;
    Question question;
    Subject subject;
    MockHttpServletResponse questionResponse;
    MockHttpServletResponse subjectResponse;

    @BeforeEach
    public void initializeObjectMapper() throws Exception {
        objectMapper = new ObjectMapper();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        question = new Question(1, "What is 6+7", "Maths",
                new ArrayList<>(List.of("3", "5", "13", "17")), "c");
        subject = new Subject("Logic", 5, new HashSet<>());
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.post("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.post("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(subject)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
    }

    @AfterEach
    public void removeObjects() {
        questionRepository.findQuestionByStatement("What is 6+7")
                .ifPresent(value -> questionRepository.delete(value));
        subjectRepository.findSubjectByName("Logic")
                .ifPresent(value -> subjectRepository.delete(value));
    }

    @Test
    public void givenAnyNullQuestionField_whenAddQuestionIsCalled_thenBadRequestIsThrown() throws Exception {
        question = new Question();
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.post("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn().getResponse();
        assertEquals("Question statement, subject, options and answer cannot be null", questionResponse.getContentAsString());
    }

    @Test
    void givenSubjectNotExist_whenAddQuestionIsCalled_thenIsNotFoundIsThrown() throws Exception {
        Question questionWithWrongSubject = new Question(1, "Which is the tallest builind in the world", "GK",
                new ArrayList<>(List.of("Burj Khalifa", "Shanghai Tower", "Makkah Royal Clock Tower", "Ping An Finance Centre")), "a");
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.post("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(questionWithWrongSubject)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Subject not found", questionResponse.getContentAsString());

    }

    @Test
    void givenCorrectQuestionFields_whenAddQuestionIsCalled_thenIsCreatedIsThrown() throws Exception {
        assertEquals(201, questionResponse.getStatus());
        Question addedQuestion = objectMapper.readValue(questionResponse.getContentAsString(), Question.class);
        assertThat(addedQuestion).isEqualToIgnoringGivenFields(question, "id");
    }

    @Test
    void givenCorrectIdRequested_whenGetQuestionByIdIsCalled_thenIsFoundIsThrown() throws Exception {
        int addedQuestionId = questionRepository.findQuestionByStatement("What is 6+7").get().getId();
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question")
                        .param("questionId", Integer.toString(addedQuestionId)))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        Question receivedQuestion = objectMapper.readValue(questionResponse.getContentAsString(), Question.class);
        assertThat(question).isEqualToIgnoringGivenFields(receivedQuestion, "id");
    }

    @Test
    void givenWrongIdRequested_whenGetQuestionByIdIsCalled_thenNotFoundIsThrown() throws Exception {
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question")
                        .param("questionId", "324"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Question not found", questionResponse.getContentAsString());

    }

    @Test
    void givenCorrectStatementRequested_whenGetQuestionByStatementIsCalled_thenIsFoundIsThrown() throws Exception {
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question/statement")
                        .param("questionStatement", "What is 6+7"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        Question receivedQuestion = objectMapper.readValue(questionResponse.getContentAsString(), Question.class);
        assertThat(question).isEqualToIgnoringGivenFields(receivedQuestion, "id");


    }

    @Test
    void givenWrongStatementRequested_whenGetQuestionByStatementIsCalled_thenNotFoundIsThrown() throws Exception {
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/question/statement")
                        .param("questionStatement", "What is not 6+7"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Question not found", questionResponse.getContentAsString());
    }

    @Test
    void givenIncompleteQuestionField_whenUpdateQuestionIsCalled_thenBadRequestIsThrown() throws Exception {
        Question incompleteFieldQuestion = new Question();
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.put("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(incompleteFieldQuestion)))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn().getResponse();

        assertEquals("Question statement, subject, options and answer cannot be null", questionResponse.getContentAsString());
    }

    @Test
    void givenCompleteQuestionField_whenUpdateQuestionIsCalled_thenIsOkIsThrown() throws Exception {
        int addedQuestionId = questionRepository.findQuestionByStatement("What is 6+7").get().getId();
        question = new Question(addedQuestionId, 1, "What is not 6+7", "",
                new ArrayList<>(List.of("3", "5", "13", "17")), "c");

        questionResponse = mockMvc.perform(MockMvcRequestBuilders.put("/admin/question")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        Question updatedQuestion = objectMapper.readValue(questionResponse.getContentAsString(), Question.class);
        assertThat(updatedQuestion).isEqualToComparingFieldByField(question);
    }

    @Test
    void givenCorrectQuestionId_whenDeleteQuestionIsCalled_thenIsOkIsThrown() throws Exception {
        int addedQuestionId = questionRepository.findQuestionByStatement("What is 6+7").get().getId();
        questionResponse = mockMvc.perform(MockMvcRequestBuilders.delete("/admin/question")
                        .param("questionId", Integer.toString(addedQuestionId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        assertEquals("Deleted successfully", questionResponse.getContentAsString());

    }

    //Subjects test starts here
    @Test
    public void givenAnyNullSubjectField_whenAddSubjectIsCalled_thenBadRequestIsThrown() throws Exception {
        Subject nullFieldSubject = new Subject();
        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.post("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(nullFieldSubject)))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn().getResponse();
        assertEquals("Subject name and allowed-attempts cannot be null", subjectResponse.getContentAsString());
    }


    @Test
    void givenCorrectSubjectFields_whenAddSubjectIsCalled_thenIsCreatedIsThrown() throws Exception {
        assertEquals(201, subjectResponse.getStatus());
        Subject addedSubject = objectMapper.readValue(subjectResponse.getContentAsString(), Subject.class);
        assertThat(addedSubject).isEqualToIgnoringGivenFields(subject, "id");
    }

    @Test
    void givenCorrectIdRequested_whenGetSubjectByIdIsCalled_thenIsFoundIsThrown() throws Exception {
        int addedSubjectId = subjectRepository.findSubjectByName(subject.getName()).get().getId();

        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject")
                        .param("subjectId", Integer.toString(addedSubjectId)))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        Subject addedSubject = objectMapper.readValue(subjectResponse.getContentAsString(), Subject.class);
        assertThat(addedSubject).isEqualToIgnoringGivenFields(subject, "id");
    }

    @Test
    void givenWrongIdRequested_whenGetSubjectByIdIsCalled_thenNotFoundIsThrown() throws Exception {
        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject")
                        .param("subjectId", "324"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Subject not found", subjectResponse.getContentAsString());

    }

    @Test
    void givenCorrectSubjectNameRequested_whenGetSubjectByNameIsCalled_thenIsFoundIsThrown() throws Exception {
        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject/name")
                        .param("subjectName", "Logic"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        Subject addedSubject = objectMapper.readValue(subjectResponse.getContentAsString(), Subject.class);
        assertThat(addedSubject).isEqualToIgnoringGivenFields(subject, "id");


    }

    @Test
    void givenWrongSubjectNameRequested_whenGetSubjectByNameIsCalled_thenNotFoundIsThrown() throws Exception {
        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.get("/admin/subject/name")
                        .param("subjectName", "GK"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        assertEquals("Subject not found", subjectResponse.getContentAsString());
    }

    @Test
    void givenIncompleteSubjectField_whenUpdateSubjectIsCalled_thenBadRequestIsThrown() throws Exception {
        Subject incompleteFieldSubject = new Subject();
        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.put("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(incompleteFieldSubject)))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn().getResponse();

        assertEquals("Subject name and allowed-attempts cannot be null", subjectResponse.getContentAsString());
    }

    @Test
    void givenCompleteSubjectField_whenUpdateSubjectIsCalled_thenIsOkIsThrown() throws Exception {
        int addedSubjectId = subjectRepository.findSubjectByName("Logic").get().getId();
        subject = new Subject(addedSubjectId,"Logic", 6, new HashSet<>());

        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.put("/admin/subject")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(subject)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        Subject updatedSubject = objectMapper.readValue(subjectResponse.getContentAsString(), Subject.class);
        assertThat(updatedSubject).isEqualToComparingFieldByField(subject);
    }

    @Test
    void givenCorrectSubjectId_whenDeleteSubjectIsCalled_thenIsOkIsThrown() throws Exception {
        int addedSubjectId = subjectRepository.findSubjectByName("Logic").get().getId();
        subjectResponse = mockMvc.perform(MockMvcRequestBuilders.delete("/admin/subject")
                        .param("subjectId", Integer.toString(addedSubjectId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        assertEquals("Deleted successfully", subjectResponse.getContentAsString());

    }
}