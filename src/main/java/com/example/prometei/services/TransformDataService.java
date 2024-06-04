package com.example.prometei.services;

import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.dto.FavorDto.CreateFlightFavorDto;
import com.example.prometei.dto.FavorDto.FlightFavorDto;
import com.example.prometei.dto.FlightDtos.CreateFlightDto;
import com.example.prometei.dto.FlightDtos.FlightDto;
import com.example.prometei.dto.FlightDtos.SearchDto;
import com.example.prometei.dto.PurchaseDtos.CreatePurchaseDto;
import com.example.prometei.dto.PurchaseDtos.PurchaseDto;
import com.example.prometei.dto.TicketDtos.TicketDto;
import com.example.prometei.dto.UserDtos.EditUserDto;
import com.example.prometei.dto.UserDtos.PassengerDto;
import com.example.prometei.dto.UserDtos.UserDto;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.enums.TicketType;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.prometei.utils.CipherUtil.decryptId;
import static com.example.prometei.utils.CipherUtil.encryptId;

@Service
public class TransformDataService {
    public SearchDto transformToSearchDto(Pair<List<Flight>, List<Flight>> pairFlight) {
        return SearchDto.builder()
                .to(pairFlight.a.stream().map(this::transformToFlightDto).toList())
                .back(pairFlight.b == null ? null : pairFlight.b.stream().map(this::transformToFlightDto).toList())
                .build();
    }

    private String DateParser(LocalDate localDate) {
        String dayOfMonth = String.valueOf(localDate.getDayOfMonth());
        String month = localDate.getMonth().getDisplayName(TextStyle.SHORT, new Locale("ru"));
        String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("ru"));

