package com.dilaverdemirel.simplespringbootk8sapp.dto;

/**
 * @author dilaverd - 12.11.2019
 */
public class SalutationDTO {
    private String message;

    public SalutationDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
