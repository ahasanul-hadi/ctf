package com.cirt.ctf.email;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MailDTO {
	private String from;
	private String to;
	private String content;
	private String subject;
}
