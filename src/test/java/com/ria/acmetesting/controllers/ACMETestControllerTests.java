package com.ria.acmetesting.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ria.acmetesting.config.DBContainers;
import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.exceptions.RequiredQuestionFieldNullException;
import com.ria.acmetesting.exceptionhandling.exceptions.RequiredStudentFieldNullException;
import com.ria.acmetesting.exceptionhandling.exceptions.StudentNotFoundException;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.respositories.StudentRepository;
import com.ria.acmetesting.services.ACMETestServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.Maps;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public class ACMETestControllerTests {

    @ClassRule
    public static PostgreSQLContainer<DBContainers> postgreSQLContainer = DBContainers.getInstance();
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    ObjectMapper objectMapper;
    @BeforeEach
    public void initializeStudentAndQuestion() throws Exception {
        objectMapper = new ObjectMapper();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRegister() throws Exception {
        StudentDTO registeringStudent = new StudentDTO("firstUsername", "name", 21);
        MockHttpServletResponse registrationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registeringStudent)))
                .andReturn().getResponse();

        StudentDTO registeredStudent = objectMapper.readValue(registrationResponse.getContentAsString(), StudentDTO.class);
        assertEquals(201, registrationResponse.getStatus());
        assertThat(registeringStudent).isEqualToComparingFieldByField(registeredStudent);

        StudentDTO nullFieldStudent = new StudentDTO( "username",null, 21);
        MockHttpServletResponse nullStudentResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(nullFieldStudent)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals( "The name, age or username cannot be null", nullStudentResult.getContentAsString());
//        mockMvc.perform(MockMvcRequestBuilders
//                .post("/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(nullFieldStudent)))
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredStudentFieldNullException))
//                .andExpect(result -> assertTrue((result.)));
    }
    @Test
    public void testGetRemainingSubjects() throws Exception {
        StudentDTO studentDTO = new StudentDTO("secondUsername", "name", 22);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)));
        List<String> sampleSubject = new ArrayList<>(List.of("Maths", "Science"));
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("studentUsername", "secondUsername");
        params.add("subject", "English");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/user/subject").param("studentUsername", "secondUsername"))
                .andReturn().getResponse();
        List<String> receivedSubjectList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {});

        assertEquals(200, response.getStatus());
        assertEquals(sampleSubject, receivedSubjectList);

    }

    @Test
    public void testMarkSubject() throws Exception {
        StudentDTO studentDTO = new StudentDTO("thirdUsername", "name", 22);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)));
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("studentUsername", "thirdUsername");
        params.add("subject", "Maths");
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertEquals("Subject \"Maths\" is selected successfully", response.getContentAsString());

    }

    @Test
    public void testStartTest() throws Exception {
        StudentDTO studentDTO = new StudentDTO("fourthUsername", "name", 22);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)));
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("studentUsername", "fourthUsername");
        params.add("subject", "Maths");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "fourthUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("Select the subject first", response.getContentAsString());


        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params));

        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "fourthUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        QuestionDTO expectedQuestion = new QuestionDTO("What is 2+5", new ArrayList<>(Arrays.asList("1", "4", "5", "7")));
        QuestionDTO receivedQuestion = objectMapper.readValue(response.getContentAsString(), QuestionDTO.class);
        assertThat(expectedQuestion).isEqualToComparingFieldByField(receivedQuestion);


        params.remove("subject");
        params.add("selectedOption", "d");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "fourthUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();


        assertEquals("The test has already started", response.getContentAsString());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params));

        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "fourthUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("The test has ended, please select next subject or finish", response.getContentAsString());

    }

    @Test
    public void testEvaluateStudentAnswer() throws Exception {
        StudentDTO studentDTO = new StudentDTO("fifthUsername", "name", 22);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)));
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("studentUsername", "fifthUsername");
        params.add("selectedOption", "d");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("Kindly start the test first", response.getContentAsString());

        params.remove("selectedOption");
        params.add("subject", "Maths");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "fifthUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        params.remove("subject");
        params.add("selectedOption", "d");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/test").param("studentUsername", "fifthUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        QuestionDTO expectedQuestion = new QuestionDTO("What is 2*5", new ArrayList<>(List.of("2", "4", "10", "15")));
        QuestionDTO receivedQuestion = objectMapper.readValue(response.getContentAsString(), QuestionDTO.class);

        assertThat(expectedQuestion).isEqualToComparingFieldByField(receivedQuestion);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("The test has ended, please select next subject or finish", response.getContentAsString());

        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "fifthUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("The test has ended, please select next subject or finish", response.getContentAsString());
    }
    @Test
    public void testGetNextQuestion() throws Exception {
        StudentDTO studentDTO = new StudentDTO("sixthUsername", "name", 22);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)));
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("studentUsername", "sixthUsername");
        params.add("selectedOption", "d");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/user/test").param("studentUsername", "sixthUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("Kindly start the test first", response.getContentAsString());

        params.remove("selectedOption");
        params.add("subject", "Maths");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "sixthUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        params.remove("subject");
        params.add("selectedOption", "d");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/test").param("studentUsername", "sixthUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        QuestionDTO expectedQuestion = new QuestionDTO("What is 2*5", new ArrayList<>(List.of("2", "4", "10", "15")));
        QuestionDTO receivedQuestion = objectMapper.readValue(response.getContentAsString(), QuestionDTO.class);

        assertThat(expectedQuestion).isEqualToComparingFieldByField(receivedQuestion);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/user/test").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        response = mockMvc.perform(MockMvcRequestBuilders.get("/user/test").param("studentUsername", "sixthUsername"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        assertEquals("The test has ended, please select next subject or finish", response.getContentAsString());
    }

    @Test
    public void testGetScore() throws Exception {
        StudentDTO studentDTO = new StudentDTO("seventhUsername", "name", 22);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)));
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("studentUsername", "seventhUsername");
        params.add("subject", "Maths");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/subject").params(params))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/starttest").param("studentUsername", "seventhUsername"))
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

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/user/score").param("studentUsername", "seventhUsername"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        assertEquals("9", response.getContentAsString());


    }


}
