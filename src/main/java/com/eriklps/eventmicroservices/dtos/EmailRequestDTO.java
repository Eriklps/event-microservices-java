package com.eriklps.eventmicroservices.dtos;

public record EmailRequestDTO(String to, String subject, String body) {
}