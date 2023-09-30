package com.ria.acmetesting.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ria.acmetesting.config.DBContainers;
import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.exceptions.RequiredQuestionFieldNullException;
import com.ria.acmetesting.exceptionhandling.exceptions.RequiredStudentFieldNullException;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.services.ACMETestServiceImpl;
import org.junit.ClassRule;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Autowired
    QuestionRepository questionRepository;

    StudentDTO studentDTO;
    ObjectMapper objectMapper;
    @BeforeEach
    public void initializeStudentAndQuestion(){
        studentDTO = new StudentDTO("studentName", 22);
        objectMapper = new ObjectMapper();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
//        Question debugQuestions = questionRepository.findById(1).get();
    }

    @Test
    public void testRegister() throws Exception {
        MockHttpServletResponse registrationResponse = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(studentDTO)))
                .andReturn().getResponse();

        StudentDTO registeredStudent = objectMapper.readValue(registrationResponse.getContentAsString(), StudentDTO.class);
        assertEquals(201, registrationResponse.getStatus());
        assertThat(studentDTO).isEqualToComparingFieldByField(registeredStudent);

        StudentDTO nullFieldStudent = new StudentDTO(null, 21);
        MockHttpServletResponse nullStudentResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(nullFieldStudent)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        assertEquals( "The Name or Age cannot be null", nullStudentResult.getContentAsString());
//        mockMvc.perform(MockMvcRequestBuilders
//                .post("/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(nullFieldStudent)))
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredStudentFieldNullException))
//                .andExpect(result -> assertTrue((result.)));
    }
//
//    @Test
//    public void testGetRemainingSubjects() throws Exception {
//        List<String> sampleSubject = new ArrayList<>();
//        sampleSubject.add("Maths");
//        sampleSubject.add("English");
//        sampleSubject.add("Science");
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/subject?studentId={id}", 1))
//                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(sampleSubject)))
//                .andReturn().getResponse();
//        List<String> receivedSubjectList = objectMapper.readValue(response.getContentAsString(),
//                new TypeReference<List<String>>() {});
//        assertEquals(200, response.getStatus());
//        assertEquals(sampleSubject, receivedSubjectList);
//
//    }
//
//    @Test
//    public void testMarkSubject() throws Exception {
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/subject?studentId={id}&subject={subject}",1, "Maths"))
//                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(studentDTO)))
//                .andReturn().getResponse();
//        StudentDTO responseStudent = objectMapper.readValue(response.getContentAsString(), studentDTO.getClass());
//        assertEquals(200, response.getStatus());
//        assertThat(studentDTO).isEqualToComparingFieldByField(responseStudent);
//    }
//
//    @Test
//    public void testStartTest() throws Exception {
//        QuestionDTO questionDTO = new QuestionDTO("What is 2+5", "4, 2, 7, 8");
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/subject/{subject}/test?studentId={id}", "Maths",1))
//                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(questionDTO)))
//                .andReturn().getResponse();
//        QuestionDTO responseQuestion = objectMapper.readValue(response.getContentAsString(), QuestionDTO.class);
//        assertEquals(200, response.getStatus());
//        assertThat(questionDTO).isEqualToComparingFieldByField(responseQuestion);
//
//    }
//
//    @Test
//    public void testGetNextQuestionTest() throws Exception {
//        QuestionDTO questionDTO = new QuestionDTO("What is 2*5", "4, 10, 7, 8");
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(
//                        "/subject/{subject}/test?studentId={id}&selectedOption={selectedOption}", "Maths",1 , "b"))
//                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(questionDTO)))
//                .andReturn().getResponse();
//        QuestionDTO responseQuestion = objectMapper.readValue(response.getContentAsString(), QuestionDTO.class);
//        assertEquals(200, response.getStatus());
//        assertThat(questionDTO).isEqualToComparingFieldByField(responseQuestion);
//    }
//
//    @Test
//    public void testGetScore() throws Exception {
//        int totalScore = 10;
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/score?studentId={id}", 1))
//                .andReturn().getResponse();
//        Integer responseScore = objectMapper.readValue(response.getContentAsString(), Integer.class);
//        assertEquals(200, response.getStatus());
//        assertEquals(totalScore, responseScore.intValue());
//
//    }


}
