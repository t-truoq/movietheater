package org.example.movie.mapper;

import org.example.movie.dto.response.BookingConfirmationResponse;
import org.example.movie.dto.response.TicketConfirmationResponse;
import org.example.movie.dto.response.TicketInfoResponse;
import org.example.movie.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "invoice.invoiceId", target = "invoiceId")
    @Mapping(source = "invoice.movieName", target = "movieName")
    @Mapping(source = "invoice.scheduleShowTime", target = "scheduleShowDate", qualifiedByName = "toLocalDate")
    @Mapping(source = "invoice.scheduleShowTime", target = "scheduleShowTime", qualifiedByName = "toStringTime")
    @Mapping(source = "invoice.seat", target = "seatNumbers", qualifiedByName = "splitSeatString")
    @Mapping(source = "invoice.totalMoney", target = "totalPrice")
    TicketConfirmationResponse toConfirmationResponse(Invoice invoice);

    @Mapping(source = "invoice.invoiceId", target = "bookingId")
    @Mapping(source = "invoice.movieName", target = "movieName")
    @Mapping(source = "invoice.scheduleShowTime", target = "date")
    @Mapping(source = "invoice.scheduleShowTime", target = "time")
    @Mapping(source = "invoice.seat", target = "seat", qualifiedByName = "splitSeatString")
    @Mapping(source = "invoice.totalMoney", target = "total")
    @Mapping(target = "cinemaRoomName", ignore = true)
    @Mapping(source = "invoice.totalMoney", target = "price")
    @Mapping(source = "invoice.account.member.memberId", target = "memberId")
    @Mapping(source = "invoice.account.fullName", target = "fullName")
    @Mapping(source = "invoice.account.identityCard", target = "identityCard")
    @Mapping(source = "invoice.account.phoneNumber", target = "phoneNumber")
    @Mapping(source = "invoice.useScore", target = "scoreForTicketConverting")
    TicketInfoResponse toInfoResponse(Invoice invoice);

    @Mapping(source = "invoice.invoiceId", target = "bookingId")
    @Mapping(source = "invoice.movieName", target = "movieName")
    @Mapping(source = "invoice.scheduleShowTime", target = "date")
    @Mapping(source = "invoice.scheduleShowTime", target = "time")
    @Mapping(source = "invoice.seat", target = "seat", qualifiedByName = "splitSeatString")
    @Mapping(source = "invoice.totalMoney", target = "total")
    @Mapping(source = "invoice.totalMoney", target = "price")
    @Mapping(source = "invoice.account.member.memberId", target = "memberId")
    @Mapping(source = "invoice.account.fullName", target = "fullName")
    @Mapping(source = "invoice.account.identityCard", target = "identityCard")
    @Mapping(source = "invoice.account.phoneNumber", target = "phoneNumber")
    @Mapping(source = "invoice.useScore", target = "scoreUsed")
    @Mapping(source = "invoice.account.member.score", target = "memberScore")
    BookingConfirmationResponse toBookingConfirmationResponse(Invoice invoice);

    @Named("splitSeatString")
    default List<String> splitSeatString(String seat) {
        return seat != null && !seat.isEmpty() ? Arrays.asList(seat.split(",")) : List.of();
    }

    @Named("toLocalDate")
    default LocalDate toLocalDate(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toLocalDate() : null;
    }

    @Named("toStringTime")
    default String toStringTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toString() : null;
    }

    @Named("toConvertToTicket")
    default String toConvertToTicket(Integer status) {
        return status != null && status == 1 ? "Converted" : "Not Converted";
    }

    @Named("toConvertedToTicketBoolean")
    default Boolean toConvertedToTicketBoolean(Integer status) {
        return status != null && status == 1;
    }
}