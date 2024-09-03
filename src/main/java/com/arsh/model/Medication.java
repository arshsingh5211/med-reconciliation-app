package com.arsh.model;

import com.arsh.validation.BrandOrGeneric;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@BrandOrGeneric
public class Medication {
    private int medicationId;
    private String brandName;
    private String genericName;

    @NotNull(message = "Drug class cannot be null")
    private String drugClass;

    @NotNull(message = "Sub-category cannot be null")
    @Size(max = 50, message = "Sub-category must be less than or equal to 50 characters")
    private String subCategory;

}
