package com.kubaokleja.springbootangular.user;

import lombok.*;

@Getter
@Builder
class UploadUserResultDTO {
    private Integer row;
    private String message;
}
