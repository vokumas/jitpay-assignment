package io.vokumas.jitpayassignment.unit;

import io.vokumas.jitpayassignment.back.model.dto.*;
import io.vokumas.jitpayassignment.back.service.declaration.UserService;
import io.vokumas.jitpayassignment.web.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class JitpayWebUnitTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void upsertUser_shouldReturnOkForValidRequest() throws Exception {
        var userId = UUID.randomUUID();
        UserDto mocked = new UserDto(userId, "email@email.com", "firstName", "secondName");
        when(userService.createOrUpdateUser(any(UserDto.class))).thenReturn(mocked);

        var userJson = String.format("""
                {
                	"userId": "%s",
                	"firstName": "firstName",
                	"email": "email@email.com",
                	"secondName": "secondName"
                }""", userId);

        mvc
                .perform(post("/api/v2/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("email@email.com"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.secondName").value("secondName"));
    }

    @Test
    void upsertUser_shouldFailWhenMissingId() throws Exception {
        var userJson = """
                {
                	"firstName": "firstName",
                	"email": "email@email.com",
                	"secondName": "secondName"
                }""";

        mvc
                .perform(post("/api/v2/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.validationErrors.userId").value("userId cannot be empty"));
    }

    @Test
    void upsertUser_shouldFailWhenMissingEmail() throws Exception {
        var userId = UUID.randomUUID();

        var userJson = String.format("""
                {
                	"userId": "%s",
                	"firstName": "firstName",
                	"secondName": "secondName"
                }""", userId);

        mvc
                .perform(post("/api/v2/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.validationErrors.email").value("email cannot be empty"));
    }

    @Test
    void upsertUser_shouldFailWhenMissingFirstName() throws Exception {
        var userId = UUID.randomUUID();

        var userJson = String.format("""
                {
                	"userId": "%s",
                	"email": "email@email.com",
                	"secondName": "secondName"
                }""", userId);

        mvc
                .perform(post("/api/v2/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.validationErrors.firstName").value("firstName cannot be empty"));
    }

    @Test
    void upsertUser_shouldFailWhenMissingSecondName() throws Exception {
        var userId = UUID.randomUUID();

        var userJson = String.format("""
                {
                	"userId": "%s",
                	"email": "email@email.com",
                	"firstName": "firstName"
                }""", userId);

        mvc
                .perform(post("/api/v2/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.validationErrors.secondName").value("secondName cannot be empty"));
    }

    @Test
    void upsertUser_shouldFailWhenMissingBody() throws Exception {
        mvc
                .perform(post("/api/v2/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLatestLocation_shouldReturnOkForValidRequest() throws Exception {
        var userId = UUID.randomUUID();
        UserSingleLocationDto mocked = new UserSingleLocationDto(userId, "email@email.com",
                "firstName", "secondName", new LocationDto(82.15485, 52.16456));
        when(userService.getUserLatestLocation(any(UUID.class))).thenReturn(mocked);

        mvc
                .perform(get("/api/v2/user/location/latest?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location.latitude").value(82.15485));
    }

    @Test
    void getLatestLocation_shouldFailWhenMissingUserIdParameter() throws Exception {
        mvc
                .perform(get("/api/v2/user/location/latest?"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("Bad request. There are some parameters missing"));
    }

    @Test
    void getLocationsInRange_shouldReturnOkForValidRequest() throws Exception {
        var userId = UUID.randomUUID();
        UserDtoTimedLocation mocked = new UserDtoTimedLocation(userId, "email@email.com", "firstName", "secondName",
                Arrays.asList(
                        new LocationTimedDto(
                                LocalDateTime.of(2022, 11, 25, 10, 10, 10, 10),
                                new LocationDto(82.15485, 52.16456)),
                        new LocationTimedDto(
                                LocalDateTime.of(2022, 11, 26, 10, 10, 10, 10),
                                new LocationDto(85.15443, 22.12366))));
        when(userService.getUserLocationsRange(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mocked);
        var from = "2022-11-23T08:11:10.500";
        var to = "2022-11-26T08:11:10.500";

        mvc
                .perform(get("/api/v2/user/location/range?userId=" + userId + "&from=" + from + "&to=" + to))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.locations.length()").value(2));
    }

    @Test
    void getLocationsInRange_shouldFailWhenMissingParams() throws Exception {
        var userId = UUID.randomUUID();
        var from = "2022-11-23T08:11:10.500";

        mvc
                .perform(get("/api/v2/user/location/range?userId=" + userId + "&from=" + from))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("Bad request. There are some parameters missing"));
    }

}
