package net.sorted.exchange.messages;

import java.io.IOException;
import java.io.StringWriter;
import net.sorted.exchange.OrderType;
import net.sorted.exchange.Side;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonConverter {

    public ExchangeOrder exchangeOrderFromJson(String json) {
        JSONParser parser = new JSONParser();
        ExchangeOrder order = null;
        try {
            Object parsed = parser.parse(json);
            if (parsed instanceof JSONObject) {
                JSONObject o = (JSONObject) parsed;
                String clientId = (String)o.get("clientId");
                String orderId = (String)o.get("orderId");
                String correlationId = (String)o.get("correlationId");

                order = new ExchangeOrder(
                        (String)o.get("orderId"),
                        (String)o.get("clientId"),
                        (String)o.get("correlationId"),
                        (String)o.get("instrument"),
                        (Long)o.get("quantity"),
                        (String)o.get("price"),
                        (o.get("side") != null) ? Side.valueOf((String)o.get("side")) : null,
                        (o.get("type") != null) ? OrderType.valueOf((String)o.get("type")) : null,
                        (o.get("state") != null) ? ExchangeOrder.State.valueOf((String)o.get("state")) : null
                );

            } else {
                throw new RuntimeException("Cannot parse ExchangeOrder message ["+json+"]");
            }
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse ExchangeOrder message ["+json+"]", e);
        }


        return order;
    }

    public String exchangeOrderToJson(ExchangeOrder order) {
        JSONObject obj = new JSONObject();

        obj.put("orderId", order.getOrderId());
        obj.put("clientId", order.getClientId());
        obj.put("correlationId", order.getCorrelationId());
        obj.put("instrument", order.getInstrument());
        obj.put("quantity", order.getQuantity());
        obj.put("price", ""+order.getPrice());
        obj.put("side", order.getSide());
        obj.put("type", order.getType());
        obj.put("state", order.getState());

        StringWriter out = new StringWriter();
        try {
            obj.writeJSONString(out);
        } catch (IOException e) {
            // wont happen - its a StringWriter so no actual IO
        }

        return out.toString();
    }

    public static final void main(String[] args) {
        String order = "{ \"orderId\": \"0\", \"clientId\": \"fred\", \"instrument\": \"AMZN\", \"quantity\": 999, \"price\": \"100.12\" }";

        JsonConverter converter = new JsonConverter();
        ExchangeOrder eo = converter.exchangeOrderFromJson(order);
        System.out.println(eo);
    }
}