        return String.format("%s %s, %s", dayOfMonth, month, dayOfWeek);
    }

    private String TimeParser(LocalTime localTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return localTime.format(formatter);
    }

    private Integer FlightTimeParser(Double duration) {
        return (int) Math.round(duration * 60);
    }

    public AdditionalFavorDto transformToAdditionalFavorDto(AdditionalFavor additionalFavor) {
        return AdditionalFavorDto.builder()
                .id(encryptId(additionalFavor.getId()))
                .name(additionalFavor.getFlightFavor().getName())
                .cost(additionalFavor.getFlightFavor().getCost())
                .seatNum(additionalFavor.getTicket().getSeatNumber())
                .ticketId(encryptId(additionalFavor.getTicket().getId()))
                .build();
    }

    public FlightFavor transformToFlightFavor(CreateFlightFavorDto createFlightFavorDto) {
        return FlightFavor.builder()
                .name(createFlightFavorDto.getName())
                .cost(createFlightFavorDto.getCost())
                .build();
    }

    public FlightFavorDto transformToFlightFavorDto(FlightFavor flightFavor) {
        return FlightFavorDto.builder()
                .id(encryptId(flightFavor.getId()))
                .name(flightFavor.getName())
                .cost(flightFavor.getCost())
                .build();
    }

    public FlightFavor transformToFlightFavor(FlightFavorDto flightFavorDto) {
        return FlightFavor.builder()
                .id(decryptId(flightFavorDto.getId()))
                .name(flightFavorDto.getName())
                .cost(flightFavorDto.getCost())
                .build();
    }

    public Flight transformToFlight(CreateFlightDto createFlightDto) {
        LocalDateTime departure = createFlightDto.getDepartureDate().atTime(createFlightDto.getDepartureTime());
        return Flight.builder()
                .airplaneModel(createFlightDto.getAirplaneModel())
                .departureTime(OffsetDateTime.of(departure, ZoneOffset.ofHours(0)).toLocalTime())
                .departureDate(OffsetDateTime.of(departure, ZoneOffset.ofHours(0)).toLocalDate())
                .departurePoint(createFlightDto.getDeparturePoint())
                .economyCost(createFlightDto.getEconomyCost())
                .businessCost(createFlightDto.getBusinessCost())
                .airplaneNumber(createFlightDto.getAirplaneNumber())
                .destinationPoint(createFlightDto.getDestinationPoint())
                .economSeats(createFlightDto.getAirplaneModel() == AirplaneModel.AIRBUS320 ? 120 : 265)
                .businessSeats(createFlightDto.getAirplaneModel() == AirplaneModel.AIRBUS320 ? 20 : 36)
                .build();
    }

    public FlightDto transformToFlightDto(Flight flight) {
        return FlightDto.builder()
                .id(encryptId(flight.getId()))
                .departurePoint(flight.getDeparturePoint())
                .destinationPoint(flight.getDestinationPoint())
                .departureDate(DateParser(flight.getDepartureDate()))
                .destinationDate(DateParser(flight.getDestinationDate()))
                .destinationTime(TimeParser(flight.getDestinationTime()))
                .departureTime(TimeParser(flight.getDepartureTime()))
                .flightTime(FlightTimeParser(flight.getFlightTime()))
                .economyCost(flight.getEconomyCost())
                .businessCost(flight.getBusinessCost())
                .model(flight.getAirplaneModel())
                .build();
    }

    public Purchase transformToPurchase(CreatePurchaseDto createPurchaseDto) {
        return Purchase.builder()
                .paymentMethod(createPurchaseDto.getPaymentMethod())
                .build();
    }

    public PurchaseDto transformToPurchaseDto(Purchase purchase) {
        return PurchaseDto.builder()
                .id(encryptId(purchase.getId()))
                .totalCost(purchase.getTotalCost())
                .paymentMethod(purchase.getPaymentMethod())
                .createDate(purchase.getCreateDate())
                .userEmail(purchase.getUser() != null ?
                        purchase.getUser().getEmail() :
                        purchase.getUnauthUser().getEmail())
                .build();
    }

    public TicketDto transformToTicketDto(Ticket ticket) {
        return TicketDto.builder()
                .id(encryptId(ticket.getId()))
                .costFavors(ticket.getAdditionalFavors()
                        .stream()
                        .map(this::transformToAdditionalFavorDto)
                        .mapToDouble(AdditionalFavorDto::getCost)
                        .sum())
                .seatNumber(ticket.getSeatNumber())
                .ticketType(ticket.getTicketType())
                .flightId(encryptId(ticket.getFlight().getId()))
                .costFlight(ticket.getTicketType() == TicketType.BUSINESS ?
                        ticket.getFlight().getBusinessCost() :
                        ticket.getFlight().getEconomyCost())
                .build();
    }

    public UserDto transformToUserDto(User user) {
        return UserDto.builder()
                .id(encryptId(user.getId()))
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .internationalPassportNum(user.getInternationalPassportNum())
                .internationalPassportDate(user.getInternationalPassportDate())
                .passport(user.getPassport())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .residenceCity(user.getResidenceCity())
                .role(user.getRole())
                .build();
    }

    public User transformToUser(EditUserDto editUserDto) {
            return User.builder()
                    .birthDate(editUserDto.getBirthDate())
                    .firstName(editUserDto.getFirstName())
                    .lastName(editUserDto.getLastName())
                    .gender(editUserDto.getGender())
                    .internationalPassportNum(editUserDto.getInternationalPassportNum())
                    .internationalPassportDate(editUserDto.getInternationalPassportDate())
                    .passport(editUserDto.getPassport())
                    .phoneNumber(editUserDto.getPhoneNumber())
                    .residenceCity(editUserDto.getResidenceCity())
                    .build();
    }

    public UnauthUser transformToUnAuthUser(PassengerDto passengerDto) {
        return UnauthUser.builder()
                .email(passengerDto.getEmail())
                .birthDate(passengerDto.getBirthDate())
                .firstName(passengerDto.getFirstName())
                .lastName(passengerDto.getLastName())
                .gender(passengerDto.getGender())
                .internationalPassportNum(passengerDto.getInternationalPassportNum())
                .internationalPassportDate(passengerDto.getInternationalPassportDate())
                .passport(passengerDto.getPassport())
                .phoneNumber(passengerDto.getPhoneNumber())
                .build();
    }

    public User transformToUser(PassengerDto passengerDto) {
        return User.builder()
                .email(passengerDto.getEmail())
                .birthDate(passengerDto.getBirthDate())
                .firstName(passengerDto.getFirstName())
                .lastName(passengerDto.getLastName())
                .gender(passengerDto.getGender())
                .internationalPassportNum(passengerDto.getInternationalPassportNum())
                .internationalPassportDate(passengerDto.getInternationalPassportDate())
                .passport(passengerDto.getPassport())
                .phoneNumber(passengerDto.getPhoneNumber())
                .build();
    }

    public long[] decryptTicketIds(String[] ticketIds) {
        List<Long> ids = new ArrayList<>();

        for (String id : ticketIds) {
            ids.add(decryptId(id));
        }

        long[] result = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = ids.get(i);
        }

        return result;
    }

    public List<UnauthUser> listPassengerDtoToUnAuthUser(List<PassengerDto> passengers) {
        List<UnauthUser> list = new ArrayList<>();

        for (PassengerDto dto : passengers) {
            list.add(transformToUnAuthUser(dto));
        }

        return list;
    }
}
