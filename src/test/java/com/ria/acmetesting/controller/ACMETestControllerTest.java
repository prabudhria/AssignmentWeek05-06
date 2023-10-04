package com.ria.acmetesting.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ria.acmetesting.config.DBContainers;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.respositories.StudentRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public class ACMETestControllerTest {

    @ClassRule
    public static PostgreSQLContainer<DBContainers> postgreSQLContainer = DBContainers.getInstance();
    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    SubjectRepository subjectRepository;
    ObjectMapper objectMapper;
    StudentDTO student;
    MockHttpServletResponse response;
    MultiValueMap<String , String> params;
    @BeforeEach
    public void initialization() throws Exception{
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        student = new StudentDTO( "testUsername","testName", 21);
        params = new LinkedMultiValueMap<>();
        params.add("studentUsername", "testUsername");
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(student)))
                .andReturn().getResponse();

    }

    @AfterEach
    public void removeObjects(){
        studentRepository.findByUsername(student.getUsername()).ifPresent(value -> studentRepository.delete(value));
        params.clear();
    }
    @Test
    public void givenValidDetails_whenRegisterIsCalled_thenIsCreatedIsThrown() throws Exception {
        assertEquals(201, response.getStatus());
        StudentDTO responseStudent = objectMapper.readValue(response.getContentAsString(), StudentDTO.class);
        assertThat(student).isEqualToComparingFieldByField(responseStudent);

    }
    @Test
    public void givenInvalidStudentDetails_whenRegisterIsCalled_thenBadRequestIsThrown() throws Exception{
        StudentDTO nullFieldStudent = new StudentDTO();
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(nullFieldStudent)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals( "The name, age or username cannot be null", response.getContentAsString());
    }
    @Test
    public void givenAlreadyUsedUsername_whenRegisterIsCalled_thenNotAcceptableIsThrown() throws Exception{
        StudentDTO studentWithSameUsername = new StudentDTO( "testUsername","testSecondName", 21);
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(studentWithSameUsername)))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn().getResponse();
        assertEquals( "This username has already been taken, kindly try another username", response.getContentAsString());

    }
    @Test
    public void testGetRemainingSubjects() throws Exception {
        List<String> sampleSubject = new ArrayList<>(List.of("Maths", "Science"));
        params.add("subject", "English");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params));

        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/subject").param("studentUsername", "testUsername"))
                .andReturn().getResponse();
        List<String> receivedSubjectList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<>() {});

        assertEquals(200, response.getStatus());
        assertEquals(sampleSubject, receivedSubjectList);
    }

    @Test
    public void testMarkSubject() throws Exception {
        params.add("subject", "Maths");
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertEquals("Subject \"Maths\" is selected successfully", response.getContentAsString());

    }

    @Test
    public void givenSubjectNotSelected_whenStartTestIsCalled_thenThrowBadRequest() throws Exception {
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals("Select the subject first", response.getContentAsString());
    }

    @Test
    public void givenTestAlreadyStarted_whenStartTestIsCalled_thenThrowBadRequest() throws Exception{
        params.add("subject", "Maths");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params));

        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        QuestionDTO expectedQuestion = new QuestionDTO("What is 2+5", new ArrayList<>(Arrays.asList("1", "4", "5", "7")));
        QuestionDTO receivedQuestion = objectMapper.readValue(response.getContentAsString(), QuestionDTO.class);
        assertThat(expectedQuestion).isEqualToComparingFieldByField(receivedQuestion);


        params.remove("subject");
        params.add("selectedOption", "d");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals("The test has already started", response.getContentAsString());

    }

    @Test
    public void givenTestHasEnded_whenStartTestIsCalled_thenThrowBadRequest() throws Exception {
        params.add("subject", "Maths");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params));
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        params.remove("subject");
        params.add("selectedOption", "d");
        int allowedAttempts = subjectRepository.getAttemptsAllowedOfSubject("Maths");
        while(allowedAttempts!=0){
            mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));
            allowedAttempts--;
        }
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("The test has ended, please select next subject or finish", response.getContentAsString());

    }

    @Test
    public void givenTestNotStarted_whenEvaluateAnswerIsCalled_thenThrowBadRequest() throws Exception{
        params.add("selectedOption", "d");

        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals("Kindly start the test first", response.getContentAsString());

    }
    @Test
    public void testEvaluateStudentAnswer() throws Exception {
        params.add("subject", "Maths");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        params.remove("subject");
        params.add("selectedOption", "d");
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        assertEquals("Answer evaluated successfully", response.getContentAsString());

    }

    @Test
    public void givenTestHasEnded_whenGetNextQuestionIsCalled_thenThrowBadRequest() throws Exception{
        params.add("subject", "Maths");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        params.remove("subject");
        params.add("selectedOption", "d");

        int allowedAttempts = subjectRepository.getAttemptsAllowedOfSubject("Maths");
        while(allowedAttempts!=0){
            mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));
            allowedAttempts--;
        }
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/test").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("The test has ended, please select next subject or finish", response.getContentAsString());
    }
    @Test
    public void testGetNextQuestion() throws Exception {
        params.add("subject", "Maths");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        params.remove("subject");
        params.add("selectedOption", "d");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/test").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        QuestionDTO expectedQuestion = new QuestionDTO("What is 2*5", new ArrayList<>(List.of("2", "4", "10", "15")));
        QuestionDTO receivedQuestion = objectMapper.readValue(response.getContentAsString(), QuestionDTO.class);

        assertThat(expectedQuestion).isEqualToComparingFieldByField(receivedQuestion);
    }

    @Test
    public void testGetScore() throws Exception {
        params.add("subject", "Maths");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        params.remove("subject");
        params.add("selectedOption", "d");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        params.replace("selectedOption", List.of("c"));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        params.replace("selectedOption", List.of("a"));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        params.replace("selectedOption", List.of("b"));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        params.replace("selectedOption", List.of("b"));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());

        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/score").param("studentUsername", "testUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        assertEquals("9", response.getContentAsString());


    }


}
