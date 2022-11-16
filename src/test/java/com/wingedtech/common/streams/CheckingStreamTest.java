package com.wingedtech.common.streams;

import com.wingedtech.common.streams.check.CheckingMessage;
import com.wingedtech.common.streams.check.CheckingStream;
import com.wingedtech.common.streams.check.CheckingStreamConfiguration;
import com.wingedtech.common.streams.check.CheckingStreamService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.binder.MessageCollectorAutoConfiguration;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CheckingStreamConfiguration.class, TestSupportBinderAutoConfiguration.class, MessageCollectorAutoConfiguration.class})
@ActiveProfiles("checking-streams")
@Disabled("暂时无法运行, 稍后再修复")
public class CheckingStreamTest {

    @Autowired
    private CheckingStreamService checkingStreamService;

    @Autowired
    private MessageCollector messageCollector;

    @Test
    @SuppressWarnings("unchecked")
    public void testCheckingMessage() {
        final String content = "content";
        final CheckingMessage message = checkingStreamService.report(content);
        Message<CheckingMessage> received = (Message<CheckingMessage>) messageCollector.forChannel(checkingStreamService.getOutputChannel()).poll();
        System.out.println(received);
        final CheckingMessage payload = received.getPayload();
        assertThat(payload).isNotNull();
        assertThat(payload.getMessage()).isEqualTo(content);
    }

    @SpringBootApplication
    @EnableBinding(CheckingStream.class)
    public static class TestApp {
        @Autowired
        private CheckingStream channels;

        /**
         * Transformer - 自动将INPUT与OUTPUT channel对接
         * @param in
         * @return
         */
//        @Transformer(inputChannel = CheckingStream.OUTPUT, outputChannel = CheckingStream.INPUT)
//        public CheckingMessage transform(CheckingMessage in) {
//            return in;
//        }
    }
}
