package com.ms.utils;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ms.dto.EmailDetails;

@Component
public class EmailUtil {

	@Autowired
	private JavaMailSender sender;
	
	public boolean sendMail(EmailDetails details) {
		boolean isMailSent = false;
		try {
			MimeMessage mimeMessage = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

			helper.setTo(details.getRecipient());
			helper.setSubject(details.getSubject());
			helper.setText(details.getMsgBody(),true);
			// Sending the mail
			sender.send(mimeMessage);
			return true;
		}

		// Catch block to handle the exceptions
		catch (Exception e) {
			e.printStackTrace();
		}
		return isMailSent;
	}
}
