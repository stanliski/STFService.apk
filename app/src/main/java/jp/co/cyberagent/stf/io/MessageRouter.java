package jp.co.cyberagent.stf.io;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashMap;
import java.util.Map;

import jp.co.cyberagent.stf.proto.Wire;
import jp.co.cyberagent.stf.query.AbstractResponder;

public class MessageRouter {
    private Map<Wire.RequestType, AbstractResponder> responders = new HashMap<Wire.RequestType, AbstractResponder>();
    private MessageWriter writer;

    public MessageRouter(MessageWriter writer) {
        this.writer = writer;
    }

    public void register(Wire.RequestType type, AbstractResponder responder) {
        responders.put(type, responder);
    }

    public boolean route(Wire.RequestEnvelope envelope) throws InvalidProtocolBufferException {
        AbstractResponder responder = responders.get(envelope.getType());

        if (responder != null) {
            writer.write(responder.respond(envelope));
            return true;
        }

        return false;
    }

    public void cleanup() {
        for (AbstractResponder responder : responders.values()) {
            responder.cleanup();
        }
    }
}
