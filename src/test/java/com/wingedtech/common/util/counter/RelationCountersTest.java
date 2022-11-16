package com.wingedtech.common.util.counter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wingedtech.common.io.ResourceUtils;
import lombok.Data;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * @author zhangyp
 */
public class RelationCountersTest {

    @Test
    public void test() throws IOException {

        // 获取测试数据
        List<UserTest> userTestData = getUserTestData();

        // 创建失败关系链
        final Map<String, String> failureRelations = new HashMap<>();
        // 订单失败 导致 合同失败
        failureRelations.put("order", "contract");
        // 合同失败 导致 用户失败
        failureRelations.put("contract", "user");

        // 创建关系计数器
        SuccessFailRelateCounters counter = new SuccessFailRelateCounters(failureRelations);

        // 开始处理
        handleUser(counter, userTestData);

        // 结果判断
        SuccessFailCounter user = counter.getCounter("user");
        assertThat(user.getSuccessCount()).isEqualTo(1);
        SuccessFailCounter contract = counter.getCounter("contract");
        assertThat(contract.getSuccessCount()).isEqualTo(1);
        SuccessFailCounter order = counter.getCounter("order");
        assertThat(order.getSuccessCount()).isEqualTo(3);

        // 最终结果
        System.out.println(user.getFormattedResults("用户"));
        System.out.println(contract.getFormattedResults("合同"));
        System.out.println(order.getFormattedResults("订单"));
    }

    /**
     * 处理用户
     *
     * @param counter
     * @param userTestData
     */
    private void handleUser(SuccessFailRelateCounters counter, List<UserTest> userTestData) {
        for (UserTest user : userTestData) {
            // 开始处理用户
            counter.ready("user");

            // 处理合同
            this.handleContract(user.getContracts(), counter);

            // 用户处理成功/失败
            if (user.getSuccess()) {
                counter.success("user");
            } else {
                counter.failure("user");
            }
        }
    }

    /**
     * 处理合同
     *
     * @param contracts
     * @param counter
     */
    private void handleContract(List<Contract> contracts, SuccessFailRelateCounters counter) {

        for (Contract contract : contracts) {
            // 开始处理合同
            counter.ready("contract");

            // 处理订单
            this.handleOrder(contract.getOrders(), counter);

            // 合同处理成功/失败
            if (contract.getSuccess()) {
                counter.success("contract");
            } else {
                counter.failure("contract");
            }
        }

    }

    /**
     * 处理订单
     *
     * @param orders
     * @param counter
     */
    private void handleOrder(List<Order> orders, SuccessFailRelateCounters counter) {

        for (Order order : orders) {
            // 开始处理订单
            counter.ready("order");

            // 订单处理成功/失败
            if (order.getSuccess()) {
                counter.success("order");
            } else {
                counter.failure("order");
            }
        }

    }

    private List<UserTest> getUserTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream = ResourceUtils.getResourceFileStream("config/relation-test.json");
        List<UserTest> userTests = mapper.readValue(inputStream, new TypeReference<List<UserTest>>() {
        });
        inputStream.close();
        return userTests;
    }
}

@Data
class UserTest {
    private String userName;
    private Boolean success;
    private List<Contract> contracts;
}

@Data
class Contract {
    private String contract;
    private Boolean success;
    private List<Order> orders;
}

@Data
class Order {
    private String order;
    private Boolean success;
}
