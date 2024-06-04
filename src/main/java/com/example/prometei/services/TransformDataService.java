package com.example.prometei.services;

import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.dto.PurchaseDtos.CreatePurchaseDto;
import com.example.prometei.dto.PurchaseDtos.PurchaseDto;
import com.example.prometei.dto.TicketDtos.TicketDto;
import com.example.prometei.dto.UserDtos.EditUserDto;
import com.example.prometei.dto.UserDtos.PassengerDto;
import com.example.prometei.dto.UserDtos.UserDto;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.UnauthUser;
import com.example.prometei.models.User;
import com.example.prometei.models.enums.TicketType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;
import static com.example.prometei.utils.CipherUtil.encryptId;

@Service
public class TransformDataService {
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
                        .map(AdditionalFavorDto::new)
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

    public Ticket transformToTicket(TicketDto ticketDto) {
        return Ticket.builder()
                .id(decryptId(ticketDto.getId()))
                .seatNumber(ticketDto.getSeatNumber())
                .ticketType(ticketDto.getTicketType())
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
