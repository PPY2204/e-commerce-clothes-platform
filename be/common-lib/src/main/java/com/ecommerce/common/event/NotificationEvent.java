package com.ecommerce.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String email;
    private String firstName;
    private String content; // Code or message body
    private String type;    // OTP_REGISTRATION, FORGOT_PASSWORD, etc.
}
