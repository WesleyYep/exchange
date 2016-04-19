package net.sorted.exchange.messages;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sorted.exchange.domain.OrderType;
import net.sorted.exchange.domain.Side;
import net.sorted.exchange.domain.Trade;
import net.sorted.exchange.orderbook.*;
import net.sorted.exchange.orderbook.OrderBookSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonConverter {

    private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd-MM-yyyy");
    private Logger log = LogManager.getLogger(JsonConverter.class);

//    public ExchangeOrderOld exchangeOrderFromJson(String json) {
//        JSONParser parser = new JSONParser();
//        ExchangeOrderOld order = null;
//        try {
//            Object parsed = parser.parse(json);
//            if (parsed instanceof JSONObject) {
//                JSONObject o = (JSONObject) parsed;
//                String clientId = (String)o.get("clientId");
//                String orderId = (String)o.get("orderId");
//                String correlationId = (String)o.get("correlationId");
//                String instrument = (String)o.get("instrument");
//                String price = (String)o.get("price");
//                Object qty = o.get("quantity");
//                Long quantity = (qty instanceof Long) ? (Long) qty : Long.parseLong(""+qty);
//                Side side = (o.get("side") != null) ? Side.valueOf((String)o.get("side")) : null;
//                OrderType type = (o.get("type") != null) ? OrderType.valueOf((String)o.get("type")) : null;
//                ExchangeOrderOld.State state =  (o.get("state") != null) ? ExchangeOrderOld.State.valueOf((String)o.get("state")) : null;
//
//                order = new ExchangeOrderOld(
//                        orderId,
//                        clientId,
//                        correlationId,
//                        instrument,
//                        quantity,
//                        price,
//                        side,
//                        type,
//                        state );
//
//            } else {
//                throw new RuntimeException("Cannot parse ExchangeOrderOld message '"+json+"'");
//            }
//        } catch (ParseException e) {
//            throw new RuntimeException("Cannot parse ExchangeOrderOld message '"+json+"'", e);
//        }
//
//
//        return order;
//    }
//
//    public String exchangeOrderToJson(ExchangeOrderOld order) {
//        JSONObject obj = new JSONObject();
//
//
//        addIfNotNull(obj, "orderId", order.getOrderId());
//        addIfNotNull(obj, "clientId", order.getClientId());
//        addIfNotNull(obj, "correlationId", order.getCorrelationId());
//        addIfNotNull(obj, "instrument", order.getInstrument());
//        addIfNotNull(obj, "quantity", order.getQuantity());
//        addIfNotNull(obj, "price", order.getPrice());
//        addIfNotNull(obj, "side", order.getSide());
//        addIfNotNull(obj, "type", order.getType());
//        addIfNotNull(obj, "state", order.getState());
//
//        return jsonObjToString(obj);
//    }
//
//
//    private void addIfNotNull(JSONObject obj, String key, Object value) {
//        if (value != null) {
//            if (value instanceof Long || value instanceof Integer) {
//                obj.put(key, value);
//            } else {
//                obj.put(key, value.toString());
//            }
//        }
//    }
//
//    public String publicTradeToJson(ExchangeMessage.PublicTrade trade) {
//        JSONObject obj = new JSONObject();
//
//        obj.put("instrumentId", trade.getInstrumentId());
//        obj.put("quantity", trade.getQuantity());
//        obj.put("price", trade.getPrice());
//        obj.put("tradeDate", dateFormat.print(trade.getTradeDateMillisSinceEpoch()));
//
//        return jsonObjToString(obj);
//    }
//
//    public String privateTradeToJson(ExchangeMessage.PrivateTrade trade) {
//        JSONObject obj = new JSONObject();
//
//        obj.put("tradeId", trade.getTradeId());
//        obj.put("orderId", trade.getOrderId());
//        obj.put("clientId", trade.getClientId());
//        obj.put("instrumentId", trade.getInstrumentId());
//        obj.put("quantity", trade.getQuantity());
//        obj.put("price", trade.getPrice());
//        obj.put("tradeDate", dateFormat.print(trade.getTradeDateMillisSinceEpoch()));
//
//        return jsonObjToString(obj);
//    }

//    public Trade jsonToTrade(String json) {
//
//        JSONParser parser = new JSONParser();
//        Trade trade = null;
//        try {
//            Object parsed = parser.parse(json);
//            if (parsed instanceof JSONObject) {
//                JSONObject o = (JSONObject) parsed;
//                String clientId = (String)o.get("clientId");
//                String tradeId = (String)o.get("tradeId");
//                String orderId = (String)o.get("orderId");
//                String instrumentId = (String)o.get("instrumentId");
//                long quantity = (Long)o.get("quantity");
//                Double price = (Double)o.get("price");
//                String side = (String)o.get("side");
//                String tradeDate = (String)o.get("tradeDate");
//
//                Side s = (side != null) ? Side.valueOf(side): null;
//                log.debug("clientId={}  json={}", clientId, json);
//
//                // TODO - parse the tradeDate
//                trade = new Trade(tradeId, orderId, clientId, instrumentId, quantity, price, s, new DateTime());
//
//
//            } else {
//                throw new RuntimeException("Cannot parse Trade message ["+json+"]");
//            }
//        } catch (ParseException e) {
//            throw new RuntimeException("Cannot parse Trade message ["+json+"]", e);
//        }
//
//        return trade;
//    }

    public String snapshotToJson(OrderBookSnapshot snapshot) {
        JSONObject json = new JSONObject();

        /* Each side is an array of price/volume in level order (for buy level 1 is the highest price, for sell level 1 is the lowest price)
        "INSTR": {
           buy: [
            { price: 1.1, volume: 99 },
            { price: 1.0, volume: 50 }
           ],
           sell: [
            { price: 1.2, volume: 100 },
            { price: 1.3, volume: 200 }
          ]
        }
     */

        JSONArray buys = new JSONArray();
        for (OrderBookLevelSnapshot level : snapshot.getBuyLevels()) {
            buys.add(orderLevelToJsonObject(level));
        }

        JSONArray sells = new JSONArray();
        for (OrderBookLevelSnapshot level : snapshot.getSellLevels()) {
            sells.add(orderLevelToJsonObject(level));
        }

        JSONObject instr = new JSONObject();
        instr.put("buy", buys);
        instr.put("sell", sells);

        json.put(snapshot.getInstrumentId(), instr);

        return jsonObjToString(json);

    }

    private JSONObject orderLevelToJsonObject(OrderBookLevelSnapshot level) {
        JSONObject json = new JSONObject();

        json.put("quantity", level.getQuantity());
        json.put("price", level.getPrice());

        return json;
    }


    private String jsonObjToString(JSONObject json) {
        StringWriter out = new StringWriter();
        try {
            json.writeJSONString(out);
        } catch (IOException e) {
            // wont happen - its a StringWriter so no actual IO
        }

        return out.toString();
    }

    public String getInstrumentIdFromSnapshotJson(String json) {
        String instrumentId = null;
        JSONParser parser = new JSONParser();
        try {
            Object parsed = parser.parse(json);

            if (parsed instanceof JSONObject) {
                JSONObject o = (JSONObject) parsed;
                instrumentId = (String) o.keySet().iterator().next();
             //   instrumentId = (String) o.get("instrumentId");
            }
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse snapshot message ["+json+"]", e);
        }
        return instrumentId;
    }



    public static final void main(String[] args) throws IOException {

//        JSONObject json = new JSONObject();
//
//        LinkedList<String> sells = new LinkedList<>();
//        sells.add("one");
//        sells.add("two");
//        sells.add("three");
//
//
//        JSONArray jarray = new JSONArray();
//        JSONObject j1 = new JSONObject();
//        j1.put("quantity", 1000);
//        j1.put("price", "99.99");
//
//        JSONObject j2 = new JSONObject();
//        j2.put("quantity", 1001);
//        j2.put("price", "99.99");
//
//        JSONObject j3 = new JSONObject();
//        j3.put("quantity", 1002);
//        j3.put("price", "99.99");
//
//
//
//        jarray.add(j1);
//        jarray.add(j2);
//        jarray.add(j3);
//
//
//        String array = JSONValue.toJSONString(sells);
//        System.out.println("Array=" + array);
//        json.put("sell", jarray);
//
//        StringWriter out = new StringWriter();
//        json.writeJSONString(out);
//        System.out.println("> " + out.toString());

        List<OrderBookLevelSnapshot> b = new ArrayList<>();
        b.add(new OrderBookLevelSnapshot(1.1, 999));
        List<OrderBookLevelSnapshot> s = new ArrayList<>();
        b.add(new OrderBookLevelSnapshot(2.2, 999));

        OrderBookSnapshot snap = new OrderBookSnapshot("AMZN", b, s);

        JsonConverter conv = new JsonConverter();
        String oj = conv.snapshotToJson(snap);
        System.out.println(oj);
    }
}
