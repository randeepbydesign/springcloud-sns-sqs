package com.ahg.example.springcloudsnssqs.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ashish
 * @since 2/2/19
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class JobDTO implements Serializable {
    private static final long serialVersionUID = -8948131589892228238L;

    @NotNull
    private String sourceFilePath;
    @NotNull
    private String destinationFilePath;
}
