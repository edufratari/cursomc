package com.eduardofratari.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.eduardofratari.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido pedido);

	void sendEmail(SimpleMailMessage msg);
}
