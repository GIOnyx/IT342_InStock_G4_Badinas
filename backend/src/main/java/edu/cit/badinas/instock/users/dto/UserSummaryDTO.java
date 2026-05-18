package edu.cit.badinas.instock.users.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSummaryDTO {

    private Long id;
    private String fullName;
    private String email;
    private String role;
}
