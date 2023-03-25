package com.kubaokleja.springbootangular.email;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "receiver", "subject" })})
class PendingEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false)
    private String receiver;
    @Column(nullable = false)
    private String subject;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String contentType;

    EmailDTO toDTO(PendingEmail email) {
        return EmailDTO.builder()
                .receiver(email.getReceiver())
                .subject(email.getSubject())
                .email(email.getEmail())
                .contentType(email.getContentType())
                .build();
    }
}
