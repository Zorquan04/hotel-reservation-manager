package org.example.hotelreservation.integrationTest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HotelReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static PostgreSQLContainer<?> postgresContainer;
    private Long userId;
    private Long roomId;
    private Long reservationId;

    @BeforeAll
    public static void setUp() {
        postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                            .withDatabaseName("reservation_manager")
                            .withUsername("postgres")
                            .withPassword("kapi2000");
        postgresContainer.start();
    }

    @AfterAll
    void tearDown() throws Exception {
        if (reservationId != null) { mockMvc.perform(delete("/api/reservations/" + reservationId)
                            .with(user("admin").roles("ADMIN")))
                            .andExpect(MockMvcResultMatchers.status().isOk());
        }

        if (roomId != null) { mockMvc.perform(delete("/api/rooms/" + roomId)
                            .with(user("admin").roles("ADMIN")))
                            .andExpect(MockMvcResultMatchers.status().isOk()); }

        if (userId != null) { mockMvc.perform(delete("/api/users/" + userId)
                            .with(user("admin").roles("ADMIN")))
                            .andExpect(MockMvcResultMatchers.status().isOk());
        }
        if (postgresContainer != null) { postgresContainer.stop(); }
    }

    @BeforeEach
    public void createUserAndRoom() throws Exception {
        String userJson = "{\"username\":\"testUser\",\"password\":\"pass\",\"role\":\"ADMIN\"}";
        String userResp = mockMvc.perform(post("/api/users/register")
                            .with(user("admin").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.username").value("testUser"))
                            .andReturn().getResponse().getContentAsString();
        userId = Long.valueOf(JsonPath.read(userResp, "$.id").toString());

        String roomJson = "{\"number\":\"101\",\"standard\":\"Deluxe\",\"price\":100}";
        String roomResp = mockMvc.perform(post("/api/rooms")
                            .with(user("admin").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(roomJson))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.number").value("101"))
                            .andReturn().getResponse().getContentAsString();
        roomId = Long.valueOf(JsonPath.read(roomResp, "$.id").toString());
    }

    @AfterEach
    public void cleanupEach() throws Exception {
        if (reservationId != null) {
            mockMvc.perform(delete("/api/reservations/" + reservationId)
                            .with(user("admin").roles("ADMIN")))
                            .andExpect(status().isOk());
            reservationId = null;
        }
        if (roomId != null) {
            mockMvc.perform(delete("/api/rooms/" + roomId)
                            .with(user("admin").roles("ADMIN")))
                            .andExpect(status().isOk());
            roomId = null;
        }
        if (userId != null) {
            mockMvc.perform(delete("/api/users/" + userId)
                            .with(user("admin").roles("ADMIN")))
                            .andExpect(status().isOk());
            userId = null;
        }
    }

    @Test
    @DisplayName("Full flow: reservation assignment")
    public void testFullReservationFlow() throws Exception {
        String reservationJson = String.format("{\"userId\":%d,\"roomId\":%d,\"date\":\"2025-06-01\"}", userId, roomId);
        mockMvc.perform(post("/api/reservations")
                            .with(user("admin").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(reservationJson))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.userId").value(userId))
                            .andExpect(jsonPath("$.roomId").value(roomId));
    }
}