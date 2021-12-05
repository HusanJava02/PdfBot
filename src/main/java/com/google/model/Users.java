package com.google.model;

import com.google.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users {
    private Integer id;
    private String userName;
    private Long chatId;
    private String botState;
    private Language languageUser;
}
