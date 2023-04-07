package ru.practicum.shareit.booking.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.SentBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.exceptions.InappropriateUser;
import ru.practicum.shareit.exceptions.UnsupportedStatus;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = BookingServiceImpl.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    private static final long BOOKING_ID = 1L;
    private static final long USER_ID = 2L;

    @Test
    public void testGetBookingReturnsBookingDtoWhenUserIsAuthorized() {
        User booker = new User();
        booker.setId(USER_ID);
        User owner = new User();
        owner.setId(3L);
        Item item = new Item();
        item.setOwner(3L);
        SentBookingDto booking = new SentBookingDto();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setId(BOOKING_ID);
        when(bookingService.getBooking(BOOKING_ID, USER_ID)).thenReturn(booking);

        SentBookingDto result = bookingService.getBooking(BOOKING_ID, USER_ID);

        assertNotNull(result);
        assertEquals(BOOKING_ID, result.getId());
    }

    @Test()
    public void testGetBookingThrowsEntityNotFoundWhenBookingIsNotFound() {
        when(bookingService.getBooking(BOOKING_ID, USER_ID)).thenThrow(new EntityNotFound("Booking not found"));

        assertThrows(EntityNotFound.class, () -> bookingService.getBooking(BOOKING_ID, USER_ID));
    }

    @Test
    public void testGetBookingThrowsInappropriateUserWhenUserIsNotAuthorized() {
        User booker = new User();
        booker.setId(3L);
        User owner = new User();
        owner.setId(4L);
        Item item = new Item();
        item.setOwner(1L);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setId(BOOKING_ID);
        when(bookingService.getBooking(BOOKING_ID, USER_ID)).thenThrow(new InappropriateUser("Inappropriate user"));

        assertThrows(InappropriateUser.class, () -> bookingService.getBooking(BOOKING_ID, USER_ID));

    }

    @Test
    public void testGetAllUserBookingsUnknownState() {
        long userId = 1L;
        String state = "INVALID";
        String userType = "BOOKER";
        Integer from = 0;
        Integer size = 10;

        when(bookingService.getAllUserBookings(userId, state, userType, from, size))
                .thenThrow(new UnsupportedStatus("Unsupported Status"));

        assertThrows(UnsupportedStatus.class, () -> bookingService.getAllUserBookings(userId, state, userType, from, size));
    }

    @Test
    public void testCreateBooking_BadRequest() {
        ReceivedBookingDto bookingDto = new ReceivedBookingDto();
        Item item = new Item();
        item.setId(1L);
        item.setOwner(1L);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().minusHours(5));
        long userId = 2L;
        when(bookingService.createBooking(bookingDto, userId)).thenThrow(new BadRequest("Not valid fields"));

        final BadRequest exception = assertThrows(BadRequest.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertEquals(exception.getMessage(), "Not valid fields");
    }

    @Test
    public void testCreateBooking_InappropriateUserRequest() {
        ReceivedBookingDto bookingDto = new ReceivedBookingDto();
        Item item = new Item();
        item.setId(1L);
        item.setOwner(1L);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(5));
        long userId = -1L;

        when(itemService.getItemById(bookingDto.getItemId())).thenReturn(item);
        when(bookingService.createBooking(bookingDto, userId)).thenThrow(new InappropriateUser("Inappropriate User"));

        final InappropriateUser exception = assertThrows(InappropriateUser.class,
                () -> bookingService.createBooking(bookingDto, userId));

        assertEquals(exception.getMessage(), "Inappropriate User");
    }

    @Test
    public void testCreateBooking_ValidBookingRequest() {
        ReceivedBookingDto receivedBookingDtoTest = new ReceivedBookingDto();
        SentBookingDto sentBookingDto = new SentBookingDto();
        Booking booking = new Booking();
        when(bookingService.createBooking(receivedBookingDtoTest, 1)).thenAnswer(invocationOnMock -> {
            bookingRepository.save(booking);
            return sentBookingDto;
        });

        SentBookingDto actualDto = bookingService.createBooking(receivedBookingDtoTest, 1);

        verify(bookingRepository).save(booking);
        assertEquals(sentBookingDto, actualDto);
    }
}