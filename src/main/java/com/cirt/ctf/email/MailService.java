package com.cirt.ctf.email;

import com.cirt.ctf.payload.TeamRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


@Service
@Slf4j
public class MailService{

	@Value("${spring.mail.host}")
	private String MAIL_SERVER_HOST;

	@Value("${spring.mail.port}")
	private int MAIL_SERVER_PORT;

	@Value("${spring.mail.username}")
	private String MAIL_SENDER;

	@Value("${spring.mail.password}")
	private String MAIL_PASSWORD;


	private Properties getMailProps(){
		Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.enable", "false");
		props.put("mail.smtp.ssl.trust", "*");

		//mapping DNS zimbra220.bcc.gov.bd -> 114.130.54.220
		props.put("mail.smtp.host", MAIL_SERVER_HOST);
		props.put("mail.smtp.port", MAIL_SERVER_PORT);

		props.put("mail.debug", "true");

		return props;

	}

	@Async
	public void send(MailDTO mailDTO) {

		Properties props = getMailProps();
		
		Session session = Session.getInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(MAIL_SENDER,MAIL_PASSWORD);
				}
		});
		
		try {

			MimeMessage message = new MimeMessage(session);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailDTO.getTo()));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			//message.addRecipient(Message.RecipientType.BCC, new InternetAddress(BCC[i]));

			message.setSubject(mailDTO.getSubject(), "UTF-8");
			message.setFrom(new InternetAddress(MAIL_SENDER, mailDTO.getFrom()));
			message.setContent(mailDTO.getContent(), "text/html; charset=utf-8");

			Transport.send(message);

		} catch (Exception e) {
			log.error(e.toString());
		}

	}

	public void sendRegistrationMail(TeamRegistration team){

		//Team Lead
		MailDTO mailDTO= MailDTO.builder().subject("CTF Account").to(team.getEmail()).build();
		String content= """
       			Dear %s<br/><br/>
    			Your account has created for CIRT Cyber Drill.
    			<br/> Use following credentials to login.<br/><br/>
    			username: <b>%s</b>
    			password: <b>%s</b>
    			<br/><br/>
    			Thank you
				""";
		content = String.format(content, team.getName(), team.getEmail(),team.getPassword());
		System.out.println("content:"+content);
		mailDTO.setContent(content);
		send(mailDTO);
		//

	}
	

	// Method to read HTML file as a String
	private static String readContentFromFile(String fileName) {
		StringBuffer contents = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				reader.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return contents.toString();
	}
	
	
}
