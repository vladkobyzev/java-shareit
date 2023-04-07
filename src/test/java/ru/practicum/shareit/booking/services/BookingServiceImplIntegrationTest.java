package ru.practicum.shareit.booking.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.SentBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.exceptions.InappropriateUser;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class BookingServiceImplIntegrationTest {


    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void getBooking_withValidBookingIdAndBooker_returnsSentBookingDto() {
        User booker = new User();
        booker.setName("John");
        booker.setEmail("test@gmail.com");
        userRepository.save(booker);

        Booking booking = new Booking();
        booking.setBooker(booker);

        User owner = new User();
        owner.setName("ownercxcvXV");
        owner.setEmail("ownerxcvvc@gmail.com");
        userRepository.save(owner);
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        itemRepository.save(item);

        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        SentBookingDto result = bookingService.getBooking(booking.getId(), booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    public void getBooking_withInvalidBookingId_throwsEntityNotFoundException() {
        long invalidBookingId = 1L;
        long validUserId = 2L;

        assertThrows(EntityNotFound.class, () -> bookingService.getBooking(invalidBookingId, validUserId));
    }

    @Test
    public void getBooking_withInvalidUserId_throwsInappropriateUserException() {
        User booker = new User();
        booker.setName("John");
        booker.setEmail("test@gmail.com");
        userRepository.save(booker);

        Booking booking = new Booking();
        booking.setBooker(booker);

        User owner = new User();
        owner.setName("ownercxcvXV");
        owner.setEmail("ownerxcvvc@gmail.com");
        userRepository.save(owner);
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        itemRepository.save(item);

        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking);
        long invalidUserId = 3000L;

        assertThrows(InappropriateUser.class, () -> bookingService.getBooking(booking.getId(), invalidUserId));
    }

    @Test
    public void testCreateBooking_ValidBooking() {
        User booker = new User();
        booker.setName("John");
        booker.setEmail("test@gmail.com");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("ownercxcvXV");
        owner.setEmail("ownerxcvvc@gmail.com");
        userRepository.save(owner);
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setOwner(owner.getId());
        item.setAvailable(true);
        itemRepository.save(item);

        ReceivedBookingDto bookingDto = new ReceivedBookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(4));

        SentBookingDto result = bookingService.createBooking(bookingDto, booker.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getBooker());
        assertNotNull(result.getBooker().getId());
        assertNotNull(result.getItem());
        assertNotNull(result.getItem().getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
    }

    @Test
    void updateBookingStatus_WithValidRequest_ShouldUpdateBookingStatus() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@example.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemRepository.save(item);

        Booking b1 = new Booking();
        b1.setBooker(user);
        b1.setStart(now.minusHours(2));
        b1.setEnd(now.minusHours(1));
        b1.setStatus(BookingStatus.WAITING);
        b1.setItem(item);
        b1.setBooker(booker);
        bookingRepository.save(b1);

        String newStatus = "true";

        SentBookingDto updatedBooking = bookingService.updateBookingStatus(b1.getId(), newStatus, user.getId());


        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void updateBookingStatus_WithInvalidBookingId_ShouldThrowEntityNotFoundException() {
        long invalidBookingId = 100L;
        String newStatus = BookingStatus.APPROVED.toString().toLowerCase();

        assertThrows(EntityNotFound.class, () -> bookingService.updateBookingStatus(invalidBookingId, newStatus, 1L));
    }

    @Test
    void updateBookingStatus_WithInappropriateUser_ShouldThrowInappropriateUserException() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@example.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemRepository.save(item);

        Booking b1 = new Booking();
        b1.setBooker(user);
        b1.setStart(now.minusHours(2));
        b1.setEnd(now.minusHours(1));
        b1.setStatus(BookingStatus.WAITING);
        b1.setItem(item);
        b1.setBooker(booker);
        bookingRepository.save(b1);

        String newStatus = BookingStatus.APPROVED.toString().toLowerCase();

        assertThrows(InappropriateUser.class, () -> bookingService.updateBookingStatus(b1.getId(), newStatus, 999L));
    }

    @Test
    void getAllUserBookings_WithValidData_ShouldReturnBookingsWithoutPagination() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        User user1 = new User();
        user1.setName("asd");
        user1.setEmail("asd@example.com");
        userRepository.save(user1);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemRepository.save(item);

        Booking b1 = new Booking();
        b1.setBooker(user);
        b1.setStart(now.minusHours(2));
        b1.setEnd(now.minusHours(1));
        b1.setStatus(BookingStatus.WAITING);
        b1.setItem(item);
        bookingRepository.save(b1);

        Booking b2 = new Booking();
        b2.setBooker(user);
        b2.setStart(now.plusHours(1));
        b2.setEnd(now.plusHours(3));
        b2.setStatus(BookingStatus.APPROVED);
        b2.setItem(item);
        bookingRepository.save(b2);

        Booking b3 = new Booking();
        b3.setBooker(user1);
        b3.setStart(now.minusHours(7));
        b3.setEnd(now.minusHours(6));
        b3.setStatus(BookingStatus.APPROVED);
        b3.setItem(item);
        bookingRepository.save(b3);

        List<SentBookingDto> actualBookingsOwner = bookingService.getAllUserBookings(user.getId(), "ALL", "OWNER", null, null);

        assertThat(actualBookingsOwner).isNotNull();
        assertThat(actualBookingsOwner.size()).isEqualTo(3);

        List<SentBookingDto> actualBookingsUser = bookingService.getAllUserBookings(user1.getId(), "ALL", "USER", null, null);

        assertThat(actualBookingsUser).isNotNull();
        assertThat(actualBookingsUser.size()).isEqualTo(1);
    }

    @Test
    void getAllUserBookings_WithValidData_ShouldReturnBookingsWithPagination() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        User user1 = new User();
        user1.setName("asd");
        user1.setEmail("asd@example.com");
        userRepository.save(user1);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemRepository.save(item);

        Booking b1 = new Booking();
        b1.setBooker(user);
        b1.setStart(now.minusHours(2));
        b1.setEnd(now.minusHours(1));
        b1.setStatus(BookingStatus.WAITING);
        b1.setItem(item);
        bookingRepository.save(b1);

        Booking b2 = new Booking();
        b2.setBooker(user);
        b2.setStart(now.plusHours(1));
        b2.setEnd(now.plusHours(3));
        b2.setStatus(BookingStatus.APPROVED);
        b2.setItem(item);
        bookingRepository.save(b2);

        Booking b3 = new Booking();
        b3.setBooker(user);
        b3.setStart(now.minusHours(7));
        b3.setEnd(now.minusHours(6));
        b3.setStatus(BookingStatus.APPROVED);
        b3.setItem(item);
        bookingRepository.save(b3);

        Booking b4 = new Booking();
        b4.setBooker(user1);
        b4.setStart(now.plusHours(8));
        b4.setEnd(now.plusHours(9));
        b4.setStatus(BookingStatus.APPROVED);
        b4.setItem(item);
        bookingRepository.save(b4);

        List<SentBookingDto> actualBookingsOwner = bookingService.getAllUserBookings(user.getId(), "ALL", "OWNER", 1, 2);

        assertThat(actualBookingsOwner).isNotNull();
        assertThat(actualBookingsOwner.size()).isEqualTo(2);
        assertEquals(actualBookingsOwner.get(0).getId(), b1.getId());

        List<SentBookingDto> actualBookingsUser = bookingService.getAllUserBookings(user1.getId(), "ALL", "USER", 0, 1);

        assertThat(actualBookingsUser).isNotNull();
        assertEquals(actualBookingsUser.get(0).getId(), b4.getId());
    }
}
