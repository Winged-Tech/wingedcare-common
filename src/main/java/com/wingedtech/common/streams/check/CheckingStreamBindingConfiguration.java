package com.wingedtech.common.streams.check;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(value = {CheckingStream.class})
public class CheckingStreamBindingConfiguration {
}
