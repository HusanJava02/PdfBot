package com.google.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChannelTg {
    private Integer id;
    private Long chatId;
    private String channelUsername;
    private boolean active;

}
