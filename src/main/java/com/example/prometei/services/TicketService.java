package com.example.prometei.services;

import com.example.prometei.models.Ticket;
import com.example.prometei.repositories.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService implements BasicService<Ticket> {

    TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository=ticketRepository;
    }

    @Override
    public void add(Ticket entity) {
        if (entity != null) {
            ticketRepository.save(entity);
        }
    }

    @Override
    public void delete(Ticket entity) {

    }

    @Override
    public List<Ticket> getAll() {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void edit(Ticket entity) {

    }

    @Override
    public Ticket getById(Long id) {
        return null;
    }
}
