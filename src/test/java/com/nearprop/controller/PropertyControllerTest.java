package com.nearprop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearprop.config.TestSecurityConfig;
import com.nearprop.dto.CreatePropertyDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import com.nearprop.entity.User;
import com.nearprop.security.JwtAuthenticationFilter;
import com.nearprop.security.JwtUtil;
import com.nearprop.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
@Import(TestSecurityConfig.class)
public class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PropertyService propertyService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private PropertyDto propertyDto;
    private CreatePropertyDto createPropertyDto;
    private User mockUser;
    private String username = "test@example.com";

    @BeforeEach
    public void setup() {
        // Setup test data
        UserSummaryDto ownerDto = UserSummaryDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .phone("1234567890")
                .roles(Set.of("SELLER"))
                .build();

        propertyDto = PropertyDto.builder()
                .id(1L)
                .title("Test Property")
                .description("Test Description")
                .type(PropertyType.APARTMENT)
                .price(new BigDecimal("100000.00"))
                .area(1000.0)
                .address("123 Test Street")
                .districtId(1L)
                .districtName("Test District")
                .city("Test City")
                .state("Test State")
                .pincode("123456")
                .bedrooms(2)
                .bathrooms(2)
                .status(PropertyStatus.AVAILABLE)
                .amenities(Set.of())
                .imageUrls(Collections.emptyList())
                .owner(ownerDto)
                .featured(false)
                .approved(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createPropertyDto = CreatePropertyDto.builder()
                .title("Test Property")
                .description("Test Description")
                .type(PropertyType.APARTMENT)
                .price(new BigDecimal("100000.00"))
                .area(1000.0)
                .address("123 Test Street")
                .districtId(1L)
                .districtName("Test District")
                .city("Test City")
                .state("Test State")
                .pincode("123456")
                .bedrooms(2)
                .bathrooms(2)
                .status(PropertyStatus.AVAILABLE)
                .build();
        
        // Create mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        
        // Configure default mock behavior
        when(propertyService.getProperty(anyLong())).thenReturn(propertyDto);
        when(propertyService.getAllProperties(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(propertyDto)));
        when(propertyService.createProperty(any(CreatePropertyDto.class), anyLong()))
                .thenReturn(propertyDto);
        when(propertyService.updateProperty(anyLong(), any(CreatePropertyDto.class), anyLong()))
                .thenReturn(propertyDto);
        doNothing().when(propertyService).deleteProperty(anyLong(), anyLong());
        when(propertyService.approveProperty(anyLong())).thenReturn(propertyDto);
        when(propertyService.getFeaturedProperties(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(propertyDto)));
        when(propertyService.searchProperties(
                any(PropertyType.class), any(PropertyStatus.class), anyString(),
                any(BigDecimal.class), any(BigDecimal.class), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(propertyDto)));
    }

    @Test
    public void testGetProperty() throws Exception {
        mockMvc.perform(get("/api/properties/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Property"))
                .andExpect(jsonPath("$.type").value("APARTMENT"));
    }

    @Test
    public void testGetAllProperties() throws Exception {
        mockMvc.perform(get("/api/properties")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Property"));
    }

    @Test
    @WithMockUser(roles = "SELLER")
    public void testCreateProperty() throws Exception {
        mockMvc.perform(post("/api/properties")
                .with(SecurityMockMvcRequestPostProcessors.user(username).roles("SELLER"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPropertyDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Property"));
    }

    @Test
    @WithMockUser(roles = "SELLER")
    public void testUpdateProperty() throws Exception {
        mockMvc.perform(put("/api/properties/1")
                .with(SecurityMockMvcRequestPostProcessors.user(username).roles("SELLER"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPropertyDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Property"));
    }

    @Test
    @WithMockUser(roles = "SELLER")
    public void testDeleteProperty() throws Exception {
        mockMvc.perform(delete("/api/properties/1")
                .with(SecurityMockMvcRequestPostProcessors.user(username).roles("SELLER")))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testApproveProperty() throws Exception {
        mockMvc.perform(put("/api/properties/1/approve")
                .with(SecurityMockMvcRequestPostProcessors.user(username).roles("ADMIN"))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Property"));
    }

    @Test
    public void testGetFeaturedProperties() throws Exception {
        mockMvc.perform(get("/api/properties/featured")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Property"));
    }

    @Test
    public void testSearchProperties() throws Exception {
        mockMvc.perform(get("/api/properties/search")
                .param("type", "APARTMENT")
                .param("minBedrooms", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Property"));
    }
} 