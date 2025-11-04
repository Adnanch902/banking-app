//package com.example.bankingapp.service;
//
//import com.example.bankingapp.dto.CustomerRequest;
//import com.example.bankingapp.entity.Customer;
//import com.example.bankingapp.exception.ResourceNotFoundException;
//import com.example.bankingapp.repository.CustomerRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class CustomerServiceTest {
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//
//    @InjectMocks
//    private CustomerService customerService;
//
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//    @Test
//    void testCreateCustomer() {
//        Customer c = new Customer();
//        c.setId(1L);
//        c.setName("John");
//
//
//        when(customerRepository.save(any())).thenReturn(c);
//
//
//        Customer created = customerService.createCustomer(c);
//        assertNotNull(created);
//        assertEquals("John", created.getName());
//    }
//
//
//    @Test
//    void testGetCustomerById() {
//        Customer c = new Customer();
//        c.setId(1L);
//        c.setName("John");
//
//
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
//
//
//        Customer found = customerService.getCustomer(1L);
//        assertEquals("John", found.getName());
//    }
//
//
//    @Test
//    void testGetCustomerNotFound() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(RuntimeException.class, () -> customerService.getCustomer(1L));
//    }
//}
//
//    @Test
//    void inquireCustomer_notFound() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> customerService.inquireCustomer(1L));
//    }
//}
