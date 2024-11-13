package com.qa.api.tests.requestPojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor //for constructor with no arguments
@AllArgsConstructor //for constructor with all arguments
@Data //for setters getters
@Builder //for builder pattern
public class Users {
    private String name;
    private String email;
    private String gender;
    private String status;

}
