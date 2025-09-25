package com.leonardo.notificacao.business;

import com.leonardo.notificacao.dto.TarefasDTO;
import com.leonardo.notificacao.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${envio.email.remetente}")
    public String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void enviaEmail(TarefasDTO dto){
        try {
            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente)); //remetente
            mimeMessageHelper.setTo(InternetAddress.parse(dto.getEmailUsuario())); //destinatario
            mimeMessageHelper.setSubject("Notificação de Tarefas"); //assunto

            Context context = new Context(); //utiliza o objeto para setar as variaveis do thymeleaf
            context.setVariable("nomeTarefa", dto.getNomeTarefa());//buscando parametros do agendador
            context.setVariable("dataEvento", dto.getDataEvento());//buscando parametros do agendador
            context.setVariable("descricao", dto.getDescricao());//buscando parametros do agendador
            String template = templateEngine.process("notificacao", context);
            mimeMessageHelper.setText(template, true);// update/set no templete como texto do email
            javaMailSender.send(mensagem);//envia o email

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar o email ", e.getCause());
        }
    }


}
