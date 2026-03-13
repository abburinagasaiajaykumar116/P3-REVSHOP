package org.revshop.payment.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient("rzp_test_SOIqeyGRqoWvn0", "FJh2fkREYys2im0MsgQnEcBI");
    }
}