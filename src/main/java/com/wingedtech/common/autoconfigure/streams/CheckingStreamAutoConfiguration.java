package com.wingedtech.common.autoconfigure.streams;

import com.wingedtech.common.streams.check.CheckingStreamConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CheckingStreamConfiguration.class)
public class CheckingStreamAutoConfiguration {
}
