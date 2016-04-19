package net.sorted.exchange.messages;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.sorted.exchange.domain.Side.*;
import static net.sorted.exchange.domain.OrderType.*;

public class JsonConverterTest {

    private JsonConverter jsonConverter;

    @Before
    public void before() {
        jsonConverter = new JsonConverter();
    }

//    @Test
//    public void testJsonToExchengeOrder() {
//        String json =
//                "{       \"orderId\": \"0\", "+
//                        "\"clientId\": \"fred\", "+
//                        "\"correlationId\": \"1234\", "+
//                        "\"instrument\": \"AMZN\", "+
//                        "\"quantity\": 999, "+
//                        "\"price\": \"100.12\", " +
//                        "\"side\": \"BUY\", " +
//                        "\"type\": \"LIMIT\", " +
//                        "\"state\": \"unsubmitted\" " +
//                "}";
//
//        ExchangeOrderOld e = jsonConverter.exchangeOrderFromJson(json);
//        assertNotNull(e);
//        assertEquals("0", e.getOrderId());
//        assertEquals("fred", e.getClientId());
//        assertEquals("1234", e.getCorrelationId());
//        assertEquals("AMZN", e.getInstrument());
//        assertEquals(999, e.getQuantity());
//        assertEquals("100.12", e.getPrice());
//        assertEquals(BUY, e.getSide());
//        assertEquals(LIMIT, e.getType());
//        assertEquals(ExchangeOrderOld.State.unsubmitted, e.getState());
//    }
//
//    @Test
//    public void testJsonToExchengeOrderWithMissingFields() {
//        String json =
//                "{       "+
//                        "\"clientId\": \"fred\", "+
//                        "\"correlationId\": \"1234\", "+
//                        "\"instrument\": \"AMZN\", "+
//                        "\"quantity\": 999, "+
//                        "\"price\": \"100.12\", " +
//                        "\"side\": \"BUY\", " +
//                        "\"type\": \"LIMIT\", " +
//                        "\"state\": \"unsubmitted\" " +
//                        "}";
//
//        ExchangeOrderOld e = jsonConverter.exchangeOrderFromJson(json);
//        assertNotNull(e);
//        assertNull(e.getOrderId());
//        assertEquals("fred", e.getClientId());
//        assertEquals("1234", e.getCorrelationId());
//        assertEquals("AMZN", e.getInstrument());
//        assertEquals(999, e.getQuantity());
//        assertEquals("100.12", e.getPrice());
//        assertEquals(BUY, e.getSide());
//        assertEquals(LIMIT, e.getType());
//        assertEquals(ExchangeOrderOld.State.unsubmitted, e.getState());
//    }

//    @Test
//    public void testExchangeOrderToJason() {
//        ExchangeOrderOld order = new ExchangeOrderOld("0", "fred", "1234", "AMZN", 999, "100.12", BUY, LIMIT, ExchangeOrderOld.State.unsubmitted);
//        String json = jsonConverter.exchangeOrderToJson(order);
//        assertTrue(json.indexOf("\"orderId\":\"0\",") >= 0);
//        assertTrue(json.indexOf("\"clientId\":\"fred\"") >= 0);
//        assertTrue(json.indexOf("\"correlationId\":\"1234\"") >= 0);
//        assertTrue(json.indexOf("\"instrument\":\"AMZN\"") >= 0);
//        assertTrue(json.indexOf("\"quantity\":999") >= 0);
//        assertTrue(json.indexOf("\"price\":\"100.12\"") >= 0);
//        assertTrue(json.indexOf("\"side\":BUY") >= 0);
//        assertTrue(json.indexOf("\"type\":LIMIT") >= 0);
//        assertTrue(json.indexOf("\"state\":unsubmitted") >= 0);
//    }
}
