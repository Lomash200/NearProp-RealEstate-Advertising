package com.nearprop.service;

import com.nearprop.dto.CreatePropertyDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.entity.*;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.impl.PropertyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private User owner;
    private Property property;
    private CreatePropertyDto createPropertyDto;

    @BeforeEach
    public void setup() {
        // Setup test user
        owner = new User();
        owner.setId(1L);
        owner.setName("Test User");
        owner.setEmail("test@example.com");
        owner.setMobileNumber("1234567890");
        owner.setRoles(Set.of(Role.SELLER));

        // Setup test property
        property = Property.builder()
                .id(1L)
                .title("Test Property")
                .description("Test Description")
                .type(PropertyType.APARTMENT)
                .price(new BigDecimal("100000.00"))
                .area(1000.0)
                .address("123 Test Street")
                .districtName("Test District")
                .city("Test City")
                .state("Test State")
                .pincode("123456")
                .bedrooms(2)
                .bathrooms(2)
                .status(PropertyStatus.AVAILABLE)
                .owner(owner)
                .featured(false)
                .approved(true)
                .build();

        // Setup create property dto
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
    }

    @Test
    public void testCreateProperty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        PropertyDto result = propertyService.createProperty(createPropertyDto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Property");
        assertThat(result.getType()).isEqualTo(PropertyType.APARTMENT);
        assertThat(result.getOwner().getId()).isEqualTo(1L);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    public void testGetProperty() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        PropertyDto result = propertyService.getProperty(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Property");
    }

    @Test
    public void testGetProperty_NotFound() {
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.getProperty(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property not found with id: 99");
    }

    @Test
    public void testGetAllProperties() {
        List<Property> properties = Collections.singletonList(property);
        Page<Property> propertyPage = new PageImpl<>(properties);

        when(propertyRepository.findByApprovedTrue(any(Pageable.class))).thenReturn(propertyPage);

        Page<PropertyDto> result = propertyService.getAllProperties(PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Property");
    }

    @Test
    public void testUpdateProperty() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        CreatePropertyDto updateDto = createPropertyDto;
        updateDto.setTitle("Updated Title");
        
        PropertyDto result = propertyService.updateProperty(1L, updateDto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    public void testUpdateProperty_Unauthorized() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        assertThatThrownBy(() -> propertyService.updateProperty(1L, createPropertyDto, 2L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("You are not authorized to update this property");
    }

    @Test
    public void testDeleteProperty() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        
        propertyService.deleteProperty(1L, 1L);
        
        verify(propertyRepository, times(1)).delete(any(Property.class));
    }

    @Test
    public void testDeleteProperty_Unauthorized() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        assertThatThrownBy(() -> propertyService.deleteProperty(1L, 2L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("You are not authorized to delete this property");
    }

    @Test
    public void testApproveProperty() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        PropertyDto result = propertyService.approveProperty(1L);

        assertThat(result).isNotNull();
        assertThat(result.getApproved()).isTrue();
        assertThat(result.getStatus()).isEqualTo(PropertyStatus.AVAILABLE);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }
} 