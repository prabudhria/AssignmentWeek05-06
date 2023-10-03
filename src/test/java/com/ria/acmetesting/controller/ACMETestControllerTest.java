package com.ria.acmetesting.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ria.acmetesting.config.DBContainers;
import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.respositories.QuestionRepository;
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

    ObjectMapper objectMapper;
    @BeforeEach
    public void initializeObjectMapper(){
        objectMapper = new ObjectMapper();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

//    givenInvalidUserDetails_whenRegisterUserIsCalled_thenBadRequestIsThrown
    @Test
    public void testRegister() throws Exception {
        StudentDTO studentDTO = new StudentDTO();
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals( "The name, age or username cannot be null", response.getContentAsString());

        studentDTO = new StudentDTO( "firstUsername","name", 21);
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
        StudentDTO registeredStudent = objectMapper.readValue(response.getContentAsString(), StudentDTO.class);
        assertThat(studentDTO).isEqualToComparingFieldByField(registeredStudent);

        studentDTO = new StudentDTO( "firstUsername","nameWithSameUsername", 21);
        response = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn().getResponse();
        assertEquals( "This username has already been taken, kindly try another username", response.getContentAsString());

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
